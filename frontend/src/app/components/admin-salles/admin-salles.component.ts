import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SalleService } from '../../services/salle.service';
import { Salle, SalleRequest, PageResponse } from '../../models/planning.model';

@Component({
  selector: 'app-admin-salles',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './admin-salles.component.html',
  styleUrl: './admin-salles.component.scss'
})
export class AdminSallesComponent implements OnInit {
  salles: Salle[] = [];
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  showModal = false;
  isEditMode = false;
  editingSalleId: number | null = null;
  
  salleForm: FormGroup;
  successMessage = '';
  errorMessage = '';
  deleteConfirmId: number | null = null;

  activeMenuId = 'salles';

  constructor(private salleService: SalleService, private fb: FormBuilder) {
    this.salleForm = this.fb.group({
      nom: ['', [Validators.required]],
      capacite: ['', [Validators.required, Validators.min(1)]],
      localisation: [''],
      equipements: [''],
      disponible: [true]
    });
  }

  ngOnInit(): void {
    this.loadSalles();
  }

  loadSalles(): void {
    this.salleService.getAllSalles(this.currentPage, this.pageSize).subscribe({
      next: (response: PageResponse<Salle>) => {
        this.salles = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.clearMessages();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Erreur lors du chargement des salles';
      }
    });
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.editingSalleId = null;
    this.salleForm.reset({ disponible: true });
    this.showModal = true;
  }

  openEditModal(salle: Salle): void {
    this.isEditMode = true;
    this.editingSalleId = salle.id;
    this.salleForm.patchValue({
      nom: salle.nom,
      capacite: salle.capacite,
      localisation: salle.localisation,
      equipements: salle.equipements,
      disponible: salle.disponible
    });
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.salleForm.reset();
    this.deleteConfirmId = null;
  }

  saveSalle(): void {
    if (this.salleForm.invalid) {
      return;
    }

    const formData: SalleRequest = this.salleForm.value;

    if (this.isEditMode && this.editingSalleId) {
      this.salleService.updateSalle(this.editingSalleId, formData).subscribe({
        next: () => {
          this.successMessage = 'Salle mise à jour avec succès';
          this.closeModal();
          this.loadSalles();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Erreur lors de la mise à jour';
        }
      });
    } else {
      this.salleService.createSalle(formData).subscribe({
        next: () => {
          this.successMessage = 'Salle créée avec succès';
          this.closeModal();
          this.loadSalles();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Erreur lors de la création';
        }
      });
    }
  }

  confirmDelete(salleId: number): void {
    this.deleteConfirmId = salleId;
  }

  cancelDelete(): void {
    this.deleteConfirmId = null;
  }

  deleteSalle(salleId: number): void {
    this.salleService.deleteSalle(salleId).subscribe({
      next: () => {
        this.successMessage = 'Salle supprimée avec succès';
        this.deleteConfirmId = null;
        this.loadSalles();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Erreur lors de la suppression';
        this.deleteConfirmId = null;
      }
    });
  }

  toggleDisponibilite(salle: Salle): void {
    const updatedData: SalleRequest = {
      nom: salle.nom,
      capacite: salle.capacite,
      localisation: salle.localisation,
      equipements: salle.equipements,
      disponible: !salle.disponible
    };
    
    this.salleService.updateSalle(salle.id, updatedData).subscribe({
      next: () => {
        salle.disponible = !salle.disponible;
        this.successMessage = 'Statut de disponibilité mis à jour';
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Erreur lors de la mise à jour';
      }
    });
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadSalles();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadSalles();
    }
  }

  clearMessages(): void {
    setTimeout(() => {
      this.successMessage = '';
      this.errorMessage = '';
    }, 3000);
  }

  getInitials(text: string): string {
    return text.split(' ').map(word => word[0]).join('').toUpperCase().slice(0, 2);
  }
}
