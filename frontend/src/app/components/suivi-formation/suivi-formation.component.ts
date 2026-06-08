import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { SuiviService } from '../../services/suivi.service';
import { AuthService } from '../../services/auth.service';
import { StatistiquesApprenant } from '../../models/planning.model';

@Component({
  selector: 'app-suivi-formation',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './suivi-formation.component.html',
  styleUrl: './suivi-formation.component.scss'
})
export class SuiviFormationComponent implements OnInit {
  suiviList: StatistiquesApprenant[] = [];
  loading = true;
  formationId: number = 0;

  constructor(
    private suiviService: SuiviService,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.formationId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.formationId) {
      this.loadSuivi();
    }
  }

  loadSuivi(): void {
    this.suiviService.getSuiviFormation(this.formationId).subscribe({
      next: (stats: StatistiquesApprenant[]) => {
        this.suiviList = stats;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  exportCSV(): void {
    const csv = this.generateCSV();
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `suivi-formation-${this.formationId}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  }

  generateCSV(): string {
    let csv = 'Nom,Prenom,Email,Taux Presence,Moyenne,Nb Notes\n';
    this.suiviList.forEach(s => {
      csv += `${s.nom},${s.prenom},${s.email},${(s.tauxPresence * 100).toFixed(1)}%,${s.moyenneGenerale.toFixed(2)},${s.detailNotes.length}\n`;
    });
    return csv;
  }

  getMoyenneTauxPresence(): number {
    if (this.suiviList.length === 0) return 0;
    return this.suiviList.reduce((sum, s) => sum + s.tauxPresence, 0) / this.suiviList.length;
  }

  getMoyenneClasse(): number {
    if (this.suiviList.length === 0) return 0;
    return this.suiviList.reduce((sum, s) => sum + s.moyenneGenerale, 0) / this.suiviList.length;
  }

  getTauxColor(taux: number): string {
    if (taux >= 0.8) return 'taux-vert';
    if (taux >= 0.6) return 'taux-orange';
    return 'taux-rouge';
  }

  goBack(): void {
    const role = this.authService.getCurrentUser()?.role;
    if (role === 'FORMATEUR') {
      this.router.navigate(['/formateur/dashboard']);
    } else {
      this.router.navigate(['/admin/dashboard']);
    }
  }

  getMoyenneColor(moyenne: number): string {
    if (moyenne >= 10) return 'moyenne-vert';
    if (moyenne >= 7) return 'moyenne-orange';
    return 'moyenne-rouge';
  }
}
