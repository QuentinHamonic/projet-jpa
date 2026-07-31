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

## Exécution

La connexion à MySQL est configurable par variables d'environnement :

- `DB_URL` : URL JDBC ;
- `DB_USER` : utilisateur MySQL ;
- `DB_PASSWORD` : mot de passe MySQL ;
- `HIBERNATE_DDL_AUTO` : `validate` par défaut ou `create` pour recréer les tables.

Le fichier [`.env.example`](.env.example) fournit des valeurs fictives. Un fichier
`.env` local peut en être dérivé, mais ne doit jamais être versionné. Pour une
exécution directe sous PowerShell, définissez les variables dans le terminal :

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/cinema?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Paris"
$env:DB_USER = "root"
$env:DB_PASSWORD = "votre-mot-de-passe-local"
$env:HIBERNATE_DDL_AUTO = "validate"
```

L'application peut ensuite être lancée avec Maven :

```shell
# Importer les données
mvn exec:java -Dexec.mainClass="fr.diginamic.App"

# Lancer le menu de recherche
mvn exec:java -Dexec.mainClass="fr.diginamic.SearchApp"
```

La commande `mvn clean package` produit également un JAR autonome :

```shell
# Lancer le menu de recherche
java -jar target/cinema-1.0-SNAPSHOT-all.jar

# Lancer l'import
java -cp target/cinema-1.0-SNAPSHOT-all.jar fr.diginamic.App
```

Le mode `create` supprime et recrée les tables au démarrage. Il doit uniquement
être activé volontairement pour initialiser une base vide avant l'import.

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
