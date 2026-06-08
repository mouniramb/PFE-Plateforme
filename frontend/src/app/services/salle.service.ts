import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Salle, SalleRequest, ConflitDTO, PageResponse } from '../models/planning.model';

@Injectable({
  providedIn: 'root'
})
export class SalleService {
  private readonly API = 'http://localhost:8080/api/salles';

  constructor(private http: HttpClient) {}

  getAllSalles(page: number, size: number): Observable<PageResponse<Salle>> {
    let params = new HttpParams();
    params = params.set('page', page.toString());
    params = params.set('size', size.toString());
    return this.http.get<PageResponse<Salle>>(this.API, { params });
  }

  getSalle(id: number): Observable<Salle> {
    return this.http.get<Salle>(`${this.API}/${id}`);
  }

  getSallesDisponibles(): Observable<Salle[]> {
    return this.http.get<Salle[]>(`${this.API}/disponibles`);
  }

  createSalle(data: SalleRequest): Observable<Salle> {
    return this.http.post<Salle>(this.API, data);
  }

  updateSalle(id: number, data: SalleRequest): Observable<Salle> {
    return this.http.put<Salle>(`${this.API}/${id}`, data);
  }

  deleteSalle(id: number): Observable<any> {
    return this.http.delete<any>(`${this.API}/${id}`);
  }

  checkDisponibilite(
    salleId: number,
    debut: string,
    fin: string,
    seanceId?: number
  ): Observable<{ disponible: boolean; conflits: ConflitDTO[] }> {
    let params = new HttpParams();
    params = params.set('debut', debut);
    params = params.set('fin', fin);
    if (seanceId) {
      params = params.set('seanceId', seanceId.toString());
    }
    return this.http.get<{ disponible: boolean; conflits: ConflitDTO[] }>(
      `${this.API}/${salleId}/disponibilite`,
      { params }
    );
  }
}
