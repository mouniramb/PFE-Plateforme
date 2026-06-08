import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PaiementService } from '../../services/paiement.service';
import { Paiement, PaiementStatut, ModePaiement } from '../../models/financial.model';

type CardStatut = 'VALIDE' | 'PARTIEL' | 'EN_ATTENTE' | 'REJETE';

interface FormationCard {
  formationId:    number;
  formationTitre: string;
  mode:           ModePaiement;
  paiements:      Paiement[];
  totalPaye:      number;
  totalEnAttente: number;
  totalBrut:      number;
  restant:        number;
  totalTranches:  number;
  tranchePayee:   number;
  statut:         CardStatut;
}

@Component({
  selector: 'app-mes-paiements-apprenant',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mes-paiements-apprenant.component.html',
  styleUrls: ['./mes-paiements-apprenant.component.scss']
})
export class MesPaiementsApprenantComponent implements OnInit {

  currentUser = this.authService.getCurrentUser();
  cards:        FormationCard[] = [];
  isLoading     = false;
  errorMessage  = '';
  expanded      = new Set<string>();

  readonly MOIS_LABELS = ['Janvier','Février','Mars','Avril','Mai','Juin',
                          'Juillet','Août','Septembre','Octobre','Novembre','Décembre'];

  get totalPaye(): number {
    return this.cards.reduce((s, c) => s + c.totalPaye, 0);
  }
  get totalEnAttente(): number {
    return this.cards.reduce((s, c) => s + c.totalEnAttente, 0);
  }
  get nbFormations(): number { return this.cards.length; }
  get nbPaiements(): number  { return this.cards.reduce((s, c) => s + c.paiements.length, 0); }

  constructor(private authService: AuthService, private paiementService: PaiementService) {}

  ngOnInit(): void { this.loadPaiements(); }

  loadPaiements(): void {
    this.isLoading = true;
    this.paiementService.getMesPaiements().subscribe({
      next: (data) => { this.cards = this.buildCards(data); this.isLoading = false; },
      error: () => { this.errorMessage = 'Erreur lors du chargement'; this.isLoading = false; }
    });
  }

  private buildCards(paiements: Paiement[]): FormationCard[] {
    const map = new Map<string, FormationCard>();
    for (const p of paiements) {
      const key = `${p.formationId}-${p.modePaiement}`;
      if (!map.has(key)) {
        map.set(key, { formationId: p.formationId, formationTitre: p.formationTitre,
          mode: p.modePaiement, paiements: [], totalPaye: 0, totalEnAttente: 0,
          totalBrut: 0, restant: 0, totalTranches: 0, tranchePayee: 0, statut: 'EN_ATTENTE' });
      }
      const c = map.get(key)!;

      // Dédupliquer EN_ATTENTE pour éviter le double comptage
      const dupKey = this.dedupKey(p);
      const alreadySeen = c.paiements.filter(x => x.statut === 'EN_ATTENTE')
                                     .some(x => this.dedupKey(x) === dupKey);
      c.paiements.push(p);

      if (p.statut === 'VALIDE') {
        c.totalPaye += p.montantNet;
        if (p.trancheNumber) c.tranchePayee++;
      } else if (p.statut === 'EN_ATTENTE' && !alreadySeen) {
        c.totalEnAttente += p.montantNet;
      }
      if (p.trancheNumber) c.totalTranches = Math.max(c.totalTranches, p.trancheNumber);
    }
    for (const [, c] of map) {
      c.totalBrut = c.totalPaye + c.totalEnAttente;
      c.restant   = c.totalEnAttente;
      const v = c.paiements.some(p => p.statut === 'VALIDE');
      const a = c.paiements.some(p => p.statut === 'EN_ATTENTE');
      const r = c.paiements.some(p => p.statut === 'REJETE');
      c.statut = r ? 'REJETE' : (v && !a) ? 'VALIDE' : (v && a) ? 'PARTIEL' : 'EN_ATTENTE';
    }
    return Array.from(map.values());
  }

  private dedupKey(p: Paiement): string {
    if (p.modePaiement === 'TRANCHE')  return `t-${p.trancheNumber}`;
    if (p.modePaiement === 'SEANCE')   return `s-${p.seanceId}`;
    if (p.modePaiement === 'ANNUAIRE') return `m-${p.moisAnnuaire}`;
    return 'f';
  }

  toggleCard(key: string): void {
    if (this.expanded.has(key)) this.expanded.delete(key);
    else this.expanded.add(key);
  }
  isExpanded(key: string): boolean { return this.expanded.has(key); }
  cardKey(c: FormationCard): string { return `${c.formationId}-${c.mode}`; }

  progressPercent(c: FormationCard): number {
    if (c.totalBrut === 0) return 0;
    return Math.round((c.totalPaye / c.totalBrut) * 100);
  }

  getModeLabel(m: string): string {
    return { FORMATION: 'Annuel', TRANCHE: 'Par tranches', SEANCE: 'Par séance', ANNUAIRE: 'Mensuel' }[m] || m;
  }
  getModeIcon(m: string): string {
    return { FORMATION: '💳', TRANCHE: '📅', SEANCE: '🎯', ANNUAIRE: '🔄' }[m] || '💰';
  }
  getCardStatutLabel(s: CardStatut): string {
    return { VALIDE:'✅ Complètement payé', PARTIEL:'⏳ Partiellement payé',
             EN_ATTENTE:'🔔 En attente', REJETE:'❌ Rejeté' }[s] || s;
  }
  getStatutIcon(s: PaiementStatut): string {
    return { VALIDE:'✅', EN_ATTENTE:'⏳', REJETE:'❌' }[s] || '•';
  }
  getStatutLabel(s: PaiementStatut): string {
    return { EN_ATTENTE:'En attente', VALIDE:'Validé', REJETE:'Rejeté' }[s] ?? s;
  }
  getStatutClass(s: PaiementStatut): string {
    return { EN_ATTENTE:'badge--attente', VALIDE:'badge--valide', REJETE:'badge--rejete' }[s] ?? '';
  }
  getMoisLabel(m: number): string { return this.MOIS_LABELS[m - 1] ?? `Mois ${m}`; }

  logout(): void { this.authService.logout(); }
}
