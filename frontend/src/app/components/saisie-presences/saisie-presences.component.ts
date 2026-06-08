import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormsModule } from '@angular/forms';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { PresenceService } from '../../services/presence.service';
import { SeanceService } from '../../services/seance.service';
import { InscriptionService } from '../../services/inscription.service';
import { Seance, PresenceStatut, PresenceBulkRequest, Presence } from '../../models/planning.model';

interface ApprenantPresence {
  apprenantId: number;
  nom: string;
  prenom: string;
  email: string;
  statut: PresenceStatut | null;
  commentaire: string;
}

@Component({
  selector: 'app-saisie-presences',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterLink],
  templateUrl: './saisie-presences.component.html',
  styleUrl: './saisie-presences.component.scss'
})
export class SaisiePresencesComponent implements OnInit {
  seance: Seance | null = null;
  apprenants: ApprenantPresence[] = [];
  seanceId: number = 0;

  successMessage = '';
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private seanceService: SeanceService,
    private presenceService: PresenceService,
    private inscriptionService: InscriptionService
  ) {}

  ngOnInit(): void {
    this.seanceId = Number(this.route.snapshot.paramMap.get('seanceId'));
    if (this.seanceId) {
      this.loadSeance();
    }
  }

  loadSeance(): void {
    this.seanceService.getSeance(this.seanceId).subscribe({
      next: (seance: Seance) => {
        this.seance = seance;
        this.loadApprenants();
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors du chargement de la séance';
      }
    });
  }

  loadApprenants(): void {
    if (!this.seance) return;

    this.inscriptionService.getInscriptionsParFormation(this.seance.formation.id, 0, 100).subscribe({
      next: (response: any) => {
        const inscriptions: any[] = response.content ?? response;
        const acceptedApprenant = inscriptions
          .filter((i: any) => i.statut === 'ACCEPTEE')
          .map((i: any) => ({
            apprenantId: i.apprenantId ?? i.apprenant?.id,
            nom: i.apprenantNom ?? i.apprenant?.nom,
            prenom: i.apprenantPrenom ?? i.apprenant?.prenom,
            email: i.apprenantEmail ?? i.apprenant?.email,
            statut: null as PresenceStatut | null,
            commentaire: ''
          }));

        this.apprenants = acceptedApprenant;
        this.loadExistingPresences();
      },
      error: (err: any) => {
        this.errorMessage = 'Erreur lors du chargement des apprenants';
      }
    });
  }

  loadExistingPresences(): void {
    this.presenceService.getPresencesParSeance(this.seanceId).subscribe({
      next: (presences: Presence[]) => {
        presences.forEach(p => {
          const apprenant = this.apprenants.find(a => a.apprenantId === p.apprenant.id);
          if (apprenant) {
            apprenant.statut = p.statut;
            apprenant.commentaire = p.commentaire || '';
          }
        });
      },
      error: (err) => {
        console.error('Erreur lors du chargement des présences existantes');
      }
    });
  }

  setAllPresent(): void {
    this.apprenants.forEach(a => {
      a.statut = 'PRESENT';
      a.commentaire = '';
    });
  }

  setAllAbsent(): void {
    this.apprenants.forEach(a => {
      a.statut = 'ABSENT';
      a.commentaire = '';
    });
  }

  setPresenceStatus(apprenant: any, statut: PresenceStatut): void {
    apprenant.statut = statut;
  }

  enregistrerPresences(): void {
    const presencesSansStatut = this.apprenants.filter(a => !a.statut);
    if (presencesSansStatut.length > 0) {
      if (!confirm(`${presencesSansStatut.length} apprenant(s) n'a/n'ont pas de statut. Confirmer l'enregistrement ?`)) {
        return;
      }
    }

    const presences = this.apprenants
      .filter(a => a.statut)
      .map(a => ({
        apprenantId: a.apprenantId,
        statut: a.statut!,
        commentaire: a.commentaire || undefined
      }));

    const bulk: PresenceBulkRequest = {
      seanceId: this.seanceId,
      presences
    };

    this.presenceService.enregistrerPresencesBulk(bulk).subscribe({
      next: () => {
        this.successMessage = 'Présences enregistrées avec succès';
        setTimeout(() => {
          this.router.navigate(['/formateur/dashboard']);
        }, 1500);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Erreur lors de l\'enregistrement';
      }
    });
  }

  getCompteurs(): { present: number; absent: number; retard: number; excuse: number } {
    return {
      present: this.apprenants.filter(a => a.statut === 'PRESENT').length,
      absent: this.apprenants.filter(a => a.statut === 'ABSENT').length,
      retard: this.apprenants.filter(a => a.statut === 'RETARD').length,
      excuse: this.apprenants.filter(a => a.statut === 'EXCUSE').length
    };
  }

  getStatutClass(statut: PresenceStatut | null): string {
    if (!statut) return '';
    switch (statut) {
      case 'PRESENT': return 'statut-present';
      case 'ABSENT': return 'statut-absent';
      case 'RETARD': return 'statut-retard';
      case 'EXCUSE': return 'statut-excuse';
      default: return '';
    }
  }

  goBack(): void {
    this.router.navigate(['/formateur/dashboard']);
  }
}
