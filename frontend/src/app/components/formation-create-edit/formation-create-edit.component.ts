import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  Formation,
  FormationRequest,
  StatutFormation
} from '../../models/formation.model';
import { UserResponse } from '../../models/user.model';
import { AuthService } from '../../services/auth.service';
import { FormationService } from '../../services/formation.service';
import { UserService } from '../../services/user.service';
import { mapHttpErrorMessage } from '../../utils/http-error.util';

function dateValidator(group: AbstractControl): ValidationErrors | null {
  const dateDebut = group.get('dateDebut')?.value;
  const dateFin = group.get('dateFin')?.value;

  if (!dateDebut || !dateFin) {
    return null;
  }

  return new Date(dateFin) <= new Date(dateDebut) ? { invalidDates: true } : null;
}

@Component({
  selector: 'app-formation-create-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './formation-create-edit.component.html',
  styleUrls: ['./formation-create-edit.component.scss']
})
export class FormationCreateEditComponent implements OnInit {
  form!: FormGroup;
  isLoading = false;
  formLoading = false;
  isEditMode = false;
  formationId: number | null = null;

  formateurs: UserResponse[] = [];
  selectedFormateurIds = new Set<number>();

  successMessage = '';
  errorMessage = '';

  readonly statuts: StatutFormation[] = ['PLANIFIEE', 'EN_COURS', 'TERMINEE'];
  readonly currentUser = this.authService.getCurrentUser();

  constructor(
    private fb: FormBuilder,
    private formationService: FormationService,
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group(
      {
        titre: ['', [Validators.required, Validators.minLength(3)]],
        description: ['', [Validators.required]],
        duree: [1, [Validators.required, Validators.min(1)]],
        dateDebut: ['', [Validators.required]],
        dateFin: ['', [Validators.required]],
        capaciteMax: [1, [Validators.required, Validators.min(1)]],
        prix: [0, [Validators.required, Validators.min(0), Validators.max(800)]],
        statut: ['PLANIFIEE', [Validators.required]]
      },
      { validators: [dateValidator] }
    );

    this.loadFormateurs();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.formationId = Number(idParam);
      this.loadFormation(this.formationId);
    }
  }

  loadFormateurs(): void {
    this.userService.getAllFormateurs().subscribe({
      next: data => {
        this.formateurs = data;
      },
      error: err => {
        this.showError(mapHttpErrorMessage(err, 'Erreur lors du chargement des formateurs.'));
      }
    });
  }

  loadFormation(id: number): void {
    this.isLoading = true;
    this.formationService.getFormation(id).subscribe({
      next: (formation: Formation) => {
        this.form.patchValue({
          titre: formation.titre,
          description: formation.description,
          duree: formation.duree,
          dateDebut: formation.dateDebut,
          dateFin: formation.dateFin,
          capaciteMax: formation.capaciteMax,
          prix: formation.prix,
          statut: formation.statut
        });

        this.selectedFormateurIds = new Set(
          formation.formateurs.map(formateur => formateur.id)
        );

        this.isLoading = false;
      },
      error: err => {
        this.isLoading = false;
        this.showError(mapHttpErrorMessage(err, 'Impossible de charger la formation.'));
      }
    });
  }

  toggleFormateur(formateurId: number): void {
    if (this.selectedFormateurIds.has(formateurId)) {
      this.selectedFormateurIds.delete(formateurId);
      return;
    }

    this.selectedFormateurIds.add(formateurId);
  }

  isSelected(formateurId: number): boolean {
    return this.selectedFormateurIds.has(formateurId);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.formLoading = true;

    const payload: FormationRequest = {
      ...this.form.value,
      formateurIds: Array.from(this.selectedFormateurIds)
    };

    if (this.isEditMode && this.formationId !== null) {
      this.formationService.updateFormation(this.formationId, payload).subscribe({
        next: () => {
          this.formLoading = false;
          this.successMessage = 'Formation mise à jour avec succès.';
          setTimeout(() => {
            this.router.navigate(['/admin/formations']);
          }, 700);
        },
        error: err => {
          this.formLoading = false;
          this.showError(mapHttpErrorMessage(err, 'Erreur lors de la mise à jour de la formation.'));
        }
      });
      return;
    }

    this.formationService.createFormation(payload).subscribe({
      next: () => {
        this.formLoading = false;
        this.successMessage = 'Formation créée avec succès.';
        setTimeout(() => {
          this.router.navigate(['/admin/formations']);
        }, 700);
      },
      error: err => {
        this.formLoading = false;
        this.showError(mapHttpErrorMessage(err, 'Erreur lors de la création de la formation.'));
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/admin/formations']);
  }

  logout(): void {
    this.authService.logout();
  }

  private showError(message: string): void {
    this.errorMessage = message;
    this.successMessage = '';
    setTimeout(() => {
      this.errorMessage = '';
    }, 5000);
  }
}
