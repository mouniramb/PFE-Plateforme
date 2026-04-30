import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Formation, StatutFormation } from '../../models/formation.model';
import { AuthService } from '../../services/auth.service';
import { FormationService } from '../../services/formation.service';
import { InscriptionService } from '../../services/inscription.service';
import { mapHttpErrorMessage } from '../../utils/http-error.util';

@Component({
  selector: 'app-catalogue-formations',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './catalogue-formations.component.html',
  styleUrls: ['./catalogue-formations.component.scss']
})
export class CatalogueFormationsComponent implements OnInit {
  formations: Formation[] = [];
  searchTerm = '';

  isLoading = false;
  inscriptionLoading = false;

  selectedFormation: Formation | null = null;

  successMessage = '';
  errorMessage = '';

  constructor(
    private formationService: FormationService,
    private inscriptionService: InscriptionService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCatalogue();
  }

  loadCatalogue(): void {
    this.isLoading = true;
    this.formationService.getCatalogueFormations().subscribe({
      next: (formations: Formation[]) => {
        this.formations = formations;
        this.isLoading = false;
      },
      error: (err: unknown) => {
        this.isLoading = false;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors du chargement du catalogue.'));
      }
    });
  }

  get filteredFormations(): Formation[] {
    const keyword = this.searchTerm.trim().toLowerCase();
    if (!keyword) {
      return this.formations;
    }

    return this.formations.filter(formation => {
      const title = formation.titre.toLowerCase();
      const description = formation.description.toLowerCase();
      return title.includes(keyword) || description.includes(keyword);
    });
  }

  openInscriptionModal(formation: Formation): void {
    this.selectedFormation = formation;
  }

  closeInscriptionModal(): void {
    this.selectedFormation = null;
    this.inscriptionLoading = false;
  }

  confirmInscription(): void {
    if (!this.selectedFormation) {
      return;
    }

    this.inscriptionLoading = true;
    this.inscriptionService.sInscrire(this.selectedFormation.id).subscribe({
      next: () => {
        this.inscriptionLoading = false;
        this.closeInscriptionModal();
        this.showSuccess('Demande d’inscription envoyée avec succès.');
      },
      error: (err: unknown) => {
        this.inscriptionLoading = false;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors de l’inscription à la formation.'));
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.authService.logout();
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

  canInscrire(formation: Formation): boolean {
    return this.isApprenant() && formation.placesDisponibles;
  }

  truncateDescription(value: string): string {
    if (value.length <= 120) {
      return value;
    }
    return `${value.slice(0, 120)}...`;
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

  getPrimaryFormateur(formation: Formation): string {
    if (!formation.formateurs || formation.formateurs.length === 0) {
      return 'Formateur non assigné';
    }

    const first = formation.formateurs[0];
    const extra = formation.formateurs.length - 1;
    if (extra <= 0) {
      return `${first.prenom} ${first.nom}`;
    }

    return `${first.prenom} ${first.nom} +${extra}`;
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
