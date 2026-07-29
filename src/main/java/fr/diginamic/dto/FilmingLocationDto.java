package fr.diginamic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données brutes d'un lieu de tournage provenant du fichier JSON.
 */
@Getter
@Setter
@NoArgsConstructor
public class FilmingLocationDto {

    @JsonProperty("ville")
    private String city;

    @JsonProperty("etatDept")
    private String stateDepartment;

    @JsonProperty("pays")
    private String country;
}
