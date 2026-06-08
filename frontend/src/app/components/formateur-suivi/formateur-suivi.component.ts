import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormationService } from '../../services/formation.service';
import { Formation, StatutFormation } from '../../models/formation.model';

@Component({
  selector: 'app-formateur-suivi',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './formateur-suivi.component.html',
  styleUrls: ['./formateur-suivi.component.scss']
})
export class FormateurSuiviComponent implements OnInit {

  formations: Formation[] = [];
  isLoading = false;
  errorMessage = '';
  currentUser = this.authService.getCurrentUser();

  constructor(
    private authService: AuthService,
    private formationService: FormationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userId = this.currentUser?.id;
    if (!userId) return;
    this.isLoading = true;
    this.formationService.getFormationsByFormateur(userId, 0, 100).subscribe({
      next: (page) => { this.formations = page.content; this.isLoading = false; },
      error: () => { this.errorMessage = 'Erreur lors du chargement.'; this.isLoading = false; }
    });
  }

  goToSuivi(formationId: number): void {
    this.router.navigate(['/formateur/formations', formationId, 'suivi']);
  }

  getStatutClass(statut: StatutFormation): string {
    const map: Record<string, string> = {
      PLANIFIEE: 'statut-planifiee', EN_COURS: 'statut-en-cours', TERMINEE: 'statut-terminee'
    };
    return map[statut] ?? '';
  }

  getStatutLabel(statut: StatutFormation): string {
    const map: Record<string, string> = {
      PLANIFIEE: 'Planifiée', EN_COURS: 'En cours', TERMINEE: 'Terminée'
    };
    return map[statut] ?? statut;
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
  }
}
