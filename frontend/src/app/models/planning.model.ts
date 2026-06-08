export type SeanceStatut = 'PLANIFIEE' | 'EN_COURS' | 'TERMINEE' | 'ANNULEE';
export type PresenceStatut = 'PRESENT' | 'ABSENT' | 'RETARD' | 'EXCUSE';
export type TypeEvaluation = 'EXAMEN' | 'DEVOIR' | 'QUIZ' | 'PROJET' | 'CONTROLE';
export interface Formation {
  id: number;
  titre: string;
  description?: string;
}
export interface Salle {
  id: number;
  nom: string;
  capacite: number;
  localisation: string;
  equipements: string;
  disponible: boolean;
  dateCreation: string;
}

export interface SalleRequest {
  nom: string;
  capacite: number;
  localisation?: string;
  equipements?: string;
  disponible?: boolean;
}

export interface Seance {
  id: number;
  titre: string;
  description: string;
  dateHeureDebut: string;
  dateHeureFin: string;
  statut: SeanceStatut;
  dureeMinutes: number;
  formation: { id: number; titre: string };
  salle: { id: number; nom: string; localisation: string } | null;
  formateur: { id: number; nom: string; prenom: string; email: string } | null;
  dateCreation: string;
  dateModification: string;
}

export interface SeanceRequest {
  titre: string;
  description?: string;
  dateHeureDebut: string;
  dateHeureFin: string;
  formationId: number;
  salleId?: number;
  formateurId?: number;
  statut?: SeanceStatut;
}

export interface Presence {
  id: number;
  seanceId: number;
  seanceTitre: string;
  apprenant: { id: number; nom: string; prenom: string; email: string };
  statut: PresenceStatut;
  commentaire?: string;
  dateEnregistrement: string;
  enregistrePar: { id: number; nom: string; prenom: string };
}

export interface PresenceRequest {
  seanceId: number;
  apprenantId: number;
  statut: PresenceStatut;
  commentaire?: string;
}

export interface PresenceBulkRequest {
  seanceId: number;
  presences: {
    apprenantId: number;
    statut: PresenceStatut;
    commentaire?: string;
  }[];
}

export interface Note {
  id: number;
  seanceId: number;
  seanceTitre: string;
  apprenant: { id: number; nom: string; prenom: string; email: string };
  valeur: number;
  coefficient: number;
  typeEvaluation: TypeEvaluation;
  commentaire?: string;
  dateCreation: string;
  enregistrePar: { id: number; nom: string; prenom: string };
}

export interface NoteRequest {
  seanceId: number;
  apprenantId: number;
  valeur: number;
  coefficient: number;
  typeEvaluation: TypeEvaluation;
  commentaire?: string;
}

export interface NoteBulkRequest {
  seanceId: number;
  notes: {
    apprenantId: number;
    valeur: number;
    coefficient: number;
    typeEvaluation: TypeEvaluation;
    commentaire?: string;
  }[];
}

export interface StatistiquesApprenant {
  apprenantId: number;
  nom: string;
  prenom: string;
  email: string;
  formationId: number;
  formationTitre: string;
  totalSeances: number;
  seancesAssistees: number;
  tauxPresence: number;
  moyenneGenerale: number;
  detailNotes: Note[];
  detailPresences: Presence[];
}

export interface PlanningDTO {
  dateDebut: string;
  dateFin: string;
  seances: Seance[];
}

export interface ConflitDTO {
  seanceExistante: Seance;
  messageConflit: string;
}

export interface TauxPresenceDTO {
  tauxPresence: number;
  seancesAssistees: number;
  totalSeances: number;
}

export interface MoyenneDTO {
  moyenne: number;
  totalNotes: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
}
