import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SeanceService } from '../../services/seance.service';
import { Seance, SeanceStatut } from '../../models/planning.model';

@Component({
  selector: 'app-formateur-presences',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './formateur-presences.component.html',
  styleUrls: ['./formateur-presences.component.scss']
})
export class FormateurPresencesComponent implements OnInit {

  seances: Seance[] = [];
  isLoading = false;
  errorMessage = '';
  currentUser = this.authService.getCurrentUser();

  constructor(
    private authService: AuthService,
    private seanceService: SeanceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userId = this.currentUser?.id;
    if (!userId) return;
    this.isLoading = true;
    this.seanceService.getSeancesByFormateur(userId, 0, 100).subscribe({
      next: (page) => { this.seances = page.content; this.isLoading = false; },
      error: () => { this.errorMessage = 'Erreur lors du chargement.'; this.isLoading = false; }
    });
  }

  goToPresences(seanceId: number): void {
    this.router.navigate(['/formateur/seances', seanceId, 'presences']);
  }

  getStatutClass(statut: SeanceStatut): string {
    const map: Record<string, string> = {
      PLANIFIEE: 'statut-planifiee', EN_COURS: 'statut-en-cours',
      TERMINEE: 'statut-terminee',  ANNULEE: 'statut-annulee'
    };
    return map[statut] ?? '';
  }

  getStatutLabel(statut: SeanceStatut): string {
    const map: Record<string, string> = {
      PLANIFIEE: 'Planifiée', EN_COURS: 'En cours',
      TERMINEE: 'Terminée',  ANNULEE: 'Annulée'
    };
    return map[statut] ?? statut;
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('fr-FR', {
      weekday: 'short', day: '2-digit', month: 'short', year: 'numeric'
    });
  }

  formatHeure(dateStr: string): string {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
  }
}
