package fr.diginamic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données brutes de naissance provenant du fichier JSON.
 */
@Getter
@Setter
@NoArgsConstructor
public class NaissanceDto {

    private String dateNaissance;
    private String lieuNaissance;
}
