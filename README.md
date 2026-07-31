# Projet Cinéma — Internet Movie Database

[![Java CI](https://github.com/QuentinHamonic/projet-jpa/actions/workflows/ci.yml/badge.svg)](https://github.com/QuentinHamonic/projet-jpa/actions/workflows/ci.yml)

[Documentation Javadoc](https://quentinhamonic.github.io/projet-jpa/)

Projet individuel Java réalisé dans le cadre de la formation Diginamic. Cette
application console importe un jeu de données inspiré d'IMDb dans MySQL, puis
permet d'effectuer plusieurs recherches cinématographiques.

## Fonctionnalités

Le projet permet de :

- représenter le domaine avec un modèle métier UML et un modèle physique de
  données documentés ;
- importer les films, personnes, rôles, réalisateurs, genres, pays et langues dans une base de données avec JPA ;
- structurer les accès aux données avec des couches DAO et service ;
- parcourir le fichier JSON en streaming avec Jackson ;
- dédoublonner les données de référence pendant l'import ;
- initialiser automatiquement une base vide, puis valider son schéma lors des
  démarrages suivants ;
- effectuer les six recherches demandées depuis un menu console.

Les recherches couvrent notamment la filmographie d'un acteur, le casting d'un
film, les films sortis sur une période et les films ou acteurs communs.

## Source des données

Le sujet propose des fichiers CSV ou un fichier JSON global. Le projet s'appuie sur [`films.json`](src/main/resources/films.json), retenu comme source unique afin de conserver une structure complète et non ambiguë des données. Son import utilise la bibliothèque Jackson.

L'import nettoie et dédoublonne les lieux de naissance, pays, langues et genres.
Il convertit également les dates de naissance exploitables en `LocalDate`.

## Prérequis

La méthode recommandée nécessite :

- Docker Desktop, ou Docker Engine avec le plugin Docker Compose ;
- Git pour cloner le dépôt.

Une exécution sans Docker nécessite Java 21, Maven 3.9 et une instance MySQL
8.4 accessible localement.

## Démarrage avec Docker

Copiez la configuration d'exemple et remplacez ses deux mots de passe fictifs :

```powershell
# PowerShell
Copy-Item .env.example .env
```

```shell
# Linux ou macOS
cp .env.example .env
```

Les variables Docker sont préfixées par `CINEMA_` afin de ne pas entrer en
conflit avec les identifiants `DB_USER` et `DB_PASSWORD` d'une exécution locale.

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
docker compose down --volumes --remove-orphans
```

## Exécution locale

Créez d'abord une base vide dans l'instance MySQL locale :

```sql
CREATE DATABASE cinema CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Fournissez ensuite la connexion avec `DB_URL`, `DB_USER` et `DB_PASSWORD`. Sous
PowerShell :

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/cinema?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Paris"
$env:DB_USER = "root"
$env:DB_PASSWORD = "votre-mot-de-passe-local"
mvn clean package
java -jar target/cinema-1.0-SNAPSHOT-all.jar
```

`fr.diginamic.App` initialise les tables et importe les données si nécessaire,
puis ouvre le menu de recherche. `fr.diginamic.SearchApp` reste disponible pour
lancer uniquement ce menu sur une base déjà initialisée.

## Déploiement

L'application n'expose pas de serveur HTTP : elle se déploie comme une
application console conteneurisée et s'utilise depuis le terminal de la machine
hôte.

Sur une nouvelle machine équipée de Docker :

```shell
git clone https://github.com/QuentinHamonic/projet-jpa.git
cd projet-jpa
# Linux ou macOS
cp .env.example .env
# PowerShell : Copy-Item .env.example .env
# Remplacer les deux mots de passe dans .env
docker compose run --build --rm app
```

Le volume `cinema-data` conserve la base entre les exécutions. Pour déployer une
nouvelle version du code :

```shell
git pull
docker compose build app
docker compose run --rm app
```

La CI construit et teste le projet à chaque push et met le JAR exécutable à
disposition comme artefact GitHub Actions pendant 14 jours. Après un push sur
`main`, le workflow CD publie automatiquement la Javadoc sur
[GitHub Pages](https://quentinhamonic.github.io/projet-jpa/).

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

- Java 21
- Maven 3.9
- JPA / Hibernate 6.6
- Jackson 2.21
- MySQL 8.4
- JUnit 4
- Docker Compose
- GitHub Actions

## Documentation

Les détails de la modélisation sont disponibles dans [`conception/conception.md`](conception/conception.md). Les analyses complémentaires du fichier JSON et du choix entre CSV et JSON se trouvent dans le dossier [`docs`](docs/).
