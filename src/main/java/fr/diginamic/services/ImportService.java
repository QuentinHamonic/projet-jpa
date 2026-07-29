package fr.diginamic.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.diginamic.dao.FilmDao;
import fr.diginamic.dao.GenreDao;
import fr.diginamic.dao.LangueDao;
import fr.diginamic.dao.LieuNaissanceDao;
import fr.diginamic.dao.PaysDao;
import fr.diginamic.dao.PersonneDao;
import fr.diginamic.dao.RoleDao;
import fr.diginamic.dto.FilmDto;
import fr.diginamic.dto.LieuTournageDto;
import fr.diginamic.dto.PaysDto;
import fr.diginamic.dto.PersonneDto;
import fr.diginamic.dto.RoleDto;
import fr.diginamic.entities.Film;
import fr.diginamic.entities.Genre;
import fr.diginamic.entities.Langue;
import fr.diginamic.entities.LieuNaissance;
import fr.diginamic.entities.LieuTournage;
import fr.diginamic.entities.Pays;
import fr.diginamic.entities.Personne;
import fr.diginamic.entities.Role;
import fr.diginamic.readers.FilmJsonReader;
import fr.diginamic.utils.ImportValueParser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

/**
 * Importe les films du fichier JSON dans la base de données.
 */
public class ImportService {

    private final EntityManager entityManager;
    private final FilmJsonReader filmJsonReader;

    private final FilmDao filmDao;
    private final PersonneDao personneDao;
    private final RoleDao roleDao;
    private final GenreDao genreDao;
    private final PaysDao paysDao;
    private final LangueDao langueDao;
    private final LieuNaissanceDao lieuNaissanceDao;

    private final Map<String, Film> filmsById = new HashMap<>();
    private final Map<String, Personne> personnesById = new HashMap<>();
    private final Map<String, Genre> genresByName = new HashMap<>();
    private final Map<String, Pays> paysByName = new HashMap<>();
    private final Map<String, Langue> languesByName = new HashMap<>();
    private final Map<String, LieuNaissance> lieuxByLabel = new HashMap<>();
    private final Map<RoleKey, Role> rolesByKey = new HashMap<>();

    /**
     * Crée un service d'import utilisant le gestionnaire d'entités fourni.
     *
     * @param em gestionnaire d'entités utilisé pendant l'import
     */
    public ImportService(EntityManager em) {
        this.entityManager = em;

        this.filmJsonReader = new FilmJsonReader();
        this.filmDao = new FilmDao(entityManager);
        this.personneDao = new PersonneDao(entityManager);
        this.roleDao = new RoleDao(entityManager);
        this.genreDao = new GenreDao(entityManager);
        this.paysDao = new PaysDao(entityManager);
        this.langueDao = new LangueDao(entityManager);
        this.lieuNaissanceDao = new LieuNaissanceDao(entityManager);

    }

    /**
     * Charge les entités existantes dans les caches de dédoublonnage.
     */
    private void preloadCaches() {
        filmsById.clear();
        filmDao.findAll().forEach(film -> filmsById.put(film.getIdImdb(), film));

        personnesById.clear();
        personneDao.findAll().forEach(personne -> personnesById.put(personne.getIdImdb(), personne));

        genresByName.clear();
        genreDao.findAll().forEach(genre ->
                genresByName.put(ImportValueParser.normalizeKey(genre.getNom()), genre));

        paysByName.clear();
        paysDao.findAll().forEach(pays ->
                paysByName.put(ImportValueParser.normalizeKey(pays.getNom()), pays));

        languesByName.clear();
        langueDao.findAll().forEach(langue ->
                languesByName.put(ImportValueParser.normalizeKey(langue.getNom()), langue));

        lieuxByLabel.clear();
        lieuNaissanceDao.findAll().forEach(lieu ->
                lieuxByLabel.put(ImportValueParser.normalizeKey(lieu.getLibelle()), lieu));

        rolesByKey.clear();
        roleDao.findAll().forEach(role -> {
            RoleKey key = new RoleKey(
                    role.getFilm().getIdImdb(),
                    role.getPersonne().getIdImdb());

            rolesByKey.put(key, role);
        });
    }

    /**
     * Recherche un pays par son nom ou le crée s'il est absent.
     *
     * @param paysDto données brutes du pays
     * @return pays trouvé ou créé, ou {@code null}
     */
    private Pays findOrCreatePays(PaysDto paysDto) {

        if (paysDto == null) {
            return null;
        }

        String nom = ImportValueParser.cleanText(paysDto.getNom());

        if (nom == null) {
            return null;
        }

        String key = ImportValueParser.normalizeKey(nom);
        Pays pays = paysByName.get(key);

        if (pays == null) {

            pays = new Pays();
            pays.setNom(nom);

            paysDao.persist(pays);
            paysByName.put(key, pays);
        }
        return pays;

    }

