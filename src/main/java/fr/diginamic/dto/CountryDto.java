package fr.diginamic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données brutes d'un pays provenant du fichier JSON.
 */
@Getter
@Setter
@NoArgsConstructor
public class CountryDto {

    @JsonProperty("nom")
    private String name;

    private String url;
}
