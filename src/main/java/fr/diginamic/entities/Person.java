package fr.diginamic.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Personne pouvant intervenir comme acteur, réalisatrice ou réalisateur.
 */
@Entity
@Table(name = "personne", indexes = {
        @Index(name = "idx_personne_identite", columnList = "identite"),
        @Index(name = "idx_personne_lieu_naissance", columnList = "lieu_naissance_id")
})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Person {

    @Id
    @Column(name = "id_imdb", length = 10, nullable = false)
    @EqualsAndHashCode.Include
    private String imdbId;

    @Column(name = "identite", length = 100, nullable = false)
    private String name;

    @Column(name = "date_naissance")
    private LocalDate birthDate;

    @Column(name = "taille", precision = 3, scale = 2)
    private BigDecimal height;

    @Column(name = "url", length = 50)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lieu_naissance_id")
    private BirthPlace birthPlace;

    @OneToMany(mappedBy = "person")
    @Setter(AccessLevel.NONE)
    private List<Role> roles = new ArrayList<>();

    /**
     * Ajoute un rôle et synchronise son côté propriétaire.
     *
     * @param role rôle à rattacher
     */
    public void addRole(Role role) {
        if (role != null && !roles.contains(role)) {
            roles.add(role);
            role.setPerson(this);
        }
    }

    /**
     * Retire un rôle de la personne.
     *
     * @param role rôle à retirer
     */
    public void removeRole(Role role) {
        if (roles.remove(role)) {
            role.setPerson(null);
        }
    }
}