    /**
     * Recherche une langue par son nom ou la crée si elle est absente.
     *
     * @param text nom brut de la langue
     * @return langue trouvée ou créée, ou {@code null}
     */
    private Langue findOrCreateLangue(String text) {

        if (text == null) {
            return null;
        }

        String nom = ImportValueParser.cleanText(text);

        if (nom == null || "None".equalsIgnoreCase(nom)) {
            return null;
        }

        String key = ImportValueParser.normalizeKey(nom);
        Langue langue = languesByName.get(key);

        if (langue == null) {

            langue = new Langue();
            langue.setNom(nom);

            langueDao.persist(langue);
            languesByName.put(key, langue);
        }
        return langue;

    }

    /**
     * Recherche un genre par son nom ou le crée s'il est absent.
     *
     * @param text nom brut du genre
     * @return genre trouvé ou créé, ou {@code null}
     */
    private Genre findOrCreateGenre(String text) {
        String nom = ImportValueParser.cleanText(text);

        if (nom == null) {
            return null;
        }

        String key = ImportValueParser.normalizeKey(nom);
        Genre genre = genresByName.get(key);

        if (genre == null) {
            genre = new Genre();
            genre.setNom(nom);

            genreDao.persist(genre);
            genresByName.put(key, genre);
        }

        return genre;
    }

    /**
     * Recherche un lieu de naissance par son libellé ou le crée s'il est absent.
     *
     * @param text libellé brut du lieu
     * @return lieu trouvé ou créé, ou {@code null}
     */
    private LieuNaissance findOrCreateLieuNaissance(String text) {
        String libelle = ImportValueParser.cleanText(text);

        if (libelle == null) {
            return null;
        }

        String key = ImportValueParser.normalizeKey(libelle);
        LieuNaissance lieu = lieuxByLabel.get(key);

        if (lieu == null) {
            lieu = new LieuNaissance();
            lieu.setLibelle(libelle);

            lieuNaissanceDao.persist(lieu);
            lieuxByLabel.put(key, lieu);
        }

        return lieu;
    }

    /**
     * Recherche une personne par son identifiant IMDb ou la crée si elle est absente.
     *
     * @param personneDto données brutes de la personne
     * @return personne trouvée ou créée, ou {@code null}
     */
    private Personne findOrCreatePersonne(PersonneDto personneDto) {
        if (personneDto == null) {
            return null;
        }

        String personneId = ImportValueParser.cleanText(personneDto.getId());

        if (personneId == null) {
            throw new IllegalArgumentException("Personne sans identifiant IMDb");
        }

        Personne personne = personnesById.get(personneId);

        if (personne == null) {
            String identite = ImportValueParser.cleanText(personneDto.getIdentite());

            if (identite == null) {
                throw new IllegalArgumentException("Personne sans identité : " + personneId);
            }

            personne = new Personne();
            personne.setIdImdb(personneId);
            personne.setIdentite(identite);
            personne.setUrl(ImportValueParser.cleanText(personneDto.getUrl()));
            personne.setTaille(ImportValueParser.parseHeight(personneDto.getHeight()));

            if (personneDto.getNaissance() != null) {
                personne.setDateNaissance(
                        ImportValueParser.parseDate(personneDto.getNaissance().getDateNaissance()));
                personne.setLieuNaissance(
                        findOrCreateLieuNaissance(personneDto.getNaissance().getLieuNaissance()));
            }

            personneDao.persist(personne);
            personnesById.put(personneId, personne);
        } else if (personne.getTaille() == null) {
            personne.setTaille(ImportValueParser.parseHeight(personneDto.getHeight()));
        }

        return personne;
    }

    /**
     * Convertit un lieu de tournage brut en valeur embarquée.
     *
     * @param lieuDto données brutes du lieu
     * @return lieu converti, ou {@code null}
     */
    private LieuTournage mapLieuTournage(LieuTournageDto lieuDto) {
        if (lieuDto == null) {
            return null;
        }

        String ville = ImportValueParser.cleanText(lieuDto.getVille());
        String etatDept = ImportValueParser.cleanText(lieuDto.getEtatDept());
        String pays = ImportValueParser.cleanText(lieuDto.getPays());

        if (ville == null && etatDept == null && pays == null) {
            return null;
        }

        LieuTournage lieu = new LieuTournage();
        lieu.setVille(ville);
        lieu.setEtatDept(etatDept);
        lieu.setPays(pays);
        return lieu;
    }

    /**
     * Ajoute au film les genres fournis par le JSON.
     *
     * @param film entité à compléter
     * @param filmDto données brutes du film
     */
    private void addGenres(Film film, FilmDto filmDto) {
        if (filmDto.getGenres() == null) {
            return;
        }

        for (String nom : filmDto.getGenres()) {
            film.addGenre(findOrCreateGenre(nom));
        }
    }

