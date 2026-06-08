import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FactureService } from '../../services/facture.service';

@Component({
  selector: 'app-admin-factures',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-factures.component.html',
  styleUrls: ['./admin-factures.component.scss']
})
export class AdminFacturesComponent implements OnInit {
  factures: any[] = [];
  loading = false;
  error: string | null = null;
  successMessage: string | null = null;

  showFactureModal = false;
  editingFacture = false;
  currentFacture: any = {};

  constructor(private factureService: FactureService) {}

  ngOnInit() {
    this.loadFactures();
  }

  loadFactures() {
    this.loading = true;
    this.error = null;
    this.factureService.getAllFactures().subscribe({
      next: (data) => {
        this.factures = data;
        this.loading = false;
        console.log('Factures chargées :', data);
      },
      error: (err) => {
        this.error = `Erreur ${err.status} — ${err.error?.message || err.message || 'Impossible de charger les factures'}`;
        this.loading = false;
        console.error('Erreur chargement factures :', err);
      }
    });
  }

  statusClass(statut: string): string {
    if (statut === 'Payée' || statut === 'PAYEE') return 'status-paid';
    if (statut === 'En attente' || statut === 'EN_ATTENTE') return 'status-pending';
    return 'status-cancelled';
  }

  filterByStatut(event: any) {
    const statut = event.target.value;
    if (statut === '') {
      this.loadFactures();
      return;
    }
    this.loading = true;
    this.factureService.filterByStatut(statut).subscribe({
      next: (data) => {
        this.factures = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du filtrage';
        this.loading = false;
        console.error('Erreur filtre statut :', err);
      }
    });
  }

  openAddModal() {
    this.editingFacture = false;
    this.currentFacture = { statut: 'En attente' };
    this.showFactureModal = true;
  }

  openEditModal(facture: any) {
    this.editingFacture = true;
    this.currentFacture = { ...facture };
    this.showFactureModal = true;
  }

  closeModal() {
    this.showFactureModal = false;
    this.currentFacture = {};
    this.error = null;
  }

  saveFacture() {
    if (this.editingFacture) {
      this.factureService.updateFacture(this.currentFacture.id, this.currentFacture).subscribe({
        next: () => {
          this.successMessage = 'Facture mise à jour.';
          this.loadFactures();
          this.closeModal();
        },
        error: (err) => console.error('Erreur update facture :', err)
      });
    } else {
      this.factureService.createFacture(this.currentFacture).subscribe({
        next: () => {
          this.successMessage = 'Facture créée.';
          this.loadFactures();
          this.closeModal();
        },
        error: (err) => console.error('Erreur create facture :', err)
      });
    }
  }

  downloadPDF(id: number) {
    this.factureService.downloadFacturePDF(id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `facture-${id}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Erreur download:', err)
    });
  }

  deleteFacture(id: number) {
    if (confirm('Supprimer cette facture ?')) {
      this.factureService.deleteFacture(id).subscribe({
        next: () => {
          this.factures = this.factures.filter(f => f.id !== id);
          this.successMessage = 'Facture supprimée.';
        },
        error: (err) => console.error('Erreur deleteFacture:', err)
      });
    }
  }

  createFacture() {
    this.openAddModal();
  }
}
