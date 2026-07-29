package fr.diginamic.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.diginamic.dto.FilmDto;

/**
 * Lit les films du fichier JSON en flux continu.
 */
public class FilmJsonReader {

    private final ObjectMapper mapper;

    /**
     * Crée un lecteur utilisant Jackson.
     */
    public FilmJsonReader() {
        this.mapper = new ObjectMapper();
    }

    /**
     * Lit chaque film et le transmet au consommateur fourni.
     *
     * @param consumer traitement appliqué à chaque film lu
     * @throws IOException si la ressource est absente ou invalide
     */
    public void read(Consumer<FilmDto> consumer) throws IOException {

        InputStream is = FilmJsonReader.class.getResourceAsStream("/films.json");
        if (is == null) {
            throw new IOException("Ressource /films.json introuvable");
        }

        try (is;
                JsonParser jsonParser = mapper.createParser(is);) {

            JsonToken token = jsonParser.nextToken();

            if (token != JsonToken.START_ARRAY) {
                throw new IOException("La racine doit être un tableau JSON");
            }

            while ((token = jsonParser.nextToken()) != JsonToken.END_ARRAY) {

                if (token == null) {
                    throw new IOException("Fin inattendue du fichier JSON");
                }
                if (token != JsonToken.START_OBJECT) {
                    throw new IOException("Un objet film était attendu");
                }

                FilmDto filmDto = mapper.readValue(jsonParser, FilmDto.class);

                consumer.accept(filmDto);
            }

        }

    }

}
