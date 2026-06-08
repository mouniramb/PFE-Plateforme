import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RapportService {
  private apiUrl = 'http://localhost:8080/api/rapports';

  constructor(private http: HttpClient) {}

  getRapports(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  generateRapport(type: string, dateDebut?: string, dateFin?: string): Observable<any> {
    let url = `${this.apiUrl}/generate?type=${type}`;
    if (dateDebut) url += `&dateDebut=${dateDebut}`;
    if (dateFin) url += `&dateFin=${dateFin}`;
    return this.http.post<any>(url, {});
  }

  generateRapportApprenants(): Observable<any> {
    return this.generateRapport('APPRENANTS');
  }

  generateRapportFormations(): Observable<any> {
    return this.generateRapport('FORMATIONS');
  }

  generateRapportPaiements(): Observable<any> {
    return this.generateRapport('PAIEMENTS');
  }

  downloadRapport(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/download`, { responseType: 'blob' });
  }

  deleteRapport(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
