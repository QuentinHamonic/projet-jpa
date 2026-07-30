package fr.diginamic.services;

import java.util.List;

import fr.diginamic.dao.FilmDao;
import fr.diginamic.dao.PersonDao;
import fr.diginamic.dao.RoleDao;
import fr.diginamic.entities.Film;
import fr.diginamic.entities.Person;
import fr.diginamic.entities.Role;
import fr.diginamic.utils.ImportValueParser;
import jakarta.persistence.EntityManager;

/**
 * Fournit les recherches disponibles dans l'application cinéma.
 */
public class SearchService {

    private final FilmDao filmDao;
    private final PersonDao personDao;
    private final RoleDao roleDao;

    /**
     * Crée le service avec les DAO partageant le même gestionnaire d'entités.
     *
     * @param entityManager gestionnaire d'entités utilisé pour les recherches
     */
    public SearchService(EntityManager entityManager) {
        this.filmDao = new FilmDao(entityManager);
        this.personDao = new PersonDao(entityManager);
        this.roleDao = new RoleDao(entityManager);
    }

    private static String requireSearchTerm(String value) {
        String cleanedValue = ImportValueParser.cleanText(value);

        if (cleanedValue == null) {
            throw new IllegalArgumentException("Le terme de recherche est obligatoire");
        }

        return cleanedValue;
    }

    private static void requireDifferentTerms(String firstTerm, String secondTerm) {
        if (firstTerm.equalsIgnoreCase(secondTerm)) {
            throw new IllegalArgumentException("Les deux termes de recherche doivent être différents");
        }
    }

    private static void validateYearRange(int startYear, int endYear) {
        if (startYear > endYear) {
            throw new IllegalArgumentException("L'année de début doit précéder l'année de fin");
        }
    }

    /**
     * Recherche la filmographie d'un acteur.
     *
     * @param actorName nom complet de l'acteur
     * @return films dans lesquels il joue
     */
    public List<Film> findFilmography(String actorName) {
        return filmDao.findByActorName(requireSearchTerm(actorName));
    }

    /**
     * Recherche le casting d'un film.
     *
     * @param filmTitle titre complet du film
     * @return rôles du film, casting principal en premier
     */
    public List<Role> findCast(String filmTitle) {
        return roleDao.findCastByFilmTitle(requireSearchTerm(filmTitle));
    }

    /**
     * Recherche les films sortis entre deux années incluses.
     *
     * @param startYear première année de la période
     * @param endYear   dernière année de la période
     * @return films de la période
     */
    public List<Film> findFilmsBetweenYears(int startYear, int endYear) {
        validateYearRange(startYear, endYear);
        return filmDao.findByReleaseYearBetween(startYear, endYear);
    }

    /**
     * Recherche les films dans lesquels jouent deux acteurs.
     *
     * @param firstActorName  nom complet du premier acteur
     * @param secondActorName nom complet du second acteur
     * @return films communs aux deux acteurs
     */
    public List<Film> findCommonFilms(
            String firstActorName,
            String secondActorName) {

        String firstTerm = requireSearchTerm(firstActorName);
        String secondTerm = requireSearchTerm(secondActorName);
        requireDifferentTerms(firstTerm, secondTerm);

        return filmDao.findCommonByActorNames(firstTerm, secondTerm);
    }

    /**
     * Recherche les acteurs présents dans deux films.
     *
     * @param firstFilmTitle  titre complet du premier film
     * @param secondFilmTitle titre complet du second film
     * @return acteurs communs aux deux films
     */
    public List<Person> findCommonActors(
            String firstFilmTitle,
            String secondFilmTitle) {

        String firstTerm = requireSearchTerm(firstFilmTitle);
        String secondTerm = requireSearchTerm(secondFilmTitle);
        requireDifferentTerms(firstTerm, secondTerm);

        return personDao.findCommonActorsByFilmTitles(firstTerm, secondTerm);
    }

    /**
     * Recherche les films d'un acteur sortis entre deux années incluses.
     *
     * @param actorName nom complet de l'acteur
     * @param startYear première année de la période
     * @param endYear   dernière année de la période
     * @return films correspondant aux critères
     */
    public List<Film> findFilmsByActorBetweenYears(
            String actorName,
            int startYear,
            int endYear) {

        String actorTerm = requireSearchTerm(actorName);
        validateYearRange(startYear, endYear);

        return filmDao.findByActorNameAndReleaseYearBetween(actorTerm, startYear, endYear);
    }
}
