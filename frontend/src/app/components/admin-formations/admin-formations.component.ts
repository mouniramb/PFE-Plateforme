import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import {
  Formation,
  PageResponse,
  StatutFormation
} from '../../models/formation.model';
import { AuthService } from '../../services/auth.service';
import { FormationService } from '../../services/formation.service';
import { mapHttpErrorMessage } from '../../utils/http-error.util';

@Component({
  selector: 'app-admin-formations',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-formations.component.html',
  styleUrls: ['./admin-formations.component.scss']
})
export class AdminFormationsComponent implements OnInit {
  formations: Formation[] = [];
  isLoading = false;
  successMessage = '';
  errorMessage = '';

  selectedStatut = '';
  deleteConfirmId: number | null = null;

  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  readonly statuts: StatutFormation[] = ['PLANIFIEE', 'EN_COURS', 'TERMINEE'];
  readonly currentUser = this.authService.getCurrentUser();

  constructor(
    private formationService: FormationService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadFormations();
  }

  loadFormations(): void {
    this.isLoading = true;

    const statut = this.selectedStatut ? (this.selectedStatut as StatutFormation) : undefined;

    this.formationService.getAllFormations(this.currentPage, this.pageSize, statut).subscribe({
      next: (response: PageResponse<Formation>) => {
        this.formations = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.isLoading = false;
      },
      error: (err: unknown) => {
        this.isLoading = false;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors du chargement des formations.'));
      }
    });
  }

  onFilterChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.selectedStatut = target.value;
    this.currentPage = 0;
    this.loadFormations();
  }

  openCreate(): void {
    this.router.navigate(['/admin/formations/create']);
  }

  editFormation(id: number): void {
    this.router.navigate(['/admin/formations/edit', id]);
  }

  viewInscrits(id: number): void {
    this.router.navigate(['/admin/formations', id, 'inscrits']);
  }

  confirmDelete(id: number): void {
    this.deleteConfirmId = id;
  }

  cancelDelete(): void {
    this.deleteConfirmId = null;
  }

  deleteFormation(id: number): void {
    this.formationService.deleteFormation(id).subscribe({
      next: () => {
        this.deleteConfirmId = null;
        this.showSuccess('Formation supprimée avec succès.');

        if (this.formations.length === 1 && this.currentPage > 0) {
          this.currentPage -= 1;
        }

        this.loadFormations();
      },
      error: (err: unknown) => {
        this.deleteConfirmId = null;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors de la suppression de la formation.'));
      }
    });
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages || page === this.currentPage) {
      return;
    }

    this.currentPage = page;
    this.loadFormations();
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  getStatutLabel(statut: StatutFormation): string {
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

  getStatutClass(statut: StatutFormation): string {
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

  getPlacesPercent(formation: Formation): number {
    if (!formation.capaciteMax) {
      return 0;
    }

    return Math.min((formation.capaciteActuelle / formation.capaciteMax) * 100, 100);
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
