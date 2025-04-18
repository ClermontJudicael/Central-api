-- Création des énumérations
CREATE TYPE duration_unit AS ENUM ('SECONDS', 'MINUTES', 'HOUR');

-- Table des points de vente (ex : Analamahintsy, Antanimena)
CREATE TABLE sales_point (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Table des plats (identifiants propres au siège)
CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Table des meilleures ventes synchronisées
CREATE TABLE best_sales (
    id SERIAL PRIMARY KEY,
    sales_point_id INTEGER NOT NULL REFERENCES sales_point(id) ON DELETE CASCADE,
    dish_id INTEGER NOT NULL REFERENCES dish(id) ON DELETE CASCADE,
    quantity_sold BIGINT NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table des meilleurs temps de préparation synchronisés
CREATE TABLE best_processing_time (
    id SERIAL PRIMARY KEY,
    sales_point_id INTEGER NOT NULL REFERENCES sales_point(id) ON DELETE CASCADE,
    dish_id INTEGER NOT NULL REFERENCES dish(id) ON DELETE CASCADE,
    preparation_duration NUMERIC(10, 2) NOT NULL,
    duration_unit duration_unit NOT NULL DEFAULT 'SECONDS',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
