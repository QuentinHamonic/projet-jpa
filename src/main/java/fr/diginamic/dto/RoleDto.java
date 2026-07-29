package fr.diginamic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Données brutes d'un rôle provenant du fichier JSON.
 */
@Getter
@Setter
@NoArgsConstructor
public class RoleDto {

    private String characterName;

    @JsonProperty("acteur")
    private PersonDto actor;
}
