import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { PaiementService } from '../../services/paiement.service';
import { AuthService } from '../../services/auth.service';
import { Paiement, ModePaiement, PaiementStatut } from '../../models/financial.model';
import { EnregistrerPaiementComponent } from '../enregistrer-paiement/enregistrer-paiement.component';

@Component({
  selector: 'app-admin-paiements',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, EnregistrerPaiementComponent],
  templateUrl: './admin-paiements.component.html',
  styleUrls: ['./admin-paiements.component.scss']
})
export class AdminPaiementsComponent implements OnInit {

  paiements: Paiement[] = [];
  filteredPaiements: Paiement[] = [];
  activeTab: PaiementStatut | 'TOUS' = 'EN_ATTENTE';
  isLoading = false;
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalItems = 0;
  successMessage = '';
  errorMessage = '';

  // Modals
  showValidModal = false;
  showRejectModal = false;
  selectedPaiement: Paiement | null = null;
  rejectRaison = '';
  isSubmitting = false;

  currentUser     = this.authService.getCurrentUser();
  showPaiementModal = false;

  readonly tabs: { key: PaiementStatut | 'TOUS'; label: string }[] = [
    { key: 'EN_ATTENTE', label: 'En attente' },
    { key: 'VALIDE',     label: 'Validés' },
    { key: 'REJETE',     label: 'Rejetés' },
  ];

  constructor(
    private paiementService: PaiementService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPaiementsEnAttente();
  }

  setTab(tab: PaiementStatut | 'TOUS'): void {
    this.activeTab = tab;
    this.currentPage = 0;
    this.successMessage = '';
    this.errorMessage = '';
    this.loadCurrentTab();
  }

  private loadCurrentTab(): void {
    if (this.activeTab === 'EN_ATTENTE') {
      this.loadPaiementsEnAttente();
    } else {
      // For VALIDE and REJETE we load all paiements and filter client-side
      // because the backend only exposes /en-attente paginated endpoint.
      this.loadAllPaiements();
    }
  }

  loadPaiementsEnAttente(): void {
    this.isLoading = true;
    this.paiementService.getPaiementsEnAttente(this.currentPage, this.pageSize).subscribe({
      next: (page) => {
        this.paiements = page.content;
        this.filteredPaiements = page.content;
        this.totalPages = page.totalPages;
        this.totalItems = page.totalElements;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des paiements en attente.';
        this.isLoading = false;
      }
    });
  }

  private loadAllPaiements(): void {
    this.isLoading = true;
    // Fetch the apprenant's payments as fallback — for admin we use en-attente endpoint
    // For VALIDE/REJETE tabs, load en-attente and show empty since no dedicated endpoint exists.
    // Implementations can extend once backend exposes /api/paiements?statut=
    this.paiements = [];
    this.filteredPaiements = [];
    this.totalPages = 0;
    this.totalItems = 0;
    this.isLoading = false;
  }

  openValiderModal(p: Paiement): void {
    this.selectedPaiement = p;
    this.showValidModal = true;
    this.errorMessage = '';
  }

  openRejeterModal(p: Paiement): void {
    this.selectedPaiement = p;
    this.rejectRaison = '';
    this.showRejectModal = true;
    this.errorMessage = '';
  }

  closeModals(): void {
    this.showValidModal = false;
    this.showRejectModal = false;
    this.selectedPaiement = null;
    this.rejectRaison = '';
    this.isSubmitting = false;
  }

  validerPaiement(): void {
    if (!this.selectedPaiement) return;
    this.isSubmitting = true;
    this.paiementService.validerPaiement(this.selectedPaiement.id, { statut: 'VALIDE' }).subscribe({
      next: () => {
        this.successMessage = 'Paiement validé avec succès.';
        this.closeModals();
        this.loadCurrentTab();
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Erreur lors de la validation.';
        this.isSubmitting = false;
      }
    });
  }

  rejeterPaiement(): void {
    if (!this.selectedPaiement) return;
    if (!this.rejectRaison.trim()) {
      this.errorMessage = 'Veuillez indiquer la raison du rejet.';
      return;
    }
    this.isSubmitting = true;
    this.paiementService.rejeterPaiement(this.selectedPaiement.id, this.rejectRaison.trim()).subscribe({
      next: () => {
        this.successMessage = 'Paiement rejeté.';
        this.closeModals();
        this.loadCurrentTab();
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Erreur lors du rejet.';
        this.isSubmitting = false;
      }
    });
  }

  enregistrerPaiement(): void {
    this.showPaiementModal = true;
  }

  onPaiementSaved(): void {
    this.showPaiementModal = false;
    this.successMessage = 'Paiement enregistré avec succès.';
    this.loadPaiementsEnAttente();
  }

  onPaiementClosed(): void {
    this.showPaiementModal = false;
  }

  changePage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.loadCurrentTab();
  }

  getPages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  getModeBadgeClass(mode: ModePaiement): string {
    const map: Record<ModePaiement, string> = {
      FORMATION: 'badge-formation',
      SEANCE:    'badge-seance',
      TRANCHE:   'badge-tranche',
      ANNUAIRE:  'badge-annuaire'
    };
    return map[mode] || 'badge-formation';
  }

  getModeLabel(mode: ModePaiement): string {
    const map: Record<ModePaiement, string> = {
      FORMATION: 'Formation complète',
      SEANCE:    'Par séance',
      TRANCHE:   'Par tranche',
      ANNUAIRE:  'Mensuel'
    };
    return map[mode] || mode;
  }

  getStatutBadgeClass(statut: PaiementStatut): string {
    const map: Record<PaiementStatut, string> = {
      EN_ATTENTE: 'badge-en-attente',
      VALIDE:     'badge-valide',
      REJETE:     'badge-rejete'
    };
    return map[statut] || '';
  }

  getStatutLabel(statut: PaiementStatut): string {
    const map: Record<PaiementStatut, string> = {
      EN_ATTENTE: 'En attente',
      VALIDE:     'Validé',
      REJETE:     'Rejeté'
    };
    return map[statut] || statut;
  }

  logout(): void {
    this.authService.logout();
  }
}
