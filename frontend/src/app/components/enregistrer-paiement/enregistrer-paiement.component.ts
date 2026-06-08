import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PaiementService } from '../../services/paiement.service';
import { PaiementFormateurService } from '../../services/paiement-formateur.service';
import { FormationService } from '../../services/formation.service';
import { InscriptionService } from '../../services/inscription.service';
import { SeanceService } from '../../services/seance.service';
import { UserService } from '../../services/user.service';
import { FormationConfig, ModePaiement } from '../../models/financial.model';
import { Formation } from '../../models/formation.model';
import { Seance } from '../../models/planning.model';
import { UserResponse } from '../../models/user.model';

@Component({
  selector: 'app-enregistrer-paiement',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './enregistrer-paiement.component.html',
  styleUrls: ['./enregistrer-paiement.component.scss']
})
export class EnregistrerPaiementComponent implements OnInit {

  @Input() embedded = false;
  @Output() saved = new EventEmitter<void>();
  @Output() closed = new EventEmitter<void>();

  currentUser = this.authService.getCurrentUser();
  isAdmin     = this.authService.isAdmin();
  isFormateur = this.authService.isFormateur();

  // 'APPRENANT' = enregistrer paiement d'un apprenant (avec remise + dates)
  // 'FORMATEUR' = enregistrer paiement d'un formateur (sans remise)
  typePaiement: 'APPRENANT' | 'FORMATEUR' = 'APPRENANT';

  get isForApprenantMode(): boolean { return this.typePaiement === 'APPRENANT'; }
  get isForFormateurMode(): boolean { return this.typePaiement === 'FORMATEUR'; }

  formateurs:       UserResponse[]     = [];
  apprenants:       UserResponse[]     = [];
  formations:       Formation[]        = [];
  seances:          Seance[]           = [];
  config:           FormationConfig | null = null;
  selectedFormation: Formation | null  = null;

  isLoadingFormateurs = false;
  isLoadingApprenants = false;
  isLoadingFormations = false;
  isLoadingSeances    = false;
  isSaving            = false;
  successMessage      = '';
  errorMessage        = '';

  paiementForm!: FormGroup;
  selectedMode: ModePaiement | null = null;

  // Tranches dynamiques
  nombreTranches = 4;
  readonly TRANCHE_OPTIONS = [2,3,4,5,6,7,8,9];

  readonly MOIS_LABELS = ['Janvier','Février','Mars','Avril','Mai','Juin',
                          'Juillet','Août','Septembre','Octobre','Novembre','Décembre'];

  constructor(
    private authService: AuthService,
    private paiementService: PaiementService,
    private formationService: FormationService,
    private inscriptionService: InscriptionService,
    private seanceService: SeanceService,
    private userService: UserService,
    private paiementFormateurService: PaiementFormateurService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadApprenants();
    this.loadFormateurs();
    this.loadFormations();
  }

  setTypePaiement(type: 'APPRENANT' | 'FORMATEUR'): void {
    this.typePaiement = type;
    this.selectedMode = null;
    this.config = null;
    this.seances = [];
    this.paiementForm.reset({
      formateurId:  null,
      apprenantId:  null,
      formationId:  null,
      modePaiement: null,
      montant:      null,
      remise:       0,
      trancheNumber:null,
      seanceId:     null,
      moisAnnuaire: null,
      datePaiement: new Date().toISOString().substring(0, 10),
      notes:        ''
    });
    if (type === 'FORMATEUR') {
      this.paiementForm.get('apprenantId')!.clearValidators();
      this.paiementForm.get('apprenantId')!.updateValueAndValidity();
      this.paiementForm.get('formateurId')!.setValidators([Validators.required]);
      this.paiementForm.get('formateurId')!.updateValueAndValidity();
    } else {
      this.paiementForm.get('apprenantId')!.setValidators([Validators.required]);
      this.paiementForm.get('apprenantId')!.updateValueAndValidity();
      this.paiementForm.get('formateurId')!.clearValidators();
      this.paiementForm.get('formateurId')!.updateValueAndValidity();
    }
  }

