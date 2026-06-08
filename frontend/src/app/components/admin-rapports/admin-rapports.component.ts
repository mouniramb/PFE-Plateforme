import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { RapportService } from '../../services/rapport.service';

@Component({
  selector: 'app-admin-rapports',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-rapports.component.html',
  styleUrls: ['./admin-rapports.component.scss']
})
export class AdminRapportsComponent implements OnInit {
  rapports: any[] = [];
  allRapports: any[] = [];
  selectedType: string = '';
  loading = false;
  error: string | null = null;
  successMessage: string | null = null;
  dateDebut: string = '';
  dateFin: string = '';

  constructor(private rapportService: RapportService) {}

  ngOnInit() {
    this.loadRapports();
  }

  loadRapports() {
    this.loading = true;
    this.error = null;
    this.rapportService.getRapports().subscribe({
      next: (data) => {
        this.allRapports = data;
        this.rapports = data;
        this.loading = false;
        if (this.selectedType) {
          this.filterByType(this.selectedType);
        }
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des rapports';
        this.loading = false;
        console.error('Erreur getRapports:', err);
      }
    });
  }

  filterByType(type: string) {
    this.selectedType = type;
    this.rapports = this.allRapports.filter(r => r.type === type);
  }

  resetFilter() {
    this.selectedType = '';
    this.rapports = this.allRapports;
  }

  generateRapportType(type: string) {
    this.loading = true;
    this.rapportService.generateRapport(type).subscribe({
      next: (data) => {
        this.allRapports.unshift(data);
        this.loading = false;
        this.successMessage = `Rapport ${this.getTypeLabel(type)} généré.`;
        if (this.selectedType) {
          this.filterByType(this.selectedType);
        } else {
          this.rapports = this.allRapports;
        }
      },
      error: (err) => {
        this.error = 'Erreur lors de la génération du rapport';
        this.loading = false;
        console.error('Erreur génération rapport :', err);
      }
    });
  }

  generateRapportApprenants() { this.generateRapportType('APPRENANTS'); }
  generateRapportFormations()  { this.generateRapportType('FORMATIONS'); }
  generateRapportPaiements()   { this.generateRapportType('PAIEMENTS'); }

  generateRapport() {
    if (!this.dateDebut || !this.dateFin) {
      this.error = 'Veuillez sélectionner une date de début et de fin.';
      return;
    }
    this.loading = true;
    this.rapportService.generateRapport('CUSTOM', this.dateDebut, this.dateFin).subscribe({
      next: (data) => {
        this.allRapports.unshift(data);
        this.loading = false;
        this.successMessage = 'Rapport personnalisé généré.';
        this.dateDebut = '';
        this.dateFin = '';
        if (this.selectedType) {
          this.filterByType(this.selectedType);
        } else {
          this.rapports = this.allRapports;
        }
      },
      error: (err) => {
        this.error = 'Erreur lors de la génération';
        this.loading = false;
        console.error('Erreur génération rapport :', err);
      }
    });
  }

  downloadRapport(id: number) {
    this.rapportService.downloadRapport(id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `rapport-${id}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        this.error = 'Erreur lors du téléchargement';
        console.error('Erreur download:', err);
      }
    });
  }

  deleteRapport(id: number) {
    if (confirm('Supprimer ce rapport ?')) {
      this.rapportService.deleteRapport(id).subscribe({
        next: () => {
          this.successMessage = 'Rapport supprimé.';
          this.loadRapports();
        },
        error: (err) => console.error('Erreur deleteRapport:', err)
      });
    }
  }

  getTypeLabel(type: string): string {
    const labels: Record<string, string> = {
      APPRENANTS: 'Apprenants',
      FORMATIONS:  'Formations',
      PAIEMENTS:   'Paiements',
      CUSTOM:      'Personnalisé',
      PDF:         'Document'
    };
    return labels[type] || type;
  }

  getTypeBadgeClass(type: string): string {
    const classes: Record<string, string> = {
      APPRENANTS: 'badge-apprenants',
      FORMATIONS:  'badge-formations',
      PAIEMENTS:   'badge-paiements',
      CUSTOM:      'badge-custom',
      PDF:         'badge-pdf'
    };
    return classes[type] || 'badge-pdf';
  }
}
