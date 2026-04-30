-- Sprint 2 - Schema Formations et Inscriptions
-- exécuté sur formation_db
CREATE TABLE IF NOT EXISTS formations (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    duree INTEGER NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    capacite_max INTEGER NOT NULL,
    capacite_actuelle INTEGER NOT NULL DEFAULT 0,
    prix DECIMAL(10,2) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'PLANIFIEE',
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inscriptions (
    id BIGSERIAL PRIMARY KEY,
    formation_id BIGINT NOT NULL,
    apprenant_id BIGINT NOT NULL,
    date_inscription TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    date_acceptation TIMESTAMP,
    motif_rejet TEXT,
    date_rejet TIMESTAMP,
    CONSTRAINT fk_inscription_formation FOREIGN KEY (formation_id) REFERENCES formations(id),
    CONSTRAINT fk_inscription_apprenant FOREIGN KEY (apprenant_id) REFERENCES users(id),
    CONSTRAINT uk_formation_apprenant UNIQUE (formation_id, apprenant_id)
);

CREATE TABLE IF NOT EXISTS formation_formateurs (
    formation_id BIGINT NOT NULL,
    formateur_id BIGINT NOT NULL,
    PRIMARY KEY (formation_id, formateur_id),
    CONSTRAINT fk_ff_formation FOREIGN KEY (formation_id) REFERENCES formations(id),
    CONSTRAINT fk_ff_formateur FOREIGN KEY (formateur_id) REFERENCES users(id)
);
