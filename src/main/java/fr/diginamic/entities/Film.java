package fr.diginamic.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Œuvre cinématographique ou télévisuelle identifiée par son identifiant IMDb.
 */
@Entity
@Table(name = "film")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {

    @Id
    @Column(name = "id_imdb", length = 10, nullable = false)
    @EqualsAndHashCode.Include
    private String imdbId;

    @Column(name = "titre", length = 150, nullable = false)
    private String title;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(name = "annee_sortie", nullable = false)
    private Integer releaseYear;

    @Column(name = "note", precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = "resume", length = 500)
    private String plot;

    @Column(name = "url", length = 100)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pays_id")
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "langue_id")
    private Language language;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "tournage_ville", length = 150)),
            @AttributeOverride(name = "stateDepartment", column = @Column(name = "tournage_etat_dept", length = 100)),
            @AttributeOverride(name = "country", column = @Column(name = "tournage_pays", length = 100))
    })
    private FilmingLocation filmingLocation;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<Role> roles = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "film_realisateur", joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "id_imdb"), inverseJoinColumns = @JoinColumn(name = "personne_id", referencedColumnName = "id_imdb"))
    @Setter(AccessLevel.NONE)
    private Set<Person> directors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "film_genre", joinColumns = @JoinColumn(name = "film_id", referencedColumnName = "id_imdb"), inverseJoinColumns = @JoinColumn(name = "genre_id", referencedColumnName = "id"))
    @Setter(AccessLevel.NONE)
    private Set<Genre> genres = new HashSet<>();

    /**
     * Ajoute un rôle et synchronise son côté propriétaire.
     *
     * @param role rôle à rattacher
     */
    public void addRole(Role role) {
        if (role != null && !roles.contains(role)) {
            roles.add(role);
            role.setFilm(this);
        }
    }

    /**
     * Retire un rôle du film.
     *
     * @param role rôle à retirer
     */
    public void removeRole(Role role) {
        if (roles.remove(role)) {
            role.setFilm(null);
        }
    }

    /**
     * Ajoute un réalisateur sans autoriser de doublon.
     *
     * @param director personne réalisatrice
     */
    public void addDirector(Person director) {
        if (director != null) {
            directors.add(director);
        }
    }

    /**
     * Ajoute un genre sans autoriser de doublon.
     *
     * @param genre genre du film
     */
    public void addGenre(Genre genre) {
        if (genre != null) {
            genres.add(genre);
        }
    }
}
