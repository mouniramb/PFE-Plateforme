import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FormationConfig, Paiement, PaiementRequest, PaiementValidationRequest } from '../models/financial.model';
import { PageResponse } from '../models/formation.model';

@Injectable({ providedIn: 'root' })
export class PaiementService {
  private readonly API = 'http://localhost:8080/api/paiements';
  private readonly FORMATION_API = 'http://localhost:8080/api/formations';

  constructor(private http: HttpClient) {}

  enregistrerPaiement(data: PaiementRequest): Observable<Paiement> {
    return this.http.post<Paiement>(this.API, data);
  }

  validerPaiement(id: number, data: PaiementValidationRequest): Observable<Paiement> {
    return this.http.put<Paiement>(`${this.API}/${id}/valider`, data);
  }

  rejeterPaiement(id: number, raison: string): Observable<Paiement> {
    return this.http.put<Paiement>(`${this.API}/${id}/rejeter`, { raison });
  }

  deletePaiement(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }

  getPaiementsEnAttente(page = 0, size = 10): Observable<PageResponse<Paiement>> {
    return this.http.get<PageResponse<Paiement>>(`${this.API}/en-attente`, {
      params: { page, size }
    });
  }

  getPaiementsApprenant(apprenantId: number): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(`${this.API}/apprenant/${apprenantId}`);
  }

  getPaiementsApprenantFormation(apprenantId: number, formationId: number): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(`${this.API}/apprenant/${apprenantId}/formation/${formationId}`);
  }

  getMesPaiements(): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(`${this.API}/mes-paiements`);
  }

  getPaiementsFormateur(formateurId: number): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(`${this.API}/formateur/${formateurId}`);
  }

  getFormationConfig(formationId: number): Observable<FormationConfig> {
    return this.http.get<FormationConfig>(`${this.FORMATION_API}/${formationId}/config`);
  }

  saveFormationConfig(formationId: number, config: Partial<FormationConfig>): Observable<FormationConfig> {
    return this.http.put<FormationConfig>(`${this.FORMATION_API}/${formationId}/config`, config);
  }
}
