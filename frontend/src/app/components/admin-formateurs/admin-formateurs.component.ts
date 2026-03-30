import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { UserResponse, CreateFormateurRequest } from '../../models/user.model';
import { mapHttpErrorMessage } from '../../utils/http-error.util';

@Component({
  selector: 'app-admin-formateurs',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './admin-formateurs.component.html',
  styleUrls: ['./admin-formateurs.component.scss']
})
export class AdminFormateursComponent implements OnInit {

  formateurs: UserResponse[] = [];
  isLoading = false;
  showModal = false;
  isEditing = false;
  editingId: number | null = null;
  formLoading = false;
  successMessage = '';
  errorMessage = '';
  deleteConfirmId: number | null = null;

  formateurForm!: FormGroup;
  currentUser = this.authService.getCurrentUser();

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadFormateurs();
  }

  initForm(): void {
    this.formateurForm = this.fb.group({
      nom:    ['', [Validators.required, Validators.minLength(2)]],
      prenom: ['', [Validators.required, Validators.minLength(2)]],
      email:  ['', [Validators.required, Validators.email]]
    });
  }

  loadFormateurs(): void {
    this.isLoading = true;
    this.userService.getAllFormateurs().subscribe({
      next: (data) => { this.formateurs = data; this.isLoading = false; },
      error: (err) => {
        this.isLoading = false;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors du chargement des formateurs.'));
      }
    });
  }

  openAddModal(): void {
    this.isEditing = false;
    this.editingId = null;
    this.formateurForm.reset();
    this.showModal = true;
    this.clearMessages();
  }

  openEditModal(formateur: UserResponse): void {
    this.isEditing = true;
    this.editingId = formateur.id;
    this.formateurForm.patchValue({ nom: formateur.nom, prenom: formateur.prenom, email: formateur.email });
    this.showModal = true;
    this.clearMessages();
  }

  closeModal(): void {
    this.showModal = false;
    this.formateurForm.reset();
    this.clearMessages();
  }

  onSubmit(): void {
    if (this.formateurForm.invalid) { this.formateurForm.markAllAsTouched(); return; }

    this.formLoading = true;
    const request: CreateFormateurRequest = this.formateurForm.value;

    if (this.isEditing && this.editingId !== null) {
      this.userService.updateFormateur(this.editingId, request).subscribe({
        next: (updated) => {
          const idx = this.formateurs.findIndex(f => f.id === this.editingId);
          if (idx !== -1) this.formateurs[idx] = updated;
          this.formLoading = false;
          this.closeModal();
          this.showSuccess('Formateur modifié avec succès.');
        },
        error: (err) => {
          this.formLoading = false;
          this.showError(mapHttpErrorMessage(err, 'Erreur lors de la modification.'));
        }
      });
    } else {
      this.userService.createFormateur(request).subscribe({
        next: (created) => {
          this.formateurs.unshift(created);
          this.formLoading = false;
          this.closeModal();
          this.showSuccess('Formateur ajouté. Un email avec ses identifiants a été envoyé.');
        },
        error: (err) => {
          this.formLoading = false;
          this.showError(mapHttpErrorMessage(err, 'Erreur lors de la création.'));
        }
      });
    }
  }

  confirmDelete(id: number): void { this.deleteConfirmId = id; }
  cancelDelete(): void { this.deleteConfirmId = null; }

  deleteFormateur(id: number): void {
    this.userService.deleteFormateur(id).subscribe({
      next: () => {
        this.formateurs = this.formateurs.filter(f => f.id !== id);
        this.deleteConfirmId = null;
        this.showSuccess('Formateur supprimé avec succès.');
      },
      error: (err) => {
        this.deleteConfirmId = null;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors de la suppression.'));
      }
    });
  }

  logout(): void { this.authService.logout(); }

  private showSuccess(msg: string): void {
    this.successMessage = msg; this.errorMessage = '';
    setTimeout(() => this.successMessage = '', 5000);
  }
  private showError(msg: string): void {
    this.errorMessage = msg;
    setTimeout(() => this.errorMessage = '', 5000);
  }
  private clearMessages(): void { this.successMessage = ''; this.errorMessage = ''; }

  get nom()    { return this.formateurForm.get('nom'); }
  get prenom() { return this.formateurForm.get('prenom'); }
  get email()  { return this.formateurForm.get('email'); }
}
