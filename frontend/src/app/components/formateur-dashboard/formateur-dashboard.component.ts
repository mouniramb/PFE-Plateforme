import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SeanceService } from '../../services/seance.service';
import { Seance, SeanceStatut } from '../../models/planning.model';

@Component({
  selector: 'app-formateur-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './formateur-dashboard.component.html',
  styleUrls: ['./formateur-dashboard.component.scss']
})
export class FormateurDashboardComponent implements OnInit {

  currentUser = this.authService.getCurrentUser();
  seances    : Seance[] = [];
  isLoading  = false;
  errorMessage = '';

  constructor(
    private authService  : AuthService,
    private seanceService: SeanceService,
    private router       : Router
  ) {}

  ngOnInit(): void {
    // Vérification rôle
    const role = this.currentUser?.role;
    if (role !== 'FORMATEUR' && role !== 'ADMIN') {
      this.router.navigate(['/login']);
      return;
    }
    this.loadSeances();
  }

  loadSeances(): void {
    const userId = this.currentUser?.id;
    if (!userId) return;

    this.isLoading = true;
    this.seanceService.getSeancesByFormateur(userId, 0, 100).subscribe({
      next : (page) => {
        this.seances  = page.content;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des séances.';
        this.isLoading    = false;
      }
    });
  }

  // ── Stats ──────────────────────────────────────────────────────────
  get nbSeances(): number { return this.seances.length; }

  get nbFormations(): number {
    return new Set(this.seances.map(s => s.formation?.id)).size;
  }

  get nbSeancesTerminees(): number {
    return this.seances.filter(s => s.statut === 'TERMINEE').length;
  }

  get seancesEnCours(): Seance[] {
    return this.seances.filter(s => s.statut === 'EN_COURS' || s.statut === 'PLANIFIEE');
  }

  get seancesTerminees(): Seance[] {
    return this.seances.filter(s => s.statut === 'TERMINEE');
  }

  // ── Navigation ─────────────────────────────────────────────────────
  allerPresences(seanceId: number): void {
    this.router.navigate(['/formateur/seances', seanceId, 'presences']);
  }

  allerNotes(seanceId: number): void {
    this.router.navigate(['/formateur/seances', seanceId, 'notes']);
  }

  allerSuivi(formationId: number): void {
    this.router.navigate(['/formateur/formations', formationId, 'suivi']);
  }

  // ── Helpers ────────────────────────────────────────────────────────
  getStatutClass(statut: SeanceStatut): string {
    const map: Record<string, string> = {
      PLANIFIEE: 'statut-planifiee',
      EN_COURS : 'statut-en-cours',
      TERMINEE : 'statut-terminee',
      ANNULEE  : 'statut-annulee'
    };
    return map[statut] ?? '';
  }

  getStatutLabel(statut: SeanceStatut): string {
    const map: Record<string, string> = {
      PLANIFIEE: 'Planifiée',
      EN_COURS : 'En cours',
      TERMINEE : 'Terminée',
      ANNULEE  : 'Annulée'
    };
    return map[statut] ?? statut;
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    return d.toLocaleDateString('fr-FR', {
      weekday: 'short', day: '2-digit', month: 'short', year: 'numeric'
    });
  }

  formatHeure(dateStr: string): string {
    if (!dateStr) return '—';
    const d = new Date(dateStr);
    return d.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
  }

  logout(): void { this.authService.logout(); }
}
