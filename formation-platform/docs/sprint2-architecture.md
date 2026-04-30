# Sprint 2 - Architecture Backend

## Structure des packages
- `entity`: `Formation`, `Inscription`, enums de statuts
- `dto`: DTOs de création/mise à jour/réponse
- `repository`: accès données JPA (pagination + requêtes custom)
- `service`: logique métier, validations, transactions, notifications email
- `controller`: endpoints REST sécurisés avec `@PreAuthorize`
- `exception`: exceptions personnalisées + `GlobalExceptionHandler`

## Flux principal inscription
1. L'apprenant appelle `POST /api/inscriptions/s-inscrire`.
2. Le service valide unicité + places disponibles.
3. Inscription créée avec statut `EN_ATTENTE`.
4. Email de confirmation apprenant + notification admin.
5. L'admin accepte/rejette via endpoints dédiés.
6. En cas d'acceptation: `capaciteActuelle` de la formation est incrémentée.

## Sécurité
- Contrôle JWT global via filtre existant Sprint 1.
- Contrôle d'autorisation métier par rôles avec `@PreAuthorize`.
- Vérifications complémentaires en service/contrôleur pour les actions sensibles.
