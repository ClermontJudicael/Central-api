# Where to put the list of sales point in the db
```sql
-- Table des points de vente (ex : Analamahintsy, Antanimena)
CREATE TABLE sales_point (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    baseUrl VARCHAR(100) NOT NULL UNIQUE
);
```
