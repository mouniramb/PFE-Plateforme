import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateFormateurRequest, UserResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly API_URL = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  // ── Profil ───────────────────────────────────────────────────────────────

  getMyProfile(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.API_URL}/me`);
  }

  // ── Formateurs ───────────────────────────────────────────────────────────

  getAllFormateurs(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${this.API_URL}/formateurs`);
  }

  getFormateurById(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.API_URL}/formateurs/${id}`);
  }

  createFormateur(request: CreateFormateurRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.API_URL}/formateurs`, request);
  }

  updateFormateur(id: number, request: CreateFormateurRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.API_URL}/formateurs/${id}`, request);
  }

  deleteFormateur(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/formateurs/${id}`);
  }
}
