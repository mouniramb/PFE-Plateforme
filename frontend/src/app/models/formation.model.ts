export type StatutFormation = 'PLANIFIEE' | 'EN_COURS' | 'TERMINEE';
export type StatutInscription = 'EN_ATTENTE' | 'ACCEPTEE' | 'REJETEE' | 'ANNULEE';

export interface FormateurInfo {
  id: number;
  nom: string;
  prenom: string;
  email: string;
}

export interface Formation {
  id: number;
  titre: string;
  description: string;
  duree: number;
  dateDebut: string;
  dateFin: string;
  capaciteMax: number;
  capaciteActuelle: number;
  placesRestantes: number;
  placesDisponibles: boolean;
  prix: number;
  statut: StatutFormation;
  dateCreation: string;
  dateModification: string;
  formateurs: FormateurInfo[];
}

export interface FormationRequest {
  titre: string;
  description: string;
  duree: number;
  dateDebut: string;
  dateFin: string;
  capaciteMax: number;
  prix: number;
  statut?: StatutFormation;
  formateurIds?: number[];
}

export interface Inscription {
  id: number;
  formationId: number;
  formationTitre: string;
  formationDateDebut: string;
  formationDateFin: string;
  apprenantId: number;
  apprenantNom: string;
  apprenantPrenom: string;
  apprenantEmail: string;
  dateInscription: string;
  statut: StatutInscription;
  dateAcceptation?: string;
  motifRejet?: string;
  dateRejet?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
