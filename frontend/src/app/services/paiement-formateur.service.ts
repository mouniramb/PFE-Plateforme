import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PaiementFormateurRequest {
  formateurId:    number;
  formationId:    number;
  montant:        number;
  modePaiement:   string;
  nombreTranches?: number;
  numeroTranche?:  number;
  datePaiement:   string;
  notes?:         string;
}

export interface PaiementFormateurResponse {
  id:              number;
  formateurId:     number;
  formateurNom:    string;
  formateurPrenom: string;
  formationId:     number;
  formationTitre:  string;
  montant:         number;
  montantParTranche?: number;
  modePaiement:    string;
  nombreTranches?: number;
  numeroTranche?:  number;
  datePaiement:    string;
  statut:          string;
  notes?:          string;
  dateCreation:    string;
}

@Injectable({ providedIn: 'root' })
export class PaiementFormateurService {

  private readonly API = 'http://localhost:8080/api/paiements-formateurs';

  constructor(private http: HttpClient) {}

  enregistrer(data: PaiementFormateurRequest): Observable<PaiementFormateurResponse> {
    return this.http.post<PaiementFormateurResponse>(this.API, data);
  }

  getMesRevenus(): Observable<PaiementFormateurResponse[]> {
    return this.http.get<PaiementFormateurResponse[]>(`${this.API}/mes-revenus`);
  }

  getByFormateur(formateurId: number): Observable<PaiementFormateurResponse[]> {
    return this.http.get<PaiementFormateurResponse[]>(`${this.API}/formateur/${formateurId}`);
  }

  getAll(): Observable<PaiementFormateurResponse[]> {
    return this.http.get<PaiementFormateurResponse[]>(this.API);
  }
}
