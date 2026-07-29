package fr.diginamic.dto;

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

    private String id;
    private PaysDto pays;
    private String nom;
    private String url;
    private String rating;
    private String plot;
    private String langue;
    private LieuTournageDto lieuTournage;
    private List<PersonneDto> realisateurs = new ArrayList<>();
    private List<PersonneDto> castingPrincipal = new ArrayList<>();
    private String anneeSortie;
    private List<RoleDto> roles = new ArrayList<>();
    private List<String> genres = new ArrayList<>();
}
