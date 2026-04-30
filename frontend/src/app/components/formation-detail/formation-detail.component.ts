import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  Formation,
  Inscription,
  StatutFormation
} from '../../models/formation.model';
import { AuthService } from '../../services/auth.service';
import { FormationService } from '../../services/formation.service';
import { InscriptionService } from '../../services/inscription.service';
import { mapHttpErrorMessage } from '../../utils/http-error.util';

@Component({
  selector: 'app-formation-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './formation-detail.component.html',
  styleUrls: ['./formation-detail.component.scss']
})
export class FormationDetailComponent implements OnInit {
  formation: Formation | null = null;
  inscritsAcceptes: Inscription[] = [];

  isLoading = false;
  isInscriptionLoading = false;

  showInscriptionModal = false;

  successMessage = '';
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formationService: FormationService,
    private inscriptionService: InscriptionService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      this.router.navigate(['/catalogue']);
      return;
    }

    this.loadFormation(Number(idParam));
  }

  loadFormation(id: number): void {
    this.isLoading = true;
    this.formationService.getFormation(id).subscribe({
      next: (formation: Formation) => {
        this.formation = formation;
        this.isLoading = false;

        if (this.isAdmin() || this.isFormateur()) {
          this.loadInscritsAcceptes(formation.id);
        }
      },
      error: (err: unknown) => {
        this.isLoading = false;
        this.showError(mapHttpErrorMessage(err, 'Impossible de charger la formation.'));
      }
    });
  }

  loadInscritsAcceptes(formationId: number): void {
    this.inscriptionService.getInscritsAcceptes(formationId).subscribe({
      next: (inscriptions: Inscription[]) => {
        this.inscritsAcceptes = inscriptions;
      },
      error: (err: unknown) => {
        this.showError(mapHttpErrorMessage(err, 'Impossible de charger les inscrits acceptés.'));
      }
    });
  }

  openInscriptionModal(): void {
    this.showInscriptionModal = true;
  }

  closeInscriptionModal(): void {
    this.showInscriptionModal = false;
    this.isInscriptionLoading = false;
  }

  confirmInscription(): void {
    if (!this.formation) {
      return;
    }

    this.isInscriptionLoading = true;

    this.inscriptionService.sInscrire(this.formation.id).subscribe({
      next: () => {
        this.isInscriptionLoading = false;
        this.closeInscriptionModal();
        this.showSuccess('Demande d’inscription envoyée avec succès.');
      },
      error: (err: unknown) => {
        this.isInscriptionLoading = false;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors de l’inscription à la formation.'));
      }
    });
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  isApprenant(): boolean {
    return this.authService.isApprenant();
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  isFormateur(): boolean {
    return this.authService.isFormateur();
  }

  logout(): void {
    this.authService.logout();
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  getStatusClass(statut: StatutFormation): string {
    switch (statut) {
      case 'PLANIFIEE':
        return 'badge badge--planifiee';
      case 'EN_COURS':
        return 'badge badge--encours';
      case 'TERMINEE':
        return 'badge badge--terminee';
      default:
        return 'badge';
    }
  }

  getStatusLabel(statut: StatutFormation): string {
    switch (statut) {
      case 'PLANIFIEE':
        return 'Planifiée';
      case 'EN_COURS':
        return 'En cours';
      case 'TERMINEE':
        return 'Terminée';
      default:
        return statut;
    }
  }

  getCapacitePourcentage(): number {
    if (!this.formation || !this.formation.capaciteMax) {
      return 0;
    }

    return Math.min((this.formation.capaciteActuelle / this.formation.capaciteMax) * 100, 100);
  }

  getInscritsLink(): string {
    if (!this.formation) {
      return '/admin/formations';
    }

    if (this.isAdmin()) {
      return `/admin/formations/${this.formation.id}/inscrits`;
    }

    return `/formateur/formations/${this.formation.id}/inscrits`;
  }

  private showSuccess(message: string): void {
    this.successMessage = message;
    this.errorMessage = '';
    setTimeout(() => {
      this.successMessage = '';
    }, 5000);
  }

  private showError(message: string): void {
    this.errorMessage = message;
    this.successMessage = '';
    setTimeout(() => {
      this.errorMessage = '';
    }, 5000);
  }
}
