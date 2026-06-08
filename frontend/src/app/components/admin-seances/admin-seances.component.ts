import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormControl, Validators, AbstractControl, ValidationErrors, FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SeanceService } from '../../services/seance.service';
import { FormationService } from '../../services/formation.service';
import { UserService } from '../../services/user.service';
import { SalleService } from '../../services/salle.service';
import { Seance, SeanceRequest, SeanceStatut, Formation, Salle, ConflitDTO, PageResponse } from '../../models/planning.model';
import { Formation as FormationModel, PageResponse as FormationPageResponse } from '../../models/formation.model';

@Component({
  selector: 'app-admin-seances',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterLink],
  templateUrl: './admin-seances.component.html',
  styleUrl: './admin-seances.component.scss'
})
export class AdminSeancesComponent implements OnInit {
  seances: Seance[] = [];
  formations: FormationModel[] = [];
  formateurs: any[] = [];
  salles: Salle[] = [];

  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  selectedFormationId: number | null = null;

  showModal = false;
  isEditMode = false;
  editingSeanceId: number | null = null;

  seanceForm: FormGroup;
  successMessage = '';
  errorMessage = '';
  deleteConfirmId: number | null = null;

  // Conflict checking
  checkingConflits = false;
  conflitsDetected: ConflitDTO[] = [];
  forceCreation = false;

  get formationIdControl(): FormControl {
    return this.seanceForm.get('formationId') as FormControl;
  }

  activeMenuId = 'seances';
  statutOptions: SeanceStatut[] = ['PLANIFIEE', 'EN_COURS', 'TERMINEE', 'ANNULEE'];

  constructor(
    private seanceService: SeanceService,
    private formationService: FormationService,
    private userService: UserService,
    private salleService: SalleService,
    private fb: FormBuilder
  ) {
    this.seanceForm = this.fb.group({
      titre: ['', [Validators.required, Validators.minLength(3)]],
      description: [''],
      formationId: ['', Validators.required],
      formateurId: [''],
      salleId: [''],
      dateHeureDebut: ['', Validators.required],
      dateHeureFin: ['', Validators.required],
      statut: ['PLANIFIEE']
    }, { validators: this.dateValidator });
  }

  dateValidator(control: AbstractControl): ValidationErrors | null {
    const debut = control.get('dateHeureDebut')?.value;
    const fin = control.get('dateHeureFin')?.value;

    if (debut && fin) {
      const debutDate = new Date(debut);
      const finDate = new Date(fin);
      if (finDate <= debutDate) {
        return { dateInvalid: true };
      }
    }
    return null;
  }

  ngOnInit(): void {
    this.loadFormations();
    this.loadFormateurs();
    this.loadSalles();
  }

  loadFormations(): void {
    this.formationService.getAllFormations(0, 100).subscribe({
      next: (response: FormationPageResponse<FormationModel>) => {
        this.formations = response.content;
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors du chargement des formations';
      }
    });
  }

  loadFormateurs(): void {
    this.userService.getAllFormateurs().subscribe({
      next: (formateurs: any[]) => {
        this.formateurs = formateurs;
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors du chargement des formateurs';
      }
    });
  }

  loadSalles(): void {
    this.salleService.getSallesDisponibles().subscribe({
      next: (salles: Salle[]) => {
        this.salles = salles;
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors du chargement des salles';
      }
    });
  }

  onFormationChange(): void {
    this.currentPage = 0;
    const formationId = this.seanceForm.get('formationId')?.value;
    if (formationId) {
      this.selectedFormationId = formationId;
      this.loadSeances();
    }
  }

  loadSeances(): void {
    if (!this.selectedFormationId) return;

    this.seanceService.getSeancesByFormation(this.selectedFormationId, this.currentPage, this.pageSize).subscribe({
      next: (response: PageResponse<Seance>) => {
        this.seances = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.clearMessages();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Erreur lors du chargement des séances';
      }
    });
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.editingSeanceId = null;
    this.seanceForm.reset({ statut: 'PLANIFIEE' });
    this.conflitsDetected = [];
    this.forceCreation = false;
    this.showModal = true;
  }

  openEditModal(seance: Seance): void {
    this.isEditMode = true;
    this.editingSeanceId = seance.id;
    this.seanceForm.patchValue({
      titre: seance.titre,
      description: seance.description,
      formationId: seance.formation.id,
      formateurId: seance.formateur?.id || '',
      salleId: seance.salle?.id || '',
      dateHeureDebut: seance.dateHeureDebut,
      dateHeureFin: seance.dateHeureFin,
      statut: seance.statut
    });
    this.conflitsDetected = [];
    this.forceCreation = false;
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.seanceForm.reset();
    this.conflitsDetected = [];
    this.forceCreation = false;
    this.deleteConfirmId = null;
  }

