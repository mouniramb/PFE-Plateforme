import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Presence, PresenceRequest, PresenceBulkRequest, TauxPresenceDTO } from '../models/planning.model';

@Injectable({
  providedIn: 'root'
})
export class PresenceService {
  private readonly API = 'http://localhost:8080/api/presences';

  constructor(private http: HttpClient) {}

  enregistrerPresence(data: PresenceRequest): Observable<Presence> {
    return this.http.post<Presence>(this.API, data);
  }

  enregistrerPresencesBulk(data: PresenceBulkRequest): Observable<Presence[]> {
    return this.http.post<Presence[]>(`${this.API}/bulk`, data);
  }

  getPresencesParSeance(seanceId: number): Observable<Presence[]> {
    return this.http.get<Presence[]>(`${this.API}/seance/${seanceId}`);
  }

  getPresencesApprenant(apprenantId: number): Observable<Presence[]> {
    return this.http.get<Presence[]>(`${this.API}/apprenant/${apprenantId}`);
  }

  getPresencesApprenantFormation(apprenantId: number, formationId: number): Observable<Presence[]> {
    return this.http.get<Presence[]>(
      `${this.API}/apprenant/${apprenantId}/formation/${formationId}`
    );
  }

  getTauxPresence(apprenantId: number, formationId: number): Observable<TauxPresenceDTO> {
    return this.http.get<TauxPresenceDTO>(
      `${this.API}/apprenant/${apprenantId}/formation/${formationId}/taux`
    );
  }

  getMesPresences(): Observable<Presence[]> {
    return this.http.get<Presence[]>(`${this.API}/mes-presences`);
  }
}
