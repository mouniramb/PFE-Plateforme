import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { PaiementService } from '../../services/paiement.service';
import { Paiement, PaiementStatut } from '../../models/financial.model';

type FormationStatut = 'VALIDE' | 'PARTIEL' | 'EN_ATTENTE' | 'REJETE';

interface FormationSummary {
  formationId:    number;
  formationTitre: string;
  modePaiement:   string;
  totalPaye:      number;
  totalEnAttente: number;
  totalBrut:      number;
  restant:        number;
  paiements:      Paiement[];
  totalTranches:  number;
  tranchePayee:   number;
  statut:         FormationStatut;
}

@Component({
  selector: 'app-apprenant-paiements',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './apprenant-paiements.component.html',
  styleUrls: ['./apprenant-paiements.component.scss']
})
export class ApprenantPaiementsComponent implements OnInit {

  paiements:   Paiement[]          = [];
  formations:  FormationSummary[]   = [];
  isLoading    = false;
  errorMessage = '';
  expanded     = new Set<number>();

  currentUser = this.authService.getCurrentUser();

  get totalPaye(): number {
    return this.paiements.filter(p => p.statut === 'VALIDE').reduce((s, p) => s + p.montantNet, 0);
  }

  get totalEnAttente(): number {
    return this.paiements.filter(p => p.statut === 'EN_ATTENTE').reduce((s, p) => s + p.montantNet, 0);
  }

  get nbFormationsPayees(): number {
    return this.formations.filter(f => f.statut === 'VALIDE').length;
  }

  constructor(private authService: AuthService, private paiementService: PaiementService) {}

  ngOnInit(): void { this.loadPaiements(); }

  loadPaiements(): void {
    this.isLoading = true;
    this.paiementService.getMesPaiements().subscribe({
      next: (data) => {
        this.paiements = data;
        this.buildFormationsSummary();
        this.isLoading = false;
      },
      error: () => { this.errorMessage = 'Erreur lors du chargement.'; this.isLoading = false; }
    });
  }

  private buildFormationsSummary(): void {
    // Grouper par formation + mode séparément pour éviter le mélange des montants
    const map = new Map<string, FormationSummary>();

    for (const p of this.paiements) {
      const key = `${p.formationId}-${p.modePaiement}`;
      if (!map.has(key)) {
        map.set(key, {
          formationId:    p.formationId,
          formationTitre: p.formationTitre,
          modePaiement:   p.modePaiement,
          totalPaye:      0,
          totalEnAttente: 0,
          totalBrut:      0,
          restant:        0,
          paiements:      [],
          totalTranches:  0,
          tranchePayee:   0,
          statut:         'EN_ATTENTE'
        });
      }
      const s = map.get(key)!;

      // Dédupliquer les EN_ATTENTE (évite le double comptage des doublons)
      const dedupKey = this.getEnAttendeDedupKey(p);
      const alreadyCounted = s.paiements
        .filter(x => x.statut === 'EN_ATTENTE')
        .some(x => this.getEnAttendeDedupKey(x) === dedupKey);

      s.paiements.push(p);

      if (p.statut === 'VALIDE') {
        s.totalPaye += p.montantNet;
        if (p.trancheNumber) s.tranchePayee++;
      } else if (p.statut === 'EN_ATTENTE' && !alreadyCounted) {
        s.totalEnAttente += p.montantNet;
      }
      if (p.trancheNumber) s.totalTranches = Math.max(s.totalTranches, p.trancheNumber);
    }

    for (const [, s] of map) {
      s.totalBrut = s.totalPaye + s.totalEnAttente;
      s.restant   = s.totalEnAttente;
      const hasValide    = s.paiements.some(p => p.statut === 'VALIDE');
      const hasEnAttente = s.paiements.some(p => p.statut === 'EN_ATTENTE');
      const hasRejete    = s.paiements.some(p => p.statut === 'REJETE');
      if (hasRejete)                       s.statut = 'REJETE';
      else if (hasValide && !hasEnAttente)  s.statut = 'VALIDE';
      else if (hasValide && hasEnAttente)   s.statut = 'PARTIEL';
      else                                 s.statut = 'EN_ATTENTE';
    }

    this.formations = Array.from(map.values());
  }

  private getEnAttendeDedupKey(p: Paiement): string {
    if (p.modePaiement === 'TRANCHE')  return `tranche-${p.trancheNumber}`;
    if (p.modePaiement === 'SEANCE')   return `seance-${p.seanceId}`;
    if (p.modePaiement === 'ANNUAIRE') return `mois-${p.moisAnnuaire}`;
    return 'unique'; // FORMATION : un seul EN_ATTENTE compté
  }

  toggleDetails(formationId: number): void {
    if (this.expanded.has(formationId)) this.expanded.delete(formationId);
    else this.expanded.add(formationId);
  }

  isExpanded(formationId: number): boolean {
    return this.expanded.has(formationId);
  }

  progressPercent(f: FormationSummary): number {
    if (f.totalBrut === 0) return 0;
    return Math.round((f.totalPaye / f.totalBrut) * 100);
  }

  getModeLabel(mode: string): string {
    return { FORMATION: 'Annuel', TRANCHE: 'Par tranches', SEANCE: 'Par séance', ANNUAIRE: 'Mensuel' }[mode] || mode;
  }

  getModeIcon(mode: string): string {
    return { FORMATION: '💳', TRANCHE: '📅', SEANCE: '🎯', ANNUAIRE: '🔄' }[mode] || '💰';
  }

  getStatutLabel(statut: PaiementStatut): string {
    return { EN_ATTENTE: 'En attente', VALIDE: 'Validé', REJETE: 'Rejeté' }[statut] || statut;
  }

  getStatutClass(statut: PaiementStatut): string {
    return { EN_ATTENTE: 'badge--attente', VALIDE: 'badge--valide', REJETE: 'badge--rejete' }[statut] || '';
  }

  getCardStatutLabel(statut: FormationStatut): string {
    return { VALIDE: '✅ Complètement payé', PARTIEL: '⏳ Partiellement payé',
             EN_ATTENTE: '🔔 En attente', REJETE: '❌ Rejeté' }[statut] || statut;
  }

  getCardStatutClass(statut: FormationStatut): string {
    return { VALIDE: 'card-statut--valide', PARTIEL: 'card-statut--partiel',
             EN_ATTENTE: 'card-statut--attente', REJETE: 'card-statut--rejete' }[statut] || '';
  }

  logout(): void { this.authService.logout(); }
}