  checkConflits(): void {
    if (this.seanceForm.invalid) {
      return;
    }

    this.checkingConflits = true;
    const formData: SeanceRequest = {
      titre: this.seanceForm.get('titre')?.value,
      description: this.seanceForm.get('description')?.value,
      dateHeureDebut: new Date(this.seanceForm.get('dateHeureDebut')?.value).toISOString(),
      dateHeureFin: new Date(this.seanceForm.get('dateHeureFin')?.value).toISOString(),
      formationId: this.seanceForm.get('formationId')?.value,
      salleId: this.seanceForm.get('salleId')?.value || undefined,
      formateurId: this.seanceForm.get('formateurId')?.value || undefined,
      statut: this.seanceForm.get('statut')?.value
    };

    this.seanceService.checkConflits(formData).subscribe({
      next: (conflits: ConflitDTO[]) => {
        this.conflitsDetected = conflits;
        this.checkingConflits = false;
        if (conflits.length === 0) {
          this.successMessage = 'Aucun conflit détecté';
        }
      },
      error: (err) => {
        this.checkingConflits = false;
        this.errorMessage = 'Erreur lors de la vérification des conflits';
      }
    });
  }

  saveSeance(): void {
    if (this.seanceForm.invalid) {
      return;
    }

    const formData: SeanceRequest = {
      titre: this.seanceForm.get('titre')?.value,
      description: this.seanceForm.get('description')?.value,
      dateHeureDebut: new Date(this.seanceForm.get('dateHeureDebut')?.value).toISOString(),
      dateHeureFin: new Date(this.seanceForm.get('dateHeureFin')?.value).toISOString(),
      formationId: this.seanceForm.get('formationId')?.value,
      salleId: this.seanceForm.get('salleId')?.value || undefined,
      formateurId: this.seanceForm.get('formateurId')?.value || undefined,
      statut: this.seanceForm.get('statut')?.value
    };

    const callObservable = this.isEditMode && this.editingSeanceId
      ? this.seanceService.updateSeance(this.editingSeanceId, formData)
      : this.seanceService.createSeance(formData, this.forceCreation);

    callObservable.subscribe({
      next: () => {
        this.successMessage = this.isEditMode ? 'Séance mise à jour avec succès' : 'Séance créée avec succès';
        this.closeModal();
        this.loadSeances();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Erreur lors de la sauvegarde';
      }
    });
  }

  changeStatut(seance: Seance): void {
    const currentIndex = this.statutOptions.indexOf(seance.statut);
    let nextStatut: SeanceStatut;

    if (seance.statut === 'PLANIFIEE') {
      nextStatut = 'EN_COURS';
    } else if (seance.statut === 'EN_COURS') {
      nextStatut = 'TERMINEE';
    } else {
      return;
    }

    this.seanceService.updateStatut(seance.id, nextStatut).subscribe({
      next: () => {
        seance.statut = nextStatut;
        this.successMessage = 'Statut de la séance mis à jour';
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Erreur lors de la mise à jour du statut';
      }
    });
  }

  confirmDelete(seanceId: number, seance: Seance): void {
    if (seance.statut === 'EN_COURS' || seance.statut === 'TERMINEE') {
      this.errorMessage = 'Impossible de supprimer une séance en cours ou terminée';
      return;
    }
    this.deleteConfirmId = seanceId;
  }

  cancelDelete(): void {
    this.deleteConfirmId = null;
  }

  deleteSeance(seanceId: number): void {
    this.seanceService.deleteSeance(seanceId).subscribe({
      next: () => {
        this.successMessage = 'Séance supprimée avec succès';
        this.deleteConfirmId = null;
        this.loadSeances();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Erreur lors de la suppression';
        this.deleteConfirmId = null;
      }
    });
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadSeances();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadSeances();
    }
  }

  clearMessages(): void {
    setTimeout(() => {
      this.successMessage = '';
      this.errorMessage = '';
    }, 3000);
  }

  getStatutColor(statut: SeanceStatut): string {
    switch (statut) {
      case 'PLANIFIEE': return 'badge-planifiee';
      case 'EN_COURS': return 'badge-en-cours';
      case 'TERMINEE': return 'badge-terminee';
      case 'ANNULEE': return 'badge-annulee';
      default: return '';
    }
  }

  getFormationName(formationId: number): string {
    const formation = this.formations.find(f => f.id === formationId);
    return formation ? formation.titre : '';
  }

  getFormateurName(formateurId: number): string {
    const formateur = this.formateurs.find(f => f.id === formateurId);
    return formateur ? `${formateur.nom} ${formateur.prenom}` : '';
  }

  getSalleName(salleId: number): string {
    const salle = this.salles.find(s => s.id === salleId);
    return salle ? salle.nom : '';
  }
}
