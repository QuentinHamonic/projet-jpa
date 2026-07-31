# Projet Cinéma — Internet Movie Database

[![Java CI](https://github.com/QuentinHamonic/projet-jpa/actions/workflows/ci.yml/badge.svg)](https://github.com/QuentinHamonic/projet-jpa/actions/workflows/ci.yml)

[Documentation Javadoc](https://quentinhamonic.github.io/projet-jpa/)

Projet individuel Java réalisé dans le cadre de la formation Diginamic. L'objectif est de construire une petite application de gestion de données cinématographiques à partir d'un jeu de données inspiré d'IMDb.

## Objectifs

Le projet doit permettre de :

- concevoir le modèle métier et le modèle physique de données ;
- importer les films, personnes, rôles, réalisateurs, genres, pays et langues dans une base de données avec JPA ;
- structurer les accès aux données avec des couches DAO et service ;
- proposer une application en console permettant d'effectuer des recherches dans la base.

Les recherches prévues concernent notamment la filmographie d'un acteur, le casting d'un film, les films sortis sur une période et les films ou acteurs communs.

## Source des données

Le sujet propose des fichiers CSV ou un fichier JSON global. Le projet s'appuie sur [`films.json`](src/main/resources/films.json), retenu comme source unique afin de conserver une structure complète et non ambiguë des données. Son import utilise la bibliothèque Jackson.

L'import nettoie et dédoublonne les lieux de naissance, pays, langues et genres. Il convertit également les dates de naissance exploitables en `LocalDate`.

## État actuel

Le projet est fonctionnel :

- squelette Java/Maven initialisé ;
- fichier JSON intégré aux ressources ;
- diagramme de classes UML et modèle physique de données préparés ;
- choix de modélisation et structure de la base documentés ;
- import JSON en streaming avec Jackson et persistance JPA réalisés ;
- DAO générique, DAO spécialisés et couche de service disponibles ;
- menu console couvrant les six recherches demandées.

## Démarrage avec Docker

Copiez la configuration d'exemple et remplacez ses deux mots de passe fictifs :

```powershell
Copy-Item .env.example .env
```

Lancez ensuite l'application :

```shell
docker compose run --build --rm app
```

Au premier démarrage, Compose lance MySQL, Hibernate crée le schéma et le fichier
JSON est importé automatiquement. Les démarrages suivants conservent les données
dans le volume Docker et valident le schéma avant d'ouvrir le menu.

```shell
# Arrêter MySQL sans supprimer les données
docker compose down

# Réinitialiser complètement la base
docker compose down --volumes
```

## Exécution locale

Sans Docker, la connexion peut être fournie par `DB_URL`, `DB_USER` et
`DB_PASSWORD`. Sous PowerShell :

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/cinema?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Paris"
$env:DB_USER = "root"
$env:DB_PASSWORD = "votre-mot-de-passe-local"
mvn exec:java -Dexec.mainClass="fr.diginamic.App"
```

La commande `mvn clean package` produit également un JAR autonome :

```shell
java -jar target/cinema-1.0-SNAPSHOT-all.jar
```

`fr.diginamic.App` initialise la base si nécessaire puis ouvre le menu de
recherche. `fr.diginamic.SearchApp` reste disponible pour lancer uniquement ce
menu sur une base déjà initialisée.

## Organisation

```text
cinema/
├── conception/          # Document de conception UML et MPD
├── docs/                # Analyses du jeu de données et choix techniques
├── src/main/java/       # Code source de l'application
├── src/main/resources/  # Jeu de données films.json
├── src/test/java/       # Tests
└── pom.xml              # Configuration Maven
```

## Technologies

- Java
- Maven
- JPA / Hibernate
- Jackson
- base de données relationnelle MySQL ou MariaDB
- JUnit

## Documentation

Les détails de la modélisation sont disponibles dans [`conception/conception.md`](conception/conception.md). Les analyses complémentaires du fichier JSON et du choix entre CSV et JSON se trouvent dans le dossier [`docs`](docs/).
