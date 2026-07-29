package fr.diginamic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données brutes d'une personne provenant du fichier JSON.
 */
@Getter
@Setter
@NoArgsConstructor
public class PersonDto {

    @JsonProperty("id")
    private String imdbId;

    @JsonProperty("identite")
    private String name;

    @JsonProperty("naissance")
    private BirthDto birth;

    private String url;
    private String height;
}