  initForm(): void {
    this.paiementForm = this.fb.group({
      formateurId:  [null],
      apprenantId:  [null, Validators.required],
      formationId:  [null, Validators.required],
      modePaiement: [null, Validators.required],
      montant:      [null, [Validators.required, Validators.min(0.01)]],
      remise:       [0],
      trancheNumber:[null],
      seanceId:     [null],
      moisAnnuaire: [null],
      datePaiement: [new Date().toISOString().substring(0, 10), Validators.required],
      notes:        ['']
    });

    this.paiementForm.get('apprenantId')!.valueChanges.subscribe(id => {
      if (id) this.onApprenantChange(+id);
    });

    this.paiementForm.get('formateurId')!.valueChanges.subscribe(id => {
      if (id) this.loadFormationsByFormateur(+id);
    });

    this.paiementForm.get('formationId')!.valueChanges.subscribe(id => {
      if (id) this.onFormationChange(+id);
    });

    this.paiementForm.get('modePaiement')!.valueChanges.subscribe(mode => {
      this.onModeChange(mode);
    });

    this.paiementForm.get('trancheNumber')!.valueChanges.subscribe(() => this.updateMontant());
    this.paiementForm.get('seanceId')!.valueChanges.subscribe(() => this.updateMontant());
    this.paiementForm.get('moisAnnuaire')!.valueChanges.subscribe(() => this.updateMontant());
  }

  loadFormateurs(): void {
    this.isLoadingFormateurs = true;
    this.userService.getAllFormateurs().subscribe({
      next: (data) => { this.formateurs = data; this.isLoadingFormateurs = false; },
      error: () => { this.isLoadingFormateurs = false; }
    });
  }

  loadApprenants(): void {
    this.isLoadingApprenants = true;
    this.userService.getAllApprenants().subscribe({
      next: (data) => { this.apprenants = data; this.isLoadingApprenants = false; },
      error: () => { this.isLoadingApprenants = false; }
    });
  }

  loadFormations(): void {
    this.isLoadingFormations = true;
    this.formationService.getCatalogueFormations().subscribe({
      next: (data) => {
        this.formations = data;
        this.isLoadingFormations = false;
        console.log('Formations chargées:', data.length);
      },
      error: (err) => {
        console.error('Erreur chargement formations:', err);
        this.errorMessage = 'Erreur lors du chargement des formations.';
        this.isLoadingFormations = false;
      }
    });
  }

  loadFormationsByFormateur(formateurId: number): void {
    this.isLoadingFormations = true;
    this.formations = [];
    this.formationService.getFormationsByFormateur(formateurId, 0, 100).subscribe({
      next: (page) => {
        this.formations = page.content ?? [];
        this.isLoadingFormations = false;
        if (this.formations.length === 0) {
          this.errorMessage = 'Ce formateur n\'enseigne aucune formation.';
        } else {
          this.errorMessage = '';
        }
      },
      error: () => {
        // Fallback : toutes les formations filtrées côté client
        this.formationService.getCatalogueFormations().subscribe({
          next: (data) => {
            this.formations = data.filter(f =>
              f.formateurs?.some(fmt => fmt.id === formateurId) ?? false
            );
            this.isLoadingFormations = false;
          },
          error: () => { this.isLoadingFormations = false; }
        });
      }
    });
  }

  onApprenantChange(apprenantId: number): void {
    this.paiementForm.patchValue({ formationId: null, modePaiement: null });
    this.selectedMode = null;
    this.config = null;
    this.formations = [];
    this.isLoadingFormations = true;
    this.errorMessage = '';

    // 1. Charger les inscriptions de l'apprenant pour avoir les IDs
    this.inscriptionService.getFormationsApprenant(apprenantId).subscribe({
      next: (inscriptions) => {
        if (inscriptions.length === 0) {
          this.isLoadingFormations = false;
          this.errorMessage = 'Cet apprenant n\'a aucune inscription acceptée.';
          return;
        }
        const formationIds = inscriptions.map(i => i.formationId);

        // 2. Charger toutes les formations (admin) et filtrer par les IDs inscrits
        this.formationService.getAllFormations(0, 200).subscribe({
          next: (page) => {
            const allFormations = page.content ?? [];
            this.formations = allFormations.filter(f => formationIds.includes(f.id));
            this.isLoadingFormations = false;
            if (this.formations.length === 0) {
              // Fallback : données de l'inscription (sans prix)
              this.formations = inscriptions.map(i => ({
                id:        i.formationId,
                titre:     i.formationTitre,
                prix:      0,
                dateDebut: i.formationDateDebut ?? '',
                dateFin:   i.formationDateFin   ?? '',
                description: '', duree: 0, capaciteMax: 0, capaciteActuelle: 0,
                placesRestantes: 0, placesDisponibles: true, statut: 'EN_COURS' as any,
                dateCreation: '', dateModification: '', formateurs: []
              }));
            }
          },
          error: () => {
            // Fallback catalogue public
            this.formationService.getCatalogueFormations().subscribe({
              next: (allFormations) => {
                this.formations = allFormations.filter(f => formationIds.includes(f.id));
                this.isLoadingFormations = false;
              },
              error: () => { this.isLoadingFormations = false; }
            });
          }
        });
      },
      error: () => {
        this.isLoadingFormations = false;
        this.errorMessage = 'Erreur lors du chargement des formations de l\'apprenant.';
      }
    });
  }

