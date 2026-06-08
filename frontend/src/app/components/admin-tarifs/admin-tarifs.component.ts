import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TarifService } from '../../services/tarif.service';

@Component({
  selector: 'app-admin-tarifs',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-tarifs.component.html',
  styleUrls: ['./admin-tarifs.component.scss']
})
export class AdminTarifsComponent implements OnInit {
  tarifs: any[] = [];
  loading = false;
  error: string | null = null;
  successMessage: string | null = null;

  showTarifModal = false;
  editingTarif = false;
  currentTarif: any = {};

  constructor(private tarifService: TarifService) {}

  ngOnInit() {
    this.loadTarifs();
  }

  loadTarifs() {
    this.loading = true;
    this.error = null;
    this.tarifService.getTarifs().subscribe({
      next: (data) => {
        this.tarifs = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des tarifs';
        this.loading = false;
        console.error('Erreur getTarifs:', err);
      }
    });
  }

  addTarif() {
    this.openAddModal();
  }

  openAddModal() {
    this.editingTarif = false;
    this.currentTarif = {};
    this.showTarifModal = true;
  }

  openEditModal(tarif: any) {
    this.editingTarif = true;
    this.currentTarif = { ...tarif };
    this.showTarifModal = true;
  }

  closeModal() {
    this.showTarifModal = false;
    this.currentTarif = {};
    this.error = null;
  }

  saveTarif() {
    if (this.editingTarif) {
      this.tarifService.updateTarif(this.currentTarif.id, this.currentTarif).subscribe({
        next: () => {
          this.successMessage = 'Tarif mis à jour.';
          this.loadTarifs();
          this.closeModal();
        },
        error: (err) => console.error('Erreur update tarif :', err)
      });
    } else {
      this.tarifService.createTarif(this.currentTarif).subscribe({
        next: () => {
          this.successMessage = 'Tarif créé.';
          this.loadTarifs();
          this.closeModal();
        },
        error: (err) => console.error('Erreur create tarif :', err)
      });
    }
  }

  editarif(tarif: any) {
    this.openEditModal(tarif);
  }

  deleteTarif(id: number) {
    if (confirm('Supprimer ce tarif ?')) {
      this.tarifService.deleteTarif(id).subscribe({
        next: () => {
          this.tarifs = this.tarifs.filter(t => t.id !== id);
          this.successMessage = 'Tarif supprimé.';
        },
        error: (err) => console.error('Erreur deleteTarif:', err)
      });
    }
  }
}
