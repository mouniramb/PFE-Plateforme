import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Note, NoteRequest, NoteBulkRequest, MoyenneDTO } from '../models/planning.model';

@Injectable({
  providedIn: 'root'
})
export class NoteService {
  private readonly API = 'http://localhost:8080/api/notes';

  constructor(private http: HttpClient) {}

  enregistrerNote(data: NoteRequest): Observable<Note> {
    return this.http.post<Note>(this.API, data);
  }

  enregistrerNotesBulk(data: NoteBulkRequest): Observable<Note[]> {
    return this.http.post<Note[]>(`${this.API}/bulk`, data);
  }

  getNotesParSeance(seanceId: number): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.API}/seance/${seanceId}`);
  }

  getNotesApprenant(apprenantId: number): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.API}/apprenant/${apprenantId}`);
  }

  getNotesApprenantFormation(apprenantId: number, formationId: number): Observable<Note[]> {
    return this.http.get<Note[]>(
      `${this.API}/apprenant/${apprenantId}/formation/${formationId}`
    );
  }

  getMoyenne(apprenantId: number, formationId: number): Observable<MoyenneDTO> {
    return this.http.get<MoyenneDTO>(
      `${this.API}/apprenant/${apprenantId}/formation/${formationId}/moyenne`
    );
  }

  getMesNotes(): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.API}/mes-notes`);
  }
}