  selectFormation(f: Formation): void {
    this.selectedFormation = f;
    const prix = f.prix && f.prix > 0 ? f.prix : null;
    this.paiementForm.patchValue({
      formationId: f.id,
      montant: prix,
      modePaiement: null,
      trancheNumber: null,
      seanceId: null,
      moisAnnuaire: null
    });
    this.selectedMode = null;
    this.onFormationChange(f.id);
  }

  onFormationChange(formationId: number): void {
    this.seances = [];
    this.config = null;
    if (!this.selectedFormation || this.selectedFormation.id !== formationId) {
      this.selectedFormation = this.formations.find(f => f.id === +formationId) ?? null;
    }
    this.paiementForm.patchValue({ modePaiement: null, trancheNumber: null, seanceId: null, moisAnnuaire: null });
    this.selectedMode = null;
    // Montant = prix de la formation sélectionnée
    if (this.selectedFormation?.prix && this.selectedFormation.prix > 0) {
      this.paiementForm.patchValue({ montant: this.selectedFormation.prix }, { emitEvent: false });
    }

    this.paiementService.getFormationConfig(formationId).subscribe({
      next: (cfg) => {
        this.config = cfg;
        this.recalcTranches();
      },
      error: () => {}
    });

    this.isLoadingSeances = true;
    this.seanceService.getSeancesByFormation(formationId, 0, 100).subscribe({
      next: (page: any) => {
        this.seances = page.content ?? [];
        this.isLoadingSeances = false;
      },
      error: () => { this.isLoadingSeances = false; }
    });
  }

  onModeChange(mode: ModePaiement): void {
    this.selectedMode = mode;
    this.paiementForm.patchValue({ trancheNumber: null, seanceId: null, moisAnnuaire: null, remise: 0 });
    this.clearModeValidators();

    if (mode === 'FORMATION' && this.config) {
      this.paiementForm.patchValue({ montant: this.config.prixFormation }, { emitEvent: false });
    }
    if (mode === 'TRANCHE') {
      // Utiliser le prix de la formation (pas config) pour le calcul des tranches
      const prix = this.selectedFormation?.prix ?? this.config?.prixFormation;
      if (prix) {
        this.paiementForm.patchValue({ montant: prix }, { emitEvent: false });
      }
      this.paiementForm.get('trancheNumber')!.setValidators([Validators.required, Validators.min(1)]);
    }
    if (mode === 'SEANCE' && this.config?.prixParSeance) {
      this.paiementForm.patchValue({ montant: this.config.prixParSeance }, { emitEvent: false });
    }
    if (mode === 'SEANCE') {
      this.paiementForm.get('seanceId')!.setValidators([Validators.required]);
    }
    if (mode === 'ANNUAIRE') {
      this.paiementForm.get('moisAnnuaire')!.setValidators([Validators.required, Validators.min(1), Validators.max(12)]);
    }
    ['trancheNumber','seanceId','moisAnnuaire'].forEach(f => {
      this.paiementForm.get(f)!.updateValueAndValidity();
    });
    this.updateMontant();
  }

  onNombreTranchesChange(n: number): void {
    this.nombreTranches = n;
    this.recalcTranches();
    this.paiementForm.patchValue({ trancheNumber: null });
  }

  recalcTranches(): void {
    // Tranches recalculées dynamiquement via le getter tranchesCalc
  }

  selectTranche(num: number, montant: number): void {
    this.paiementForm.get('trancheNumber')!.setValue(num);
    this.paiementForm.patchValue({ montant: montant }, { emitEvent: false });
  }

  get tranchesCalc(): { num: number; montant: number }[] {
    if (this.nombreTranches < 2) return [];
    // Utiliser le montant saisi dans le formulaire ou le prix de la config
    const total = +(this.paiementForm.get('montant')?.value ?? this.config?.prixFormation ?? 0);
    if (total <= 0) return [];
    const montantUnitaire = Math.round((total / this.nombreTranches) * 100) / 100;
    return Array.from({ length: this.nombreTranches }, (_, i) => ({
      num: i + 1,
      montant: montantUnitaire
    }));
  }

