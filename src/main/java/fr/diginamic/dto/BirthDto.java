package fr.diginamic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données brutes de naissance provenant du fichier JSON.
 */
@Getter
@Setter
@NoArgsConstructor
public class BirthDto {

    @JsonProperty("dateNaissance")
    private String birthDate;

    @JsonProperty("lieuNaissance")
    private String birthPlace;
}
