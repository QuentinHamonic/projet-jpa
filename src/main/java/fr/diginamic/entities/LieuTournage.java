package fr.diginamic.entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Valeur embarquée décrivant le lieu de tournage d'un film.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class LieuTournage {

    private String ville;

    private String etatDept;

    private String pays;
}
