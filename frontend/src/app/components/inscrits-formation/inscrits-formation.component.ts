import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Formation, Inscription } from '../../models/formation.model';
import { AuthService } from '../../services/auth.service';
import { FormationService } from '../../services/formation.service';
import { InscriptionService } from '../../services/inscription.service';
import { mapHttpErrorMessage } from '../../utils/http-error.util';

@Component({
  selector: 'app-inscrits-formation',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './inscrits-formation.component.html',
  styleUrls: ['./inscrits-formation.component.scss']
})
export class InscritsFormationComponent implements OnInit {
  formation: Formation | null = null;
  inscriptions: Inscription[] = [];

  isLoading = false;

  errorMessage = '';

  readonly currentUser = this.authService.getCurrentUser();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formationService: FormationService,
    private inscriptionService: InscriptionService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : NaN;

    if (!id || Number.isNaN(id)) {
      this.router.navigate(['/catalogue']);
      return;
    }

    this.loadData(id);
  }

  loadData(formationId: number): void {
    this.isLoading = true;

    this.formationService.getFormation(formationId).subscribe({
      next: (formation: Formation) => {
        this.formation = formation;
        this.loadInscrits(formationId);
      },
      error: (err: unknown) => {
        this.isLoading = false;
        this.errorMessage = mapHttpErrorMessage(err, 'Erreur lors du chargement de la formation.');
      }
    });
  }

  loadInscrits(formationId: number): void {
    this.inscriptionService.getInscritsAcceptes(formationId).subscribe({
      next: (inscriptions: Inscription[]) => {
        this.inscriptions = inscriptions;
        this.isLoading = false;
      },
      error: (err: unknown) => {
        this.isLoading = false;
        this.errorMessage = mapHttpErrorMessage(err, 'Erreur lors du chargement des inscrits.');
      }
    });
  }

  exportCsv(): void {
    const headers = ['Nom', 'Prénom', 'Email', 'Date inscription'];
    const rows = this.inscriptions.map(item => [
      item.apprenantNom,
      item.apprenantPrenom,
      item.apprenantEmail,
      item.dateInscription ? new Date(item.dateInscription).toLocaleDateString('fr-FR') : ''
    ]);

    const csv = [headers, ...rows]
      .map(line => line.map(value => `"${String(value).replace(/"/g, '""')}"`).join(','))
      .join('\n');

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    const formationName = this.formation?.titre?.replace(/\s+/g, '_').toLowerCase() ?? 'formation';

    link.href = url;
    link.download = `inscrits_${formationName}.csv`;
    link.click();

    URL.revokeObjectURL(url);
  }

  getRetourLink(): string {
    if (this.isAdmin()) {
      return '/admin/formations';
    }

    return '/formateur/dashboard';
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
}
