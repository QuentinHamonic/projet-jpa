package fr.diginamic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données brutes d'un pays provenant du fichier JSON.
 */
@Getter
@Setter
@NoArgsConstructor
public class PaysDto {

    private String nom;
    private String url;
}
