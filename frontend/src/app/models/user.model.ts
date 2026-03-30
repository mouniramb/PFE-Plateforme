export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nom: string;
  prenom: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  role: string;
  id: number;
  nom: string;
  prenom: string;
  email: string;
}

export interface UserResponse {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
  dateCreation: string;
}

export interface CreateFormateurRequest {
  nom: string;
  prenom: string;
  email: string;
}
