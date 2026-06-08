import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TarifService {
  private apiUrl = 'http://localhost:8080/api/tarifs';

  constructor(private http: HttpClient) { }

  getTarifs(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
      catchError(error => {
        console.error('Erreur lors de la récupération des tarifs', error);
        return of([]);
      })
    );
  }

  getTarifById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      catchError(error => {
        console.error('Erreur lors de la récupération du tarif', error);
        return of(null);
      })
    );
  }

  createTarif(tarif: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, tarif).pipe(
      catchError(error => {
        console.error('Erreur lors de la création du tarif', error);
        return of(null);
      })
    );
  }

  updateTarif(id: number, tarif: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, tarif).pipe(
      catchError(error => {
        console.error('Erreur lors de la mise à jour du tarif', error);
        return of(null);
      })
    );
  }

  deleteTarif(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`).pipe(
      catchError(error => {
        console.error('Erreur lors de la suppression du tarif', error);
        return of({ success: false });
      })
    );
  }
}
