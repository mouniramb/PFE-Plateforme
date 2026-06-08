import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PaiementService } from '../../services/paiement.service';
import { Paiement, PaiementStatut } from '../../models/financial.model';

interface ApprenantSummary {
  apprenantId: number;
  apprenantNom: string;
  apprenantPrenom: string;
  totalPaye: number;
  totalEnAttente: number;
  formations: { formationId: number; formationTitre: string; paiements: Paiement[] }[];
}

@Component({
  selector: 'app-formateur-paiements',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './formateur-paiements.component.html',
  styleUrls: ['./formateur-paiements.component.scss']
})
export class FormateurPaiementsComponent implements OnInit {

  paiements: Paiement[] = [];
  apprenants: ApprenantSummary[] = [];
  isLoading = false;
  errorMessage = '';

  currentUser = this.authService.getCurrentUser();

  get totalValide(): number {
    return this.paiements.filter(p => p.statut === 'VALIDE').reduce((s, p) => s + p.montantNet, 0);
  }
  get totalEnAttente(): number {
    return this.paiements.filter(p => p.statut === 'EN_ATTENTE').reduce((s, p) => s + p.montantNet, 0);
  }
  get nbApprenants(): number {
    return new Set(this.paiements.map(p => p.apprenantId)).size;
  }

  constructor(
    private authService: AuthService,
    private paiementService: PaiementService
  ) {}

  ngOnInit(): void {
    const id = this.authService.getUserId();
    if (id) this.loadPaiements(id);
  }

  loadPaiements(formateurId: number): void {
    this.isLoading = true;
    this.paiementService.getPaiementsFormateur(formateurId).subscribe({
      next: (data) => {
        this.paiements = data;
        this.buildApprenantsSummary();
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des paiements.';
        this.isLoading = false;
      }
    });
  }

  private buildApprenantsSummary(): void {
    const map = new Map<number, ApprenantSummary>();
    for (const p of this.paiements) {
      if (!map.has(p.apprenantId)) {
        map.set(p.apprenantId, {
          apprenantId:     p.apprenantId,
          apprenantNom:    p.apprenantNom,
          apprenantPrenom: p.apprenantPrenom,
          totalPaye:       0,
          totalEnAttente:  0,
          formations:      []
        });
      }
      const a = map.get(p.apprenantId)!;
      if (p.statut === 'VALIDE')     a.totalPaye       += p.montantNet;
      if (p.statut === 'EN_ATTENTE') a.totalEnAttente  += p.montantNet;

      let fGroup = a.formations.find(f => f.formationId === p.formationId);
      if (!fGroup) {
        fGroup = { formationId: p.formationId, formationTitre: p.formationTitre, paiements: [] };
        a.formations.push(fGroup);
      }
      fGroup.paiements.push(p);
    }
    this.apprenants = Array.from(map.values());
  }

  getModeLabel(mode: string): string {
    const labels: Record<string, string> = {
      FORMATION: 'Annuel', TRANCHE: 'Par tranches',
      SEANCE: 'Par séance', ANNUAIRE: 'Mensuel'
    };
    return labels[mode] || mode;
  }

  getStatutClass(statut: PaiementStatut): string {
    return { EN_ATTENTE: 'badge--attente', VALIDE: 'badge--valide', REJETE: 'badge--rejete' }[statut] || '';
  }

  getStatutLabel(statut: PaiementStatut): string {
    return { EN_ATTENTE: 'En attente', VALIDE: 'Validé', REJETE: 'Rejeté' }[statut] || statut;
  }

  logout(): void { this.authService.logout(); }
}
