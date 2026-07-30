package fr.diginamic;

import java.util.List;
import java.util.Scanner;

import fr.diginamic.config.JpaUtil;
import fr.diginamic.entities.Film;
import fr.diginamic.entities.Person;
import fr.diginamic.entities.Role;
import fr.diginamic.services.SearchService;
import jakarta.persistence.EntityManager;

/**
 * Application console permettant d'effectuer des recherches cinématographiques.
 */
public final class SearchApp {

    private static final String MENU = """
            1. Afficher la filmographie d'un acteur
            2. Afficher le casting d'un film
            3. Afficher les films sortis entre deux années
            4. Afficher les films communs à deux acteurs
            5. Afficher les acteurs communs à deux films
            6. Afficher les films d'un acteur sortis entre deux années
            7. Quitter
            """;

    private SearchApp() {
    }

    /**
     * Lance le menu de recherche puis ferme les ressources utilisées.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        EntityManager entityManager = JpaUtil.createEntityManager();

        try (Scanner scanner = new Scanner(System.in)) {
            SearchService searchService = new SearchService(entityManager);
            runMenu(scanner, searchService);
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }

            JpaUtil.close();
        }
    }

    private static void runMenu(Scanner scanner, SearchService searchService) {
        boolean running = true;
        clearScreen();

        while (running) {
            System.out.println(MENU);
            int choice = readInt(scanner, "Votre choix : ");

            try {
                switch (choice) {
                    case 1 -> displayFilmography(scanner, searchService);
                    case 2 -> displayCast(scanner, searchService);
                    case 3 -> displayFilmsBetweenYears(scanner, searchService);
                    case 4 -> displayCommonFilms(scanner, searchService);
                    case 5 -> displayCommonActors(scanner, searchService);
                    case 6 -> displayFilmsByActorBetweenYears(scanner, searchService);
                    case 7 -> running = false;
                    default -> System.out.println("Choix inconnu.");
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Erreur : " + exception.getMessage());
            }

            if (running) {
                waitForEnter(scanner);
                clearScreen();
            }
        }

        System.out.println("Au revoir.");
    }

    private static void displayFilmography(Scanner scanner, SearchService searchService) {

        String actorName = readText(scanner, "Nom de l'acteur : ");
        displayFilms(searchService.findFilmography(actorName));
    }

    private static void displayCast(Scanner scanner, SearchService searchService) {

        String filmTitle = readText(scanner, "Titre du film : ");
        List<Role> roles = searchService.findCast(filmTitle);

        if (roles.isEmpty()) {
            System.out.println("Aucun rôle trouvé.");
            return;
        }

        for (Role role : roles) {
            String characterName = role.getCharacterName() == null
                    ? "Personnage inconnu"
                    : role.getCharacterName();

            String mainCast = role.isMainCast()
                    ? "principal"
                    : "secondaire";

            System.out.printf(
                    "%s — %s (%s)%n",
                    role.getPerson().getName(),
                    characterName,
                    mainCast);
        }
    }

    private static void displayFilmsBetweenYears(Scanner scanner, SearchService searchService) {

        int startYear = readInt(scanner, "Année de début : ");
        int endYear = readInt(scanner, "Année de fin : ");
        displayFilms(searchService.findFilmsBetweenYears(startYear, endYear));
    }

    private static void displayCommonFilms(Scanner scanner, SearchService searchService) {

        String firstActorName = readText(scanner, "Premier acteur : ");
        String secondActorName = readText(scanner, "Second acteur : ");
        displayFilms(searchService.findCommonFilms(firstActorName, secondActorName));
    }

    private static void displayCommonActors(Scanner scanner, SearchService searchService) {

        String firstFilmTitle = readText(scanner, "Premier film : ");
        String secondFilmTitle = readText(scanner, "Second film : ");
        displayPeople(searchService.findCommonActors(firstFilmTitle, secondFilmTitle));
    }

    private static void displayFilmsByActorBetweenYears(Scanner scanner, SearchService searchService) {

        String actorName = readText(scanner, "Nom de l'acteur : ");
        int startYear = readInt(scanner, "Année de début : ");
        int endYear = readInt(scanner, "Année de fin : ");

        displayFilms(searchService.findFilmsByActorBetweenYears(actorName, startYear, endYear));
    }

    private static void displayFilms(List<Film> films) {
        if (films.isEmpty()) {
            System.out.println("Aucun film trouvé.");
            return;
        }

        for (Film film : films) {
            System.out.printf(
                    "%s (%d)%n",
                    film.getTitle(),
                    film.getReleaseYear());
        }
    }

    private static void displayPeople(List<Person> people) {
        if (people.isEmpty()) {
            System.out.println("Aucun acteur trouvé.");
            return;
        }

        for (Person person : people) {
            System.out.println(person.getName());
        }
    }

    private static String readText(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            String value = readText(scanner, prompt).trim();

            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Veuillez saisir un nombre entier.");
            }
        }
    }

    private static void waitForEnter(Scanner scanner) {
        System.out.print("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
