import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NoteService } from '../../services/note.service';
import { Note } from '../../models/planning.model';

@Component({
  selector: 'app-mes-notes-apprenant',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './mes-notes-apprenant.component.html',
  styleUrl: './mes-notes-apprenant.component.scss'
})
export class MesNotesApprenantComponent implements OnInit {
  notesByFormation: Map<string, Note[]> = new Map();
  loading = true;
  expandedFormations: Set<string> = new Set();

  constructor(private noteService: NoteService) {}

  ngOnInit(): void {
    this.loadNotes();
  }

  loadNotes(): void {
    this.noteService.getMesNotes().subscribe({
      next: (notes: Note[]) => {
        this.groupNotesByFormation(notes);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  groupNotesByFormation(notes: Note[]): void {
    const map = new Map<string, Note[]>();
    notes.forEach(note => {
      const key = note.seanceTitre;
      if (!map.has(key)) {
        map.set(key, []);
      }
      map.get(key)?.push(note);
    });
    this.notesByFormation = map;
  }

  toggleFormation(formation: string): void {
    if (this.expandedFormations.has(formation)) {
      this.expandedFormations.delete(formation);
    } else {
      this.expandedFormations.add(formation);
    }
  }

  isExpanded(formation: string): boolean {
    return this.expandedFormations.has(formation);
  }

  getMoyenneFormation(notes: Note[]): number {
    if (notes.length === 0) return 0;
    return notes.reduce((sum, n) => sum + n.valeur, 0) / notes.length;
  }

  getNoteColor(valeur: number): string {
    if (valeur >= 10) return 'note-vert';
    if (valeur >= 7) return 'note-orange';
    return 'note-rouge';
  }

  getMoyenneColor(moyenne: number): string {
    if (moyenne >= 10) return 'moyenne-vert';
    if (moyenne >= 7) return 'moyenne-orange';
    return 'moyenne-rouge';
  }
}
