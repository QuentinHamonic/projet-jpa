package fr.diginamic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données brutes d'une personne provenant du fichier JSON.
 */
@Getter
@Setter
@NoArgsConstructor
public class PersonneDto {

    private String id;
    private String identite;
    private NaissanceDto naissance;
    private String url;
    private String height;
}
