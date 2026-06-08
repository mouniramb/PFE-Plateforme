import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { NoteService } from '../../services/note.service';
import { SeanceService } from '../../services/seance.service';
import { InscriptionService } from '../../services/inscription.service';
import { Seance, TypeEvaluation, NoteBulkRequest, Note } from '../../models/planning.model';

interface ApprenantNote {
  apprenantId: number;
  nom: string;
  prenom: string;
  note: number | null;
  commentaire: string;
}

@Component({
  selector: 'app-saisie-notes',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './saisie-notes.component.html',
  styleUrl: './saisie-notes.component.scss'
})
export class SaisieNotesComponent implements OnInit {
  seance: Seance | null = null;
  apprenants: ApprenantNote[] = [];
  seanceId: number = 0;

  typeEvaluation: TypeEvaluation = 'EXAMEN';
  typeOptions: TypeEvaluation[] = ['EXAMEN', 'DEVOIR', 'QUIZ', 'PROJET', 'CONTROLE'];
  notesParType: Record<string, Array<{ note: number | null; commentaire: string }>> = {};

  successMessage = '';
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private seanceService: SeanceService,
    private noteService: NoteService,
    private inscriptionService: InscriptionService
  ) {}

  ngOnInit(): void {
    this.seanceId = Number(this.route.snapshot.paramMap.get('seanceId'));
    if (this.seanceId) {
      this.loadSeance();
    }
  }

  loadSeance(): void {
    this.seanceService.getSeance(this.seanceId).subscribe({
      next: (seance: Seance) => {
        this.seance = seance;
        this.loadApprenants();
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement de la séance';
      }
    });
  }

  loadApprenants(): void {
    if (!this.seance) return;

    this.inscriptionService.getInscriptionsParFormation(this.seance.formation.id, 0, 100).subscribe({
      next: (response: any) => {
        const inscriptions: any[] = response.content ?? response;
        this.apprenants = inscriptions
          .filter((i: any) => i.statut === 'ACCEPTEE')
          .map((i: any) => ({
            apprenantId: i.apprenantId ?? i.apprenant?.id,
            nom: i.apprenantNom ?? i.apprenant?.nom,
            prenom: i.apprenantPrenom ?? i.apprenant?.prenom,
            note: null,
            commentaire: ''
          }));
        // Initialise un slot vide pour chaque type
        this.typeOptions.forEach(type => {
          this.notesParType[type] = this.apprenants.map(() => ({ note: null, commentaire: '' }));
        });
      },
      error: () => {
        this.errorMessage = 'Erreur lors du chargement des apprenants';
      }
    });
  }

  setType(type: TypeEvaluation): void {
    // Sauvegarde les notes actuelles dans le slot du type courant
    this.notesParType[this.typeEvaluation] = this.apprenants.map(a => ({
      note: a.note,
      commentaire: a.commentaire
    }));
    // Change le type
    this.typeEvaluation = type;
    // Restaure les notes du nouveau type (vides si jamais saisies)
    const saved = this.notesParType[type] ?? this.apprenants.map(() => ({ note: null, commentaire: '' }));
    this.apprenants.forEach((a, i) => {
      a.note        = saved[i]?.note ?? null;
      a.commentaire = saved[i]?.commentaire ?? '';
    });
    this.successMessage = '';
    this.errorMessage   = '';
  }

  enregistrerNotes(): void {
    const notesValides = this.apprenants
      .filter(a => a.note !== null && a.note >= 0 && a.note <= 20)
      .map(a => ({
        apprenantId: a.apprenantId,
        valeur: a.note!,
        coefficient: 1,
        typeEvaluation: this.typeEvaluation,
        commentaire: a.commentaire || undefined
      }));

    if (notesValides.length === 0) {
      this.errorMessage = 'Aucune note valide à enregistrer';
      return;
    }

    const bulk: NoteBulkRequest = {
      seanceId: this.seanceId,
      notes: notesValides
    };

    this.noteService.enregistrerNotesBulk(bulk).subscribe({
      next: () => {
        this.successMessage = 'Notes enregistrées avec succès';
        setTimeout(() => {
          this.router.navigate(['/formateur/dashboard']);
        }, 1500);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Erreur lors de l\'enregistrement';
      }
    });
  }

  getNoteColor(note: number | null): string {
    if (note === null) return '';
    if (note >= 10) return 'note-vert';
    if (note >= 7) return 'note-orange';
    return 'note-rouge';
  }

  getMoyenne(): number {
    const notesValides = this.apprenants.filter(a => a.note !== null).map(a => a.note!);
    if (notesValides.length === 0) return 0;
    return notesValides.reduce((a, b) => a + b, 0) / notesValides.length;
  }

  goBack(): void {
    this.router.navigate(['/formateur/dashboard']);
  }
}
