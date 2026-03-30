import { HttpErrorResponse } from '@angular/common/http';

export function mapHttpErrorMessage(error: unknown, fallbackMessage: string): string {
  if (!(error instanceof HttpErrorResponse)) {
    return fallbackMessage;
  }

  const backendMessage =
    (typeof error.error === 'string' && error.error) ||
    error.error?.message ||
    error.error?.error;

  if (backendMessage) {
    return backendMessage;
  }

  switch (error.status) {
    case 0:
      return 'Serveur indisponible. Verifie que Spring Boot tourne sur http://localhost:8080.';
    case 400:
      return 'Requete invalide. Verifie les informations envoyees.';
    case 401:
      return 'Session expirée ou identifiants invalides. Merci de te reconnecter.';
    case 403:
      return 'Acces refuse. Tu n as pas les droits necessaires.';
    case 404:
      return 'Ressource introuvable.';
    case 409:
      return 'Conflit detecte. Cette ressource existe deja.';
    case 500:
      return 'Erreur interne du serveur. Reessaie dans quelques instants.';
    default:
      return fallbackMessage;
  }
}
