import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Inscription, PageResponse } from '../../models/formation.model';
import { AuthService } from '../../services/auth.service';
import { InscriptionService } from '../../services/inscription.service';
import { mapHttpErrorMessage } from '../../utils/http-error.util';

@Component({
  selector: 'app-admin-inscriptions',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin-inscriptions.component.html',
  styleUrls: ['./admin-inscriptions.component.scss']
})
export class AdminInscriptionsComponent implements OnInit {
  inscriptions: Inscription[] = [];
  isLoading = false;

  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  acceptConfirmId: number | null = null;
  rejectTarget: Inscription | null = null;
  rejectReason = '';
  rejectLoading = false;

  successMessage = '';
  errorMessage = '';

  readonly currentUser = this.authService.getCurrentUser();

  constructor(
    private inscriptionService: InscriptionService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadInscriptions();
  }

  loadInscriptions(): void {
    this.isLoading = true;
    this.inscriptionService.getInscriptionsEnAttente(this.currentPage, this.pageSize).subscribe({
      next: (response: PageResponse<Inscription>) => {
        this.inscriptions = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.isLoading = false;
      },
      error: (err: unknown) => {
        this.isLoading = false;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors du chargement des inscriptions.'));
      }
    });
  }

  confirmAccept(id: number): void {
    this.acceptConfirmId = id;
  }

  cancelAccept(): void {
    this.acceptConfirmId = null;
  }

  accepter(id: number): void {
    this.inscriptionService.accepterInscription(id).subscribe({
      next: () => {
        this.acceptConfirmId = null;
        this.showSuccess('Inscription acceptée avec succès.');
        this.loadInscriptions();
      },
      error: (err: unknown) => {
        this.acceptConfirmId = null;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors de l’acceptation.'));
      }
    });
  }

  openRejectModal(inscription: Inscription): void {
    this.rejectTarget = inscription;
    this.rejectReason = '';
  }

  closeRejectModal(): void {
    this.rejectTarget = null;
    this.rejectReason = '';
    this.rejectLoading = false;
  }

  reject(): void {
    if (!this.rejectTarget || !this.rejectReason.trim()) {
      return;
    }

    this.rejectLoading = true;
    this.inscriptionService.rejeterInscription(this.rejectTarget.id, this.rejectReason.trim()).subscribe({
      next: () => {
        this.closeRejectModal();
        this.showSuccess('Inscription rejetée avec succès.');
        this.loadInscriptions();
      },
      error: (err: unknown) => {
        this.rejectLoading = false;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors du rejet de l’inscription.'));
      }
    });
  }

  openFormation(id: number): void {
    this.router.navigate(['/formations', id]);
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages || page === this.currentPage) {
      return;
    }

    this.currentPage = page;
    this.loadInscriptions();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  logout(): void {
    this.authService.logout();
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
