import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Seance, SeanceRequest, SeanceStatut, ConflitDTO, PlanningDTO, PageResponse } from '../models/planning.model';

@Injectable({
  providedIn: 'root'
})
export class SeanceService {
  private readonly API = 'http://localhost:8080/api/seances';
  private readonly PLANNING_API = 'http://localhost:8080/api/planning';

  constructor(private http: HttpClient) {}

  createSeance(data: SeanceRequest, forcer: boolean = false): Observable<Seance> {
    let params = new HttpParams();
    params = params.set('forcer', forcer.toString());
    return this.http.post<Seance>(this.API, data, { params });
  }

  updateSeance(id: number, data: SeanceRequest): Observable<Seance> {
    return this.http.put<Seance>(`${this.API}/${id}`, data);
  }

  deleteSeance(id: number): Observable<any> {
    return this.http.delete<any>(`${this.API}/${id}`);
  }

  getSeance(id: number): Observable<Seance> {
    return this.http.get<Seance>(`${this.API}/${id}`);
  }

  getSeancesByFormation(formationId: number, page: number, size: number): Observable<PageResponse<Seance>> {
    let params = new HttpParams();
    params = params.set('page', page.toString());
    params = params.set('size', size.toString());
    return this.http.get<PageResponse<Seance>>(`${this.API}/formation/${formationId}`, { params });
  }

  getSeancesByFormateur(formateurId: number, page: number, size: number): Observable<PageResponse<Seance>> {
    let params = new HttpParams();
    params = params.set('page', page.toString());
    params = params.set('size', size.toString());
    return this.http.get<PageResponse<Seance>>(`${this.API}/formateur/${formateurId}`, { params });
  }

  updateStatut(id: number, statut: SeanceStatut): Observable<Seance> {
    return this.http.put<Seance>(`${this.API}/${id}/statut`, { statut });
  }

  checkConflits(data: SeanceRequest): Observable<ConflitDTO[]> {
    return this.http.post<ConflitDTO[]>(`${this.API}/conflits`, data);
  }

  getPlanningAdmin(debut: string, fin: string): Observable<PlanningDTO> {
    let params = new HttpParams();
    params = params.set('debut', debut);
    params = params.set('fin', fin);
    return this.http.get<PlanningDTO>(`${this.PLANNING_API}/admin`, { params });
  }

  getPlanningFormateur(formateurId: number, debut: string, fin: string): Observable<PlanningDTO> {
    let params = new HttpParams();
    params = params.set('debut', debut);
    params = params.set('fin', fin);
    return this.http.get<PlanningDTO>(`${this.PLANNING_API}/formateur/${formateurId}`, { params });
  }

  getPlanningApprenant(apprenantId: number, debut: string, fin: string): Observable<PlanningDTO> {
    let params = new HttpParams();
    params = params.set('debut', debut);
    params = params.set('fin', fin);
    return this.http.get<PlanningDTO>(`${this.PLANNING_API}/apprenant/${apprenantId}`, { params });
  }

  getMonPlanning(debut: string, fin: string): Observable<PlanningDTO> {
    let params = new HttpParams();
    params = params.set('debut', debut);
    params = params.set('fin', fin);
    return this.http.get<PlanningDTO>(`${this.PLANNING_API}/mon-planning`, { params });
  }
}
