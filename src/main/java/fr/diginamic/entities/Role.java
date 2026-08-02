package fr.diginamic.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Participation d'une personne au casting d'un film.
 */
@Entity
@Table(name = "role_film", uniqueConstraints = @UniqueConstraint(name = "uk_role_film_film_personne", columnNames = {
        "film_id", "personne_id" }), indexes = {
                @Index(name = "idx_role_film_personne", columnList = "personne_id"),
                @Index(name = "idx_role_film_principal", columnList = "film_id, principal")
        })
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "personne_id", nullable = false)
    private Person person;

    @Column(name = "personnage", length = 100)
    private String characterName;

    @Column(name = "principal", nullable = false)
    private boolean mainCast;
}
