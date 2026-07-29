package fr.diginamic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Données brutes d'un film provenant du fichier JSON.
 */
@Getter
@Setter
@NoArgsConstructor
public class FilmDto {

    @JsonProperty("id")
    private String imdbId;

    @JsonProperty("pays")
    private CountryDto country;

    @JsonProperty("nom")
    private String title;

    private String url;
    private String rating;
    private String plot;

    @JsonProperty("langue")
    private String language;

    @JsonProperty("lieuTournage")
    private FilmingLocationDto filmingLocation;

    @JsonProperty("realisateurs")
    private List<PersonDto> directors = new ArrayList<>();

    @JsonProperty("castingPrincipal")
    private List<PersonDto> mainCast = new ArrayList<>();

    @JsonProperty("anneeSortie")
    private String releaseYear;

    private List<RoleDto> roles = new ArrayList<>();
    private List<String> genres = new ArrayList<>();
}
