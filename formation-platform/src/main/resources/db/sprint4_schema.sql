-- ============================================================
-- SPRINT 4 - Gestion Financière et Statistiques
-- Schema SQL pour PostgreSQL
-- ============================================================

-- TABLE: tarifs
CREATE TABLE IF NOT EXISTS tarifs (
    id               BIGSERIAL PRIMARY KEY,
    formation_id     BIGINT NOT NULL UNIQUE REFERENCES formations(id),
    prix_unitaire    DECIMAL(10,2) NOT NULL CHECK (prix_unitaire > 0),
    prix_reduit      DECIMAL(10,2),
    seuil_groupe     INTEGER,
    devise           VARCHAR(3) DEFAULT 'EUR',
    actif            BOOLEAN DEFAULT true,
    date_creation    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP,
    CONSTRAINT chk_prix_reduit CHECK (
        prix_reduit IS NULL OR (prix_reduit > 0 AND prix_reduit < prix_unitaire)
    ),
    CONSTRAINT chk_seuil_groupe CHECK (
        prix_reduit IS NULL OR seuil_groupe >= 2
    )
);

-- TABLE: factures
CREATE TABLE IF NOT EXISTS factures (
    id               BIGSERIAL PRIMARY KEY,
    numero_facture   VARCHAR(50) NOT NULL UNIQUE,
    apprenant_id     BIGINT NOT NULL REFERENCES users(id),
    formation_id     BIGINT NOT NULL REFERENCES formations(id),
    date_emission    DATE NOT NULL,
    date_echeance    DATE NOT NULL,
    montant_total    DECIMAL(10,2) NOT NULL,
    montant_tva      DECIMAL(10,2) NOT NULL,
    montant_ht       DECIMAL(10,2) NOT NULL,
    statut           VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    date_annulation  DATE,
    date_creation    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_montants   CHECK (montant_total > montant_tva),
    CONSTRAINT chk_dates      CHECK (date_echeance >= date_emission),
    CONSTRAINT uk_apprenant_formation UNIQUE (apprenant_id, formation_id)
);

-- TABLE: paiements
CREATE TABLE IF NOT EXISTS paiements (
    id                   BIGSERIAL PRIMARY KEY,
    facture_id           BIGINT NOT NULL,
    montant              DECIMAL(10,2) NOT NULL CHECK (montant > 0),
    date_paiement        DATE NOT NULL,
    mode_paiement        VARCHAR(20) NOT NULL,
    statut               VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    reference            VARCHAR(100),
    commentaire          TEXT,
    date_enregistrement  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enregistre_par_id    BIGINT REFERENCES users(id),
    date_validation      TIMESTAMP,
    valide_par_id        BIGINT REFERENCES users(id),
    CONSTRAINT fk_facture FOREIGN KEY (facture_id) REFERENCES factures(id) ON DELETE RESTRICT
);

-- TABLE: rapports
CREATE TABLE IF NOT EXISTS rapports (
    id               BIGSERIAL PRIMARY KEY,
    titre            VARCHAR(255) NOT NULL,
    type             VARCHAR(20) NOT NULL,
    date_debut       DATE NOT NULL,
    date_fin         DATE NOT NULL,
    contenu_json     TEXT NOT NULL,
    format           VARCHAR(10) NOT NULL,
    date_generation  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    genere_par_id    BIGINT REFERENCES users(id)
);

-- ============================================================
-- INDEX pour les performances
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_factures_apprenant ON factures(apprenant_id);
CREATE INDEX IF NOT EXISTS idx_factures_statut    ON factures(statut);
CREATE INDEX IF NOT EXISTS idx_factures_date      ON factures(date_emission);
CREATE INDEX IF NOT EXISTS idx_paiements_facture  ON paiements(facture_id);
CREATE INDEX IF NOT EXISTS idx_paiements_statut   ON paiements(statut);
CREATE INDEX IF NOT EXISTS idx_rapport_type       ON rapports(type);
CREATE INDEX IF NOT EXISTS idx_rapport_date       ON rapports(date_generation);

-- ============================================================
-- DONNÉES DE TEST
-- ============================================================

-- Tarif pour la première formation (si elle existe)
INSERT INTO tarifs (formation_id, prix_unitaire, prix_reduit, seuil_groupe, devise, actif)
SELECT id, 500.00, 450.00, 5, 'EUR', true
FROM formations
WHERE id = (SELECT MIN(id) FROM formations)
ON CONFLICT (formation_id) DO NOTHING;

-- Facture de test (si apprenant et formation existent)
INSERT INTO factures (numero_facture, apprenant_id, formation_id, date_emission, date_echeance,
                      montant_total, montant_tva, montant_ht, statut)
SELECT
    'FACT-2025-0001',
    (SELECT id FROM users WHERE role = 'APPRENANT' LIMIT 1),
    (SELECT MIN(id) FROM formations),
    CURRENT_DATE,
    CURRENT_DATE + INTERVAL '30 days',
    600.00, 100.00, 500.00,
    'EN_ATTENTE'
WHERE
    (SELECT id FROM users WHERE role = 'APPRENANT' LIMIT 1) IS NOT NULL
    AND (SELECT MIN(id) FROM formations) IS NOT NULL
ON CONFLICT (numero_facture) DO NOTHING;
