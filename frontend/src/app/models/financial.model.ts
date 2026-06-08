export type PaiementStatut = 'EN_ATTENTE' | 'VALIDE' | 'REJETE';
export type ModePaiement = 'FORMATION' | 'SEANCE' | 'TRANCHE' | 'ANNUAIRE';

export interface Paiement {
  id: number;
  apprenantId: number;
  apprenantNom: string;
  apprenantPrenom: string;
  formationId: number;
  formationTitre: string;
  modePaiement: ModePaiement;
  montant: number;
  remise: number;
  montantNet: number;
  datePaiement: string;
  statut: PaiementStatut;
  trancheNumber?: number;
  seanceId?: number;
  seanceTitre?: string;
  moisAnnuaire?: number;
  notes?: string;
  commentaire?: string;
  dateEnregistrement: string;
  enregistrePar?: { id: number; nom: string; prenom: string };
  dateValidation?: string;
  validePar?: { id: number; nom: string; prenom: string };
}

export interface PaiementRequest {
  apprenantId: number;
  formationId: number;
  modePaiement: ModePaiement;
  montant: number;
  remise?: number;
  trancheNumber?: number;
  seanceId?: number;
  moisAnnuaire?: number;
  datePaiement: string;
  notes?: string;
}

export interface FormationConfig {
  formationId: number;
  formationTitre: string;
  prixFormation: number;
  permetFormationComplete: boolean;
  permetParSeance: boolean;
  prixParSeance?: number;
  permetParTranche: boolean;
  nombreTranches?: number;
  montantsTranches?: string;
  permetParAnnuaire: boolean;
  montantMensuel?: number;
  nombreMois?: number;
}

export interface PaiementValidationRequest {
  statut: PaiementStatut;
  commentaire?: string;
}