    /**
     * Ajoute au film les réalisateurs fournis par le JSON.
     *
     * @param film entité à compléter
     * @param filmDto données brutes du film
     */
    private void addRealisateurs(Film film, FilmDto filmDto) {
        if (filmDto.getRealisateurs() == null) {
            return;
        }

        for (PersonneDto personneDto : filmDto.getRealisateurs()) {
            film.addRealisateur(findOrCreatePersonne(personneDto));
        }
    }

    /**
     * Extrait les identifiants IMDb du casting principal.
     *
     * @param filmDto données brutes du film
     * @return identifiants uniques du casting principal
     */
    private Set<String> extractPrincipalIds(FilmDto filmDto) {
        Set<String> principalIds = new HashSet<>();

        if (filmDto.getCastingPrincipal() == null) {
            return principalIds;
        }

        for (PersonneDto personneDto : filmDto.getCastingPrincipal()) {
            if (personneDto != null) {
                String personneId = ImportValueParser.cleanText(personneDto.getId());

                if (personneId != null) {
                    principalIds.add(personneId);
                }
            }
        }

        return principalIds;
    }

    /**
     * Crée ou complète les rôles du film.
     *
     * @param film entité à compléter
     * @param filmDto données brutes du film
     */
    private void addRoles(Film film, FilmDto filmDto) {
        if (filmDto.getRoles() == null) {
            return;
        }

        Set<String> principalIds = extractPrincipalIds(filmDto);

        for (RoleDto roleDto : filmDto.getRoles()) {
            if (roleDto == null) {
                continue;
            }

            Personne personne = findOrCreatePersonne(roleDto.getActeur());

            if (personne == null) {
                continue;
            }

            RoleKey key = new RoleKey(film.getIdImdb(), personne.getIdImdb());
            Role role = rolesByKey.get(key);
            String personnage = ImportValueParser.cleanText(roleDto.getCharacterName());
            boolean principal = principalIds.contains(personne.getIdImdb());

            if (role == null) {
                role = new Role();
                role.setPersonnage(personnage);
                role.setPrincipal(principal);

                film.addRole(role);
                personne.addRole(role);
                roleDao.persist(role);
                rolesByKey.put(key, role);
            } else {
                if (role.getPersonnage() == null && personnage != null) {
                    role.setPersonnage(personnage);
                }

                if (!role.isPrincipal() && principal) {
                    role.setPrincipal(true);
                }
            }
        }
    }

    /**
     * Convertit et importe une entrée de film du JSON.
     *
     * @param filmDto données brutes du film
     */
    private void importFilm(FilmDto filmDto) {
        String filmId = ImportValueParser.cleanText(filmDto.getId());

        if (filmId == null) {
            throw new IllegalArgumentException("Film sans identifiant IMDb");
        }

        Film film = filmsById.get(filmId);
        Integer parsedYear = ImportValueParser.parseYear(filmDto.getAnneeSortie());

        if (film == null) {
            String titre = ImportValueParser.cleanText(filmDto.getNom());

            if (titre == null) {
                throw new IllegalArgumentException("Film sans titre : " + filmId);
            }

            if (parsedYear == null) {
                throw new IllegalArgumentException("Film sans année valide : " + filmId);
            }

            film = new Film();

            film.setIdImdb(filmId);
            film.setTitre(titre);
            film.setAnneeSortie(parsedYear);
            film.setNote(ImportValueParser.parseRating(filmDto.getRating()));
            film.setResume(ImportValueParser.cleanText(filmDto.getPlot()));
            film.setUrl(ImportValueParser.cleanText(filmDto.getUrl()));

            filmDao.persist(film);
            filmsById.put(filmId, film);
        } else if (parsedYear != null && (film.getAnneeSortie() == null || parsedYear < film.getAnneeSortie())) {

            film.setAnneeSortie(parsedYear);
        }

        Pays pays = findOrCreatePays(filmDto.getPays());

        if (film.getPays() == null && pays != null) {
            film.setPays(pays);
        }

        Langue langue = findOrCreateLangue(filmDto.getLangue());

        if (film.getLangue() == null && langue != null) {
            film.setLangue(langue);
        }

        if (film.getLieuTournage() == null) {
            film.setLieuTournage(mapLieuTournage(filmDto.getLieuTournage()));
        }

        addGenres(film, filmDto);
        addRealisateurs(film, filmDto);
        addRoles(film, filmDto);

    }

    /**
     * Exécute l'import complet dans une transaction unique.
     *
     * @throws IOException si la lecture du fichier JSON échoue
     */
    public void importFilms() throws IOException {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            preloadCaches();
            filmJsonReader.read(this::importFilm);

            transaction.commit();
        } catch (IOException | RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }
    }

    /**
     * Clé de dédoublonnage d'un rôle par film et personne.
     */
    private record RoleKey(String filmId, String personneId) {
    }

}
