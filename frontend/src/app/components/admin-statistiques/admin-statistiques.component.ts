import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { StatistiqueService } from '../../services/statistique.service';

@Component({
  selector: 'app-admin-statistiques',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-statistiques.component.html',
  styleUrls: ['./admin-statistiques.component.scss']
})
export class AdminStatistiquesComponent implements OnInit {
  stats: any = { nbFormations: 0, nbApprenants: 0, nbInstructeurs: 0, revenuTotal: 0 };
  formations: any[] = [];
  loading = false;
  error: string | null = null;

  dateDebut: string = '';
  dateFin: string = '';

  constructor(private statistiqueService: StatistiqueService) {}

  ngOnInit() {
    this.loadStatistiques();
    this.loadFormations();
  }

  loadStatistiques() {
    this.loading = true;
    this.statistiqueService.getStatistiques().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des statistiques';
        this.loading = false;
        console.error('Erreur getStatistiques:', err);
      }
    });
  }

  loadFormations() {
    this.formations = [
      { nom: 'JavaScript',  nbApprenants: 5, nbSeances: 20, revenu: 4000, tauxReussite: 80 },
      { nom: 'Angular',     nbApprenants: 6, nbSeances: 24, revenu: 7200, tauxReussite: 85 },
      { nom: 'Spring Boot', nbApprenants: 4, nbSeances: 16, revenu: 3200, tauxReussite: 75 },
      { nom: 'React',       nbApprenants: 5, nbSeances: 20, revenu: 3500, tauxReussite: 80 },
      { nom: 'Python',      nbApprenants: 5, nbSeances: 20, revenu: 2100, tauxReussite: 70 }
    ];
  }

  filterByDate() {
    if (!this.dateDebut || !this.dateFin) {
      this.error = 'Veuillez sélectionner les deux dates.';
      return;
    }
    this.error = null;
    this.loading = true;
    this.statistiqueService.filterByDateRange(this.dateDebut, this.dateFin).subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du filtrage';
        this.loading = false;
        console.error('Erreur filterByDateRange:', err);
      }
    });
  }
}
