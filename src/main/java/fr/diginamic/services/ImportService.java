package fr.diginamic.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.diginamic.dao.BirthPlaceDao;
import fr.diginamic.dao.CountryDao;
import fr.diginamic.dao.FilmDao;
import fr.diginamic.dao.GenreDao;
import fr.diginamic.dao.LanguageDao;
import fr.diginamic.dao.PersonDao;
import fr.diginamic.dao.RoleDao;
import fr.diginamic.dto.CountryDto;
import fr.diginamic.dto.FilmDto;
import fr.diginamic.dto.FilmingLocationDto;
import fr.diginamic.dto.PersonDto;
import fr.diginamic.dto.RoleDto;
import fr.diginamic.entities.BirthPlace;
import fr.diginamic.entities.Country;
import fr.diginamic.entities.Film;
import fr.diginamic.entities.FilmingLocation;
import fr.diginamic.entities.Genre;
import fr.diginamic.entities.Language;
import fr.diginamic.entities.Person;
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
    private final PersonDao personDao;
    private final RoleDao roleDao;
    private final GenreDao genreDao;
    private final CountryDao countryDao;
    private final LanguageDao languageDao;
    private final BirthPlaceDao birthPlaceDao;

    private final Map<String, Film> filmsById = new HashMap<>();
    private final Map<String, Person> peopleById = new HashMap<>();
    private final Map<String, Genre> genresByName = new HashMap<>();
    private final Map<String, Country> countriesByName = new HashMap<>();
    private final Map<String, Language> languagesByName = new HashMap<>();
    private final Map<String, BirthPlace> birthPlacesByLabel = new HashMap<>();
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
        this.personDao = new PersonDao(entityManager);
        this.roleDao = new RoleDao(entityManager);
        this.genreDao = new GenreDao(entityManager);
        this.countryDao = new CountryDao(entityManager);
        this.languageDao = new LanguageDao(entityManager);
        this.birthPlaceDao = new BirthPlaceDao(entityManager);

    }

    /**
     * Charge les entités existantes dans les caches de dédoublonnage.
     */
    private void preloadCaches() {
        filmsById.clear();
        filmDao.findAll().forEach(film -> filmsById.put(film.getImdbId(), film));

        peopleById.clear();
        personDao.findAll().forEach(person -> peopleById.put(person.getImdbId(), person));

        genresByName.clear();
        genreDao.findAll().forEach(genre ->
                genresByName.put(ImportValueParser.normalizeKey(genre.getName()), genre));

        countriesByName.clear();
        countryDao.findAll().forEach(country ->
                countriesByName.put(ImportValueParser.normalizeKey(country.getName()), country));

        languagesByName.clear();
        languageDao.findAll().forEach(language ->
                languagesByName.put(ImportValueParser.normalizeKey(language.getName()), language));

        birthPlacesByLabel.clear();
        birthPlaceDao.findAll().forEach(birthPlace ->
                birthPlacesByLabel.put(ImportValueParser.normalizeKey(birthPlace.getLabel()), birthPlace));

        rolesByKey.clear();
        roleDao.findAll().forEach(role -> {
            RoleKey key = new RoleKey(
                    role.getFilm().getImdbId(),
                    role.getPerson().getImdbId());

            rolesByKey.put(key, role);
        });
    }

    /**
     * Recherche un pays par son nom ou le crée s'il est absent.
     *
     * @param countryDto données brutes du pays
     * @return pays trouvé ou créé, ou {@code null}
     */
    private Country findOrCreateCountry(CountryDto countryDto) {

        if (countryDto == null) {
            return null;
        }

        String name = ImportValueParser.cleanText(countryDto.getName());

        if (name == null) {
            return null;
        }

        String key = ImportValueParser.normalizeKey(name);
        Country country = countriesByName.get(key);

        if (country == null) {

            country = new Country();
            country.setName(name);

            countryDao.persist(country);
            countriesByName.put(key, country);
        }
        return country;

    }

    /**
     * Recherche une langue par son nom ou la crée si elle est absente.
     *
     * @param text nom brut de la langue
     * @return langue trouvée ou créée, ou {@code null}
     */
    private Language findOrCreateLanguage(String text) {

        if (text == null) {
            return null;
        }

        String name = ImportValueParser.cleanText(text);

        if (name == null || "None".equalsIgnoreCase(name)) {
            return null;
        }

        String key = ImportValueParser.normalizeKey(name);
        Language language = languagesByName.get(key);

        if (language == null) {

            language = new Language();
            language.setName(name);

            languageDao.persist(language);
            languagesByName.put(key, language);
        }
        return language;

    }

    /**
     * Recherche un genre par son nom ou le crée s'il est absent.
     *
     * @param text nom brut du genre
     * @return genre trouvé ou créé, ou {@code null}
     */
    private Genre findOrCreateGenre(String text) {
        String name = ImportValueParser.cleanText(text);

        if (name == null) {
            return null;
        }

        String key = ImportValueParser.normalizeKey(name);
        Genre genre = genresByName.get(key);

        if (genre == null) {
            genre = new Genre();
            genre.setName(name);

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
    private BirthPlace findOrCreateBirthPlace(String text) {
        String label = ImportValueParser.cleanText(text);

        if (label == null) {
            return null;
        }

        String key = ImportValueParser.normalizeKey(label);
        BirthPlace birthPlace = birthPlacesByLabel.get(key);

        if (birthPlace == null) {
            birthPlace = new BirthPlace();
            birthPlace.setLabel(label);

            birthPlaceDao.persist(birthPlace);
            birthPlacesByLabel.put(key, birthPlace);
        }

        return birthPlace;
    }

    /**
     * Recherche une personne par son identifiant IMDb ou la crée si elle est absente.
     *
     * @param personDto données brutes de la personne
     * @return personne trouvée ou créée, ou {@code null}
     */
    private Person findOrCreatePerson(PersonDto personDto) {
        if (personDto == null) {
            return null;
        }

        String personId = ImportValueParser.cleanText(personDto.getImdbId());

        if (personId == null) {
            throw new IllegalArgumentException("Personne sans identifiant IMDb");
        }

        Person person = peopleById.get(personId);

        if (person == null) {
            String name = ImportValueParser.cleanText(personDto.getName());

            if (name == null) {
                throw new IllegalArgumentException("Personne sans identité : " + personId);
            }

            person = new Person();
            person.setImdbId(personId);
            person.setName(name);
            person.setUrl(ImportValueParser.cleanText(personDto.getUrl()));
            person.setHeight(ImportValueParser.parseHeight(personDto.getHeight()));

            if (personDto.getBirth() != null) {
                person.setBirthDate(
                        ImportValueParser.parseDate(personDto.getBirth().getBirthDate()));
                person.setBirthPlace(
                        findOrCreateBirthPlace(personDto.getBirth().getBirthPlace()));
            }

            personDao.persist(person);
            peopleById.put(personId, person);
        } else if (person.getHeight() == null) {
            person.setHeight(ImportValueParser.parseHeight(personDto.getHeight()));
        }

        return person;
    }

    /**
     * Convertit un lieu de tournage brut en valeur embarquée.
     *
     * @param locationDto données brutes du lieu
     * @return lieu converti, ou {@code null}
     */
    private FilmingLocation mapFilmingLocation(FilmingLocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }

        String city = ImportValueParser.cleanText(locationDto.getCity());
        String stateDepartment = ImportValueParser.cleanText(locationDto.getStateDepartment());
        String country = ImportValueParser.cleanText(locationDto.getCountry());

        if (city == null && stateDepartment == null && country == null) {
            return null;
        }

        FilmingLocation location = new FilmingLocation();
        location.setCity(city);
        location.setStateDepartment(stateDepartment);
        location.setCountry(country);
        return location;
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

        for (String name : filmDto.getGenres()) {
            film.addGenre(findOrCreateGenre(name));
        }
    }

    /**
     * Ajoute au film les réalisateurs fournis par le JSON.
     *
     * @param film entité à compléter
     * @param filmDto données brutes du film
     */
    private void addDirectors(Film film, FilmDto filmDto) {
        if (filmDto.getDirectors() == null) {
            return;
        }

        for (PersonDto personDto : filmDto.getDirectors()) {
            film.addDirector(findOrCreatePerson(personDto));
        }
    }

    /**
     * Extrait les identifiants IMDb du casting principal.
     *
     * @param filmDto données brutes du film
     * @return identifiants uniques du casting principal
     */
    private Set<String> extractMainCastIds(FilmDto filmDto) {
        Set<String> mainCastIds = new HashSet<>();

        if (filmDto.getMainCast() == null) {
            return mainCastIds;
        }

        for (PersonDto personDto : filmDto.getMainCast()) {
            if (personDto != null) {
                String personId = ImportValueParser.cleanText(personDto.getImdbId());

                if (personId != null) {
                    mainCastIds.add(personId);
                }
            }
        }

        return mainCastIds;
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

        Set<String> mainCastIds = extractMainCastIds(filmDto);

        for (RoleDto roleDto : filmDto.getRoles()) {
            if (roleDto == null) {
                continue;
            }

            Person person = findOrCreatePerson(roleDto.getActor());

            if (person == null) {
                continue;
            }

            RoleKey key = new RoleKey(film.getImdbId(), person.getImdbId());
            Role role = rolesByKey.get(key);
            String characterName = ImportValueParser.cleanText(roleDto.getCharacterName());
            boolean mainCast = mainCastIds.contains(person.getImdbId());

            if (role == null) {
                role = new Role();
                role.setCharacterName(characterName);
                role.setMainCast(mainCast);

                film.addRole(role);
                person.addRole(role);
                roleDao.persist(role);
                rolesByKey.put(key, role);
            } else {
                if (role.getCharacterName() == null && characterName != null) {
                    role.setCharacterName(characterName);
                }

                if (!role.isMainCast() && mainCast) {
                    role.setMainCast(true);
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
        String filmId = ImportValueParser.cleanText(filmDto.getImdbId());

        if (filmId == null) {
            throw new IllegalArgumentException("Film sans identifiant IMDb");
        }

        Film film = filmsById.get(filmId);
        Integer parsedYear = ImportValueParser.parseYear(filmDto.getReleaseYear());

        if (film == null) {
            String title = ImportValueParser.cleanText(filmDto.getTitle());

            if (title == null) {
                throw new IllegalArgumentException("Film sans titre : " + filmId);
            }

            if (parsedYear == null) {
                throw new IllegalArgumentException("Film sans année valide : " + filmId);
            }

            film = new Film();

            film.setImdbId(filmId);
            film.setTitle(title);
            film.setReleaseYear(parsedYear);
            film.setRating(ImportValueParser.parseRating(filmDto.getRating()));
            film.setPlot(ImportValueParser.cleanText(filmDto.getPlot()));
            film.setUrl(ImportValueParser.cleanText(filmDto.getUrl()));

            filmDao.persist(film);
            filmsById.put(filmId, film);
        } else if (parsedYear != null && (film.getReleaseYear() == null || parsedYear < film.getReleaseYear())) {

            film.setReleaseYear(parsedYear);
        }

        Country country = findOrCreateCountry(filmDto.getCountry());

        if (film.getCountry() == null && country != null) {
            film.setCountry(country);
        }

        Language language = findOrCreateLanguage(filmDto.getLanguage());

        if (film.getLanguage() == null && language != null) {
            film.setLanguage(language);
        }

        if (film.getFilmingLocation() == null) {
            film.setFilmingLocation(mapFilmingLocation(filmDto.getFilmingLocation()));
        }

        addGenres(film, filmDto);
        addDirectors(film, filmDto);
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
    private record RoleKey(String filmId, String personId) {
    }

}
