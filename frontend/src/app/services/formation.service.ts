import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import {
  Formation,
  FormationRequest,
  PageResponse,
  StatutFormation
} from '../models/formation.model';

@Injectable({
  providedIn: 'root'
})
export class FormationService {
  private readonly API_URL = 'http://localhost:8080/api/formations';

  constructor(private http: HttpClient) {}

  getAllFormations(
    page: number,
    size: number,
    statut?: StatutFormation
  ): Observable<PageResponse<Formation>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (statut) {
      params = params.set('statut', statut);
    }

    return this.http
      .get<PageResponse<Formation>>(this.API_URL, { params })
      .pipe(map(response => this.normalizePage(response)));
  }

  getFormation(id: number): Observable<Formation> {
    return this.http
      .get<Formation>(`${this.API_URL}/${id}`)
      .pipe(map(formation => this.normalizeFormation(formation)));
  }

  createFormation(data: FormationRequest): Observable<Formation> {
    return this.http
      .post<Formation>(this.API_URL, data)
      .pipe(map(formation => this.normalizeFormation(formation)));
  }

  updateFormation(id: number, data: FormationRequest): Observable<Formation> {
    return this.http
      .put<Formation>(`${this.API_URL}/${id}`, data)
      .pipe(map(formation => this.normalizeFormation(formation)));
  }

  deleteFormation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  getCatalogueFormations(keyword?: string): Observable<Formation[]> {
    if (keyword?.trim()) {
      return this.searchFormations(keyword.trim());
    }

    return this.http
      .get<PageResponse<Formation> | Formation[]>(`${this.API_URL}/catalogue`)
      .pipe(map(response => this.extractFormationArray(response)));
  }

  getFormationsByFormateur(
    formateurId: number,
    page: number,
    size: number
  ): Observable<PageResponse<Formation>> {
    const params = new HttpParams().set('page', page).set('size', size);

    return this.http
      .get<PageResponse<Formation>>(`${this.API_URL}/formateur/${formateurId}`, { params })
      .pipe(map(response => this.normalizePage(response)));
  }

  searchFormations(keyword: string): Observable<Formation[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http
      .get<PageResponse<Formation> | Formation[]>(`${this.API_URL}/search`, { params })
      .pipe(map(response => this.extractFormationArray(response)));
  }

  private normalizePage(response: PageResponse<Formation>): PageResponse<Formation> {
    return {
      ...response,
      content: response.content.map(item => this.normalizeFormation(item))
    };
  }

  private extractFormationArray(response: PageResponse<Formation> | Formation[]): Formation[] {
    if (Array.isArray(response)) {
      return response.map(item => this.normalizeFormation(item));
    }

    return response.content.map(item => this.normalizeFormation(item));
  }

  private normalizeFormation(formation: Formation): Formation {
    const capaciteMax = formation.capaciteMax ?? 0;
    const capaciteActuelle = formation.capaciteActuelle ?? 0;
    const placesRestantes =
      formation.placesRestantes ?? Math.max(capaciteMax - capaciteActuelle, 0);

    return {
      ...formation,
      formateurs: formation.formateurs ?? [],
      capaciteMax,
      capaciteActuelle,
      placesRestantes,
      placesDisponibles: placesRestantes > 0
    };
  }
}
