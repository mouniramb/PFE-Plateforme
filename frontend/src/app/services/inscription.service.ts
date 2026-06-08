import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { Inscription, PageResponse } from '../models/formation.model';

@Injectable({
  providedIn: 'root'
})
export class InscriptionService {
  private readonly API_URL = 'http://localhost:8080/api/inscriptions';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  sInscrire(formationId: number): Observable<Inscription> {
    return this.http
      .post<Inscription>(`${this.API_URL}/s-inscrire`, { formationId })
      .pipe(map(item => this.normalizeInscription(item)));
  }

  getMesInscriptions(): Observable<Inscription[]> {
    const apprenantId = this.authService.getCurrentUser()?.id;

    if (!apprenantId) {
      return throwError(() => new Error('Utilisateur non connecté'));
    }

    const params = new HttpParams().set('page', 0).set('size', 100);

    return this.http
      .get<PageResponse<Inscription> | Inscription[]>(`${this.API_URL}/apprenant/${apprenantId}`, { params })
      .pipe(map(response => this.extractInscriptionArray(response)));
  }

  annulerInscription(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}/annuler`);
  }

  getInscriptionsEnAttente(page: number, size: number): Observable<PageResponse<Inscription>> {
    const params = new HttpParams().set('page', page).set('size', size);

    return this.http
      .get<PageResponse<Inscription>>(`${this.API_URL}/en-attente`, { params })
      .pipe(map(response => this.normalizePage(response)));
  }

  accepterInscription(id: number): Observable<Inscription> {
    return this.http
      .put<Inscription>(`${this.API_URL}/${id}/accepter`, {})
      .pipe(map(item => this.normalizeInscription(item)));
  }

  rejeterInscription(id: number, motif: string): Observable<Inscription> {
    return this.http
      .put<Inscription>(`${this.API_URL}/${id}/rejeter`, { motif })
      .pipe(map(item => this.normalizeInscription(item)));
  }

  getInscriptionsParFormation(
    formationId: number,
    page: number,
    size: number
  ): Observable<PageResponse<Inscription>> {
    const params = new HttpParams().set('page', page).set('size', size);

    return this.http
      .get<PageResponse<Inscription>>(`${this.API_URL}/formation/${formationId}`, { params })
      .pipe(map(response => this.normalizePage(response)));
  }

  // Retourne les formations d'un apprenant (ACCEPTEE seulement)
  getFormationsApprenant(apprenantId: number): Observable<Inscription[]> {
    const params = new HttpParams().set('page', 0).set('size', 100);
    return this.http
      .get<PageResponse<Inscription> | Inscription[]>(
        `${this.API_URL}/apprenant/${apprenantId}`, { params }
      )
      .pipe(
        map(response => {
          const all = this.extractInscriptionArray(response);
          return all.filter(i => i.statut === 'ACCEPTEE');
        })
      );
  }

  getInscritsAcceptes(formationId: number): Observable<Inscription[]> {
    return this.http
      .get<PageResponse<Inscription> | Inscription[]>(`${this.API_URL}/formation/${formationId}/acceptes`)
      .pipe(
        map(response => this.extractInscriptionArray(response)),
        catchError(() =>
          this.getInscriptionsParFormation(formationId, 0, 100).pipe(
            map(response => response.content)
          )
        )
      );
  }

  private normalizePage(response: PageResponse<Inscription>): PageResponse<Inscription> {
    return {
      ...response,
      content: response.content.map(item => this.normalizeInscription(item))
    };
  }

  private extractInscriptionArray(response: PageResponse<Inscription> | Inscription[]): Inscription[] {
    if (Array.isArray(response)) {
      return response.map(item => this.normalizeInscription(item));
    }

    return response.content.map(item => this.normalizeInscription(item));
  }

  private normalizeInscription(item: Inscription): Inscription {
    return {
      ...item,
      formationDateDebut: item.formationDateDebut ?? '',
      formationDateFin: item.formationDateFin ?? ''
    };
  }
}
