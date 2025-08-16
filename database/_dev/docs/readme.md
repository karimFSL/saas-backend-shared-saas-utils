1. Base de données par locataire (Database per Tenant)

Principe :
Chaque locataire possède sa propre base de données. Les données sont complètement isolées.

Avantages : Isolation maximale, facile à sauvegarder et restaurer par locataire.

Inconvénients : Gestion plus complexe si beaucoup de locataires, surcharge côté connexion.

Exemple conceptuel :

Tenant A → Base de données tenant_a_db

Tenant B → Base de données tenant_b_db

2. Schéma par locataire (Schema per Tenant)

Principe :
Tous les locataires partagent la même base de données, mais chaque locataire possède son propre schéma (schema).

Avantages : Isolation intermédiaire, moins de bases à gérer qu’en base par locataire.

Inconvénients : Certaines limitations de PostgreSQL selon le nombre de schémas, gestion des migrations un peu plus complexe.

Exemple conceptuel :

Base de données main_db

Schéma tenant_a

Schéma tenant_b

3. Ligne par locataire (Row per Tenant)

Principe :
Toutes les données des locataires sont dans les mêmes tables, mais chaque ligne contient un identifiant du locataire (tenant_id) pour les différencier.

Avantages : Simple à mettre en place, très peu de bases/schémas à gérer.

Inconvénients : Moins d’isolation, risque d’erreurs de filtrage, difficulté pour gérer de très gros volumes de données.

Exemple conceptuel :

CREATE TABLE customers (
id SERIAL PRIMARY KEY,
tenant_id INT NOT NULL,
name VARCHAR(255)
);