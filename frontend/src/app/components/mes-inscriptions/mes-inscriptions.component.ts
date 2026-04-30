import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Inscription, StatutInscription } from '../../models/formation.model';
import { AuthService } from '../../services/auth.service';
import { InscriptionService } from '../../services/inscription.service';
import { mapHttpErrorMessage } from '../../utils/http-error.util';

@Component({
  selector: 'app-mes-inscriptions',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mes-inscriptions.component.html',
  styleUrls: ['./mes-inscriptions.component.scss']
})
export class MesInscriptionsComponent implements OnInit {
  inscriptions: Inscription[] = [];
  isLoading = false;

  cancelConfirmId: number | null = null;

  successMessage = '';
  errorMessage = '';

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
    this.inscriptionService.getMesInscriptions().subscribe({
      next: (inscriptions: Inscription[]) => {
        this.inscriptions = inscriptions;
        this.isLoading = false;
      },
      error: (err: unknown) => {
        this.isLoading = false;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors du chargement de vos inscriptions.'));
      }
    });
  }

  confirmCancel(id: number): void {
    this.cancelConfirmId = id;
  }

  cancelInline(): void {
    this.cancelConfirmId = null;
  }

  annulerInscription(id: number): void {
    this.inscriptionService.annulerInscription(id).subscribe({
      next: () => {
        this.cancelConfirmId = null;
        this.showSuccess('Inscription annulée avec succès.');
        this.loadInscriptions();
      },
      error: (err: unknown) => {
        this.cancelConfirmId = null;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors de l’annulation de l’inscription.'));
      }
    });
  }

  openFormation(formationId: number): void {
    this.router.navigate(['/formations', formationId]);
  }

  logout(): void {
    this.authService.logout();
  }

  getStatusClass(statut: StatutInscription): string {
    switch (statut) {
      case 'EN_ATTENTE':
        return 'badge badge--attente';
      case 'ACCEPTEE':
        return 'badge badge--acceptee';
      case 'REJETEE':
        return 'badge badge--rejetee';
      case 'ANNULEE':
        return 'badge badge--annulee';
      default:
        return 'badge';
    }
  }

  getStatusLabel(statut: StatutInscription): string {
    switch (statut) {
      case 'EN_ATTENTE':
        return 'En attente';
      case 'ACCEPTEE':
        return 'Acceptée';
      case 'REJETEE':
        return 'Rejetée';
      case 'ANNULEE':
        return 'Annulée';
      default:
        return statut;
    }
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
