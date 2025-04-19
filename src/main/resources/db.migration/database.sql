-- Création des énumérations
CREATE TYPE duration_unit AS ENUM ('SECONDS', 'MINUTES', 'HOUR');

-- Table des points de vente (ex : Analamahintsy, Antanimena)
CREATE TABLE sales_point (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    baseUrl VARCHAR(100) NOT NULL UNIQUE
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

CREATE TABLE IF NOT EXISTS aggregated_sales (
    id SERIAL PRIMARY KEY,
    sales_point_name VARCHAR(255) NOT NULL,
    dish VARCHAR(255) NOT NULL,
    quantity_sold BIGINT NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE (sales_point_name, dish)
);

CREATE TABLE IF NOT EXISTS aggregated_processing_time (
    id SERIAL PRIMARY KEY,
    sales_point_name VARCHAR(255) NOT NULL,
    dish VARCHAR(255) NOT NULL,
    average DOUBLE PRECISION NOT NULL,
    minimum BIGINT NOT NULL,
    maximum BIGINT NOT NULL,
    unit duration_unit NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE (sales_point_name, dish)
);

ALTER TABLE best_sales ADD CONSTRAINT unique_sales_dish UNIQUE (sales_point_id, dish_id);

-- 1. Ajouter la colonne price
ALTER TABLE dish
ADD COLUMN price NUMERIC(10,2);

-- 2. (Optionnel mais recommandé) Mettre un prix par défaut pour les anciens plats
UPDATE dish
SET price = 0.00
WHERE price IS NULL;
ALTER TABLE best_processing_time ADD CONSTRAINT unique_processing_time UNIQUE (sales_point_id, dish_id);