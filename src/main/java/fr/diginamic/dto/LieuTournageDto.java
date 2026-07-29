package fr.diginamic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données brutes d'un lieu de tournage provenant du fichier JSON.
 */
@Getter
@Setter
@NoArgsConstructor
public class LieuTournageDto {

    private String ville;
    private String etatDept;
    private String pays;
}
