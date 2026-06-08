import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StatistiquesApprenant } from '../models/planning.model';

@Injectable({
  providedIn: 'root'
})
export class SuiviService {
  private readonly API = 'http://localhost:8080/api/suivi';

  constructor(private http: HttpClient) {}

  getSuiviApprenant(apprenantId: number, formationId: number): Observable<StatistiquesApprenant> {
    return this.http.get<StatistiquesApprenant>(
      `${this.API}/apprenant/${apprenantId}/formation/${formationId}`
    );
  }

  getSuiviFormation(formationId: number): Observable<StatistiquesApprenant[]> {
    return this.http.get<StatistiquesApprenant[]>(`${this.API}/formation/${formationId}`);
  }

  getMonSuivi(): Observable<StatistiquesApprenant[]> {
    return this.http.get<StatistiquesApprenant[]>(`${this.API}/mon-suivi`);
  }

  getDetailApprenant(formationId: number, apprenantId: number): Observable<StatistiquesApprenant> {
    return this.http.get<StatistiquesApprenant>(
      `${this.API}/apprenant/${apprenantId}/formation/${formationId}`
    );
  }
}
