import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StatistiqueService {
  private apiUrl = 'http://localhost:8080/api/statistiques';

  constructor(private http: HttpClient) {}

  getStatistiques(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }

  getStatistiquesFormations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/formations`);
  }

  getStatistiquesApprenants(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/apprenants`);
  }

  filterByDateRange(dateDebut: string, dateFin: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}?dateDebut=${dateDebut}&dateFin=${dateFin}`);
  }
}