  clearModeValidators(): void {
    ['trancheNumber', 'seanceId', 'moisAnnuaire'].forEach(f => {
      this.paiementForm.get(f)!.clearValidators();
      this.paiementForm.get(f)!.updateValueAndValidity();
    });
  }

  updateMontant(): void {
    if (!this.config) return;
    const mode = this.paiementForm.get('modePaiement')!.value;
    if (mode === 'SEANCE' && this.config.prixParSeance) {
      this.paiementForm.patchValue({ montant: this.config.prixParSeance }, { emitEvent: false });
    }
    if (mode === 'ANNUAIRE' && this.config.montantMensuel) {
      this.paiementForm.patchValue({ montant: this.config.montantMensuel }, { emitEvent: false });
    }
    if (mode === 'TRANCHE') {
      const num = this.paiementForm.get('trancheNumber')!.value;
      if (num) {
        const t = this.tranchesCalc.find(x => x.num === +num);
        if (t) this.paiementForm.patchValue({ montant: t.montant }, { emitEvent: false });
      }
    }
  }

  get montantNet(): number {
    const m = +this.paiementForm.get('montant')!.value || 0;
    const r = +this.paiementForm.get('remise')!.value || 0;
    return Math.max(m - r, 0);
  }

  getMoisLabel(m: number): string {
    return this.MOIS_LABELS[m - 1] ?? `Mois ${m}`;
  }

  formatDT(v: number | null | undefined): string {
    return (v ?? 0) + ' DT';
  }

  submit(): void {
    if (this.paiementForm.invalid) {
      this.paiementForm.markAllAsTouched();
      return;
    }
    this.isSaving = true;
    this.errorMessage = '';

    const val = this.paiementForm.value;

    // En mode formateur : utiliser formateurId comme apprenantId
    // (le formateur est l'utilisateur lié au paiement)
    const beneficiaireId = this.isForFormateurMode
      ? +val.formateurId
      : +val.apprenantId;

    if (!beneficiaireId || beneficiaireId <= 0) {
      this.errorMessage = this.isForFormateurMode
        ? 'Veuillez sélectionner un formateur.'
        : 'Veuillez sélectionner un apprenant.';
      this.isSaving = false;
      return;
    }

    const payload: any = {
      apprenantId:  beneficiaireId,
      formationId:  +val.formationId,
      modePaiement: val.modePaiement,
      montant:      +val.montant,
      remise:       this.isForFormateurMode ? 0 : (+val.remise || 0),
      datePaiement: val.datePaiement,
      notes:        val.notes || null
    };
    if (val.trancheNumber) payload.trancheNumber = +val.trancheNumber;
    if (val.seanceId)      payload.seanceId      = +val.seanceId;
    if (val.moisAnnuaire)  payload.moisAnnuaire  = +val.moisAnnuaire;

    if (this.isForFormateurMode) {
      // Paiement formateur → nouvelle table dédiée
      const forPayload = {
        formateurId:    beneficiaireId,
        formationId:    +val.formationId,
        montant:        +val.montant,
        modePaiement:   val.modePaiement,
        nombreTranches: val.trancheNumber ? this.nombreTranches : undefined,
        numeroTranche:  val.trancheNumber ? +val.trancheNumber : undefined,
        datePaiement:   val.datePaiement,
        notes:          val.notes || undefined
      };
      this.paiementFormateurService.enregistrer(forPayload).subscribe({
        next: () => {
          this.successMessage = 'Paiement formateur enregistré.';
          this.isSaving = false;
          if (this.embedded) { setTimeout(() => this.saved.emit(), 1200); }
          else { setTimeout(() => this.router.navigate(['/admin/paiements']), 2000); }
        },
        error: (err) => {
          this.errorMessage = err?.error?.message || "Erreur lors de l'enregistrement";
          this.isSaving = false;
        }
      });
    } else {
      this.paiementService.enregistrerPaiement(payload).subscribe({
        next: () => {
          this.successMessage = 'Paiement enregistré. En attente de validation.';
          this.isSaving = false;
          if (this.embedded) { setTimeout(() => this.saved.emit(), 1200); }
          else { setTimeout(() => this.router.navigate(['/admin/paiements']), 2000); }
        },
        error: (err) => {
          this.errorMessage = err?.error?.message || "Erreur lors de l'enregistrement";
          this.isSaving = false;
        }
      });
    }
  }

  annuler(): void {
    if (this.embedded) {
      this.closed.emit();
    } else {
      this.router.navigate(['/admin/paiements']);
    }
  }

  logout(): void { this.authService.logout(); }
}
