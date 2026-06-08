import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { SuiviService } from '../../services/suivi.service';
import { StatistiquesApprenant } from '../../models/planning.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-suivi-apprenant',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './suivi-apprenant.component.html',
  styleUrl: './suivi-apprenant.component.scss'
})
export class SuiviApprenantComponent implements OnInit {
  suiviList: StatistiquesApprenant[] = [];
  loading = true;
  errorMessage = '';
  userRole: string = '';
  currentUser: any;

  constructor(
    private suiviService: SuiviService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.userRole = this.authService.getUserRole() || '';
    this.currentUser = this.authService.getCurrentUser();
    this.loadSuivi();
  }

  loadSuivi(): void {
    this.loading = true;
    this.suiviService.getMonSuivi().subscribe({
      next: (stats: StatistiquesApprenant[]) => {
        this.suiviList = stats;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors du chargement du suivi';
        this.loading = false;
      }
    });
  }

  getTauxColor(taux: number): string {
    if (taux >= 80) return 'taux-vert';
    if (taux >= 60) return 'taux-orange';
    return 'taux-rouge';
  }

  getMoyenneColor(moyenne: number): string {
    if (moyenne >= 10) return 'moyenne-vert';
    if (moyenne >= 7) return 'moyenne-orange';
    return 'moyenne-rouge';
  }

  getPresenceColor(statut: string): string {
    switch (statut) {
      case 'PRESENT': return 'presence-present';
      case 'ABSENT': return 'presence-absent';
      case 'RETARD': return 'presence-retard';
      case 'EXCUSE': return 'presence-excuse';
      default: return '';
    }
  }

  getNoteColor(valeur: number): string {
    if (valeur >= 10) return 'note-vert';
    if (valeur >= 7) return 'note-orange';
    return 'note-rouge';
  }
}
