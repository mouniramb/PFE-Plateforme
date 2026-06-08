-- Sprint 3: Planification et Suivi Pédagogique - Schema
-- Database schema for Salles, Seances, Presences, and Notes

-- =====================================================
-- TABLE: salles
-- =====================================================
CREATE TABLE IF NOT EXISTS salles (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    capacite INTEGER NOT NULL CHECK (capacite > 0),
    localisation VARCHAR(255),
    equipements TEXT,
    disponible BOOLEAN DEFAULT true,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_salles_disponible ON salles(disponible);
CREATE INDEX idx_salles_capacite ON salles(capacite);

-- =====================================================
-- TABLE: seances
-- =====================================================
CREATE TABLE IF NOT EXISTS seances (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    date_heure_debut TIMESTAMP NOT NULL,
    date_heure_fin TIMESTAMP NOT NULL,
    statut VARCHAR(20) DEFAULT 'PLANIFIEE',
    formation_id BIGINT NOT NULL,
    salle_id BIGINT,
    formateur_id BIGINT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP,
    CONSTRAINT fk_seances_formation FOREIGN KEY (formation_id) REFERENCES formations(id) ON DELETE CASCADE,
    CONSTRAINT fk_seances_salle FOREIGN KEY (salle_id) REFERENCES salles(id) ON DELETE SET NULL,
    CONSTRAINT fk_seances_formateur FOREIGN KEY (formateur_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_seances_dates CHECK (date_heure_fin > date_heure_debut)
);

CREATE INDEX idx_seances_formation ON seances(formation_id);
CREATE INDEX idx_seances_formateur ON seances(formateur_id);
CREATE INDEX idx_seances_salle ON seances(salle_id);
CREATE INDEX idx_seances_debut ON seances(date_heure_debut);
CREATE INDEX idx_seances_fin ON seances(date_heure_fin);
CREATE INDEX idx_seances_statut ON seances(statut);

-- =====================================================
-- TABLE: presences
-- =====================================================
CREATE TABLE IF NOT EXISTS presences (
    id BIGSERIAL PRIMARY KEY,
    seance_id BIGINT NOT NULL,
    apprenant_id BIGINT NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'ABSENT',
    commentaire TEXT,
    date_enregistrement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enregistre_par_id BIGINT,
    CONSTRAINT fk_presences_seance FOREIGN KEY (seance_id) REFERENCES seances(id) ON DELETE CASCADE,
    CONSTRAINT fk_presences_apprenant FOREIGN KEY (apprenant_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_presences_enregistre_par FOREIGN KEY (enregistre_par_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT uq_presences_seance_apprenant UNIQUE(seance_id, apprenant_id)
);

CREATE INDEX idx_presences_seance ON presences(seance_id);
CREATE INDEX idx_presences_apprenant ON presences(apprenant_id);
CREATE INDEX idx_presences_statut ON presences(statut);

-- =====================================================
-- TABLE: notes
-- =====================================================
CREATE TABLE IF NOT EXISTS notes (
    id BIGSERIAL PRIMARY KEY,
    seance_id BIGINT NOT NULL,
    apprenant_id BIGINT NOT NULL,
    valeur DECIMAL(4,2) NOT NULL CHECK (valeur >= 0 AND valeur <= 20),
    coefficient DECIMAL(3,1) DEFAULT 1.0,
    type_evaluation VARCHAR(20) NOT NULL,
    commentaire TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enregistre_par_id BIGINT,
    CONSTRAINT fk_notes_seance FOREIGN KEY (seance_id) REFERENCES seances(id) ON DELETE CASCADE,
    CONSTRAINT fk_notes_apprenant FOREIGN KEY (apprenant_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notes_enregistre_par FOREIGN KEY (enregistre_par_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT uq_notes_seance_apprenant_type UNIQUE(seance_id, apprenant_id, type_evaluation)
);

CREATE INDEX idx_notes_seance ON notes(seance_id);
CREATE INDEX idx_notes_apprenant ON notes(apprenant_id);
CREATE INDEX idx_notes_type_evaluation ON notes(type_evaluation);

-- =====================================================
-- SAMPLE DATA (for testing)
-- =====================================================

-- Insert sample salles
INSERT INTO salles (nom, capacite, localisation, equipements, disponible) VALUES
('Salle A101', 30, 'Bâtiment A, Étage 1', 'Vidéoprojecteur, PC, Tableau blanc', true),
('Salle B202', 25, 'Bâtiment B, Étage 2', 'Vidéoprojecteur, PC, Tableau blanc, Wifi', true),
('Salle C301', 40, 'Bâtiment C, Étage 3', 'Vidéoprojecteur, PC, Son surround, Écrans multiples', true),
('Salle D101', 20, 'Bâtiment D, Étage 1', 'Vidéoprojecteur, Tableau blanc', false)
ON CONFLICT DO NOTHING;

-- =====================================================
-- CONSTRAINTS VERIFICATION
-- =====================================================
-- Verify salles constraints
-- SELECT * FROM salles WHERE capacite <= 0; -- Should return empty

-- Verify seances constraints
-- SELECT * FROM seances WHERE date_heure_fin <= date_heure_debut; -- Should return empty

-- Verify notes constraints
-- SELECT * FROM notes WHERE valeur < 0 OR valeur > 20; -- Should return empty

-- =====================================================
-- USEFUL QUERIES FOR SPRINT 3
-- =====================================================

-- Get all seances for a specific formation with instructor details
-- SELECT s.*, f.titre as formation_titre, u.nom, u.prenom, sa.nom as salle_nom
-- FROM seances s
-- JOIN formations f ON s.formation_id = f.id
-- LEFT JOIN users u ON s.formateur_id = u.id
-- LEFT JOIN salles sa ON s.salle_id = sa.id
-- WHERE s.formation_id = ?;

-- Get attendance rate for a specific learner in a specific formation
-- SELECT 
--   COUNT(CASE WHEN p.statut IN ('PRESENT', 'RETARD') THEN 1 END) as seances_assistees,
--   COUNT(DISTINCT s.id) as total_seances,
--   ROUND(100.0 * COUNT(CASE WHEN p.statut IN ('PRESENT', 'RETARD') THEN 1 END) / 
--         NULLIF(COUNT(DISTINCT s.id), 0), 2) as taux_presence
-- FROM presences p
-- JOIN seances s ON p.seance_id = s.id
-- WHERE p.apprenant_id = ? AND s.formation_id = ? AND s.statut = 'TERMINEE';

-- Get weighted average for a learner in a formation
-- SELECT 
--   SUM(n.valeur * n.coefficient) / NULLIF(SUM(n.coefficient), 0) as moyenne_ponderee
-- FROM notes n
-- JOIN seances s ON n.seance_id = s.id
-- WHERE n.apprenant_id = ? AND s.formation_id = ?;

-- Get schedule conflicts for a room during a specific period
-- SELECT s1.*
-- FROM seances s1
-- WHERE s1.salle_id = ? 
--   AND s1.statut != 'ANNULEE'
--   AND s1.date_heure_debut < ?::timestamp
--   AND s1.date_heure_fin > ?::timestamp;

-- Get schedule conflicts for an instructor during a specific period
-- SELECT s1.*
-- FROM seances s1
-- WHERE s1.formateur_id = ? 
--   AND s1.statut != 'ANNULEE'
--   AND s1.date_heure_debut < ?::timestamp
--   AND s1.date_heure_fin > ?::timestamp;
