import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PaiementFormateurService, PaiementFormateurResponse } from '../../services/paiement-formateur.service';

interface FormationRevenu {
  formationId:    number;
  formationTitre: string;
  totalRecu:      number;
  nbPaiements:    number;
  modePaiement:   string;
  paiements:      PaiementFormateurResponse[];
}

@Component({
  selector: 'app-formateur-revenus',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './formateur-revenus.component.html',
  styleUrls: ['./formateur-revenus.component.scss']
})
export class FormateurRevenusComponent implements OnInit {

  paiements:   PaiementFormateurResponse[] = [];
  formations:  FormationRevenu[] = [];
  isLoading    = false;
  errorMessage = '';

  currentUser = this.authService.getCurrentUser();

  get totalRecu(): number {
    return this.paiements
      .filter(p => p.statut === 'VALIDE')
      .reduce((s, p) => s + p.montant, 0);
  }

  get totalEnAttente(): number {
    return this.paiements
      .filter(p => p.statut === 'EN_ATTENTE')
      .reduce((s, p) => s + p.montant, 0);
  }

  constructor(
    private authService: AuthService,
    private paiementFormateurService: PaiementFormateurService
  ) {}

  ngOnInit(): void {
    this.loadRevenus();
  }

  loadRevenus(): void {
    this.isLoading = true;
    this.paiementFormateurService.getMesRevenus().subscribe({
      next: (data) => {
        this.paiements = data ?? [];
        this.buildFormationsSummary();
        this.isLoading = false;
        this.errorMessage = '';
      },
      error: (err) => {
        this.isLoading = false;
        // Si 404 ou tableau vide → pas d'erreur, juste aucun revenu
        if (err.status === 404 || err.status === 204) {
          this.paiements = [];
          this.formations = [];
        } else {
          this.errorMessage = `Erreur ${err.status} — Veuillez redémarrer Spring Boot pour créer la table paiements_formateurs.`;
        }
      }
    });
  }

  private buildFormationsSummary(): void {
    const map = new Map<number, FormationRevenu>();
    for (const p of this.paiements) {
      if (!map.has(p.formationId)) {
        map.set(p.formationId, {
          formationId:    p.formationId,
          formationTitre: p.formationTitre,
          totalRecu:      0,
          nbPaiements:    0,
          modePaiement:   p.modePaiement,
          paiements:      []
        });
      }
      const f = map.get(p.formationId)!;
      f.paiements.push(p);
      f.nbPaiements++;
      if (p.statut === 'VALIDE') f.totalRecu += p.montant;
    }
    this.formations = Array.from(map.values());
  }

  getModeLabel(mode: string): string {
    const labels: Record<string, string> = {
      FORMATION: 'Annuel', TRANCHE: 'Par tranches',
      SEANCE: 'Par séance', ANNUAIRE: 'Mensuel'
    };
    return labels[mode] || mode;
  }

  getStatutClass(statut: string): string {
    return { VALIDE: 'badge--valide', EN_ATTENTE: 'badge--attente', REJETE: 'badge--rejete' }[statut] || '';
  }

  logout(): void { this.authService.logout(); }
}
