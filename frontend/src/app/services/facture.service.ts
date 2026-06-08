import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class FactureService {
  private apiUrl = 'http://localhost:8080/api/factures';

  constructor(private http: HttpClient) {}

  getAllFactures(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
      catchError(err => {
        console.error('Erreur GET /api/factures :', err.status, err.message);
        throw err;
      })
    );
  }

  getFacture(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => { console.error('Erreur getFacture:', err); return of(null); })
    );
  }

  createFacture(data: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, data).pipe(
      catchError(err => { console.error('Erreur createFacture:', err); return of(null); })
    );
  }

  updateFacture(id: number, data: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, data).pipe(
      catchError(err => { console.error('Erreur updateFacture:', err); return of(null); })
    );
  }

  deleteFacture(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => { console.error('Erreur deleteFacture:', err); return of(null); })
    );
  }

  downloadFacturePDF(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/download`, { responseType: 'blob' });
  }

  filterByStatut(statut: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}?statut=${statut}`).pipe(
      catchError(err => {
        console.error('Erreur filterByStatut :', err);
        return of([]);
      })
    );
  }
}
