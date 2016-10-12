package br.com.tiagohs.popmovies.util;

import java.util.ArrayList;
import java.util.List;

import br.com.tiagohs.popmovies.model.atwork.Artwork;
import br.com.tiagohs.popmovies.model.atwork.ArtworkMedia;
import br.com.tiagohs.popmovies.model.credits.CreditMovieBasic;
import br.com.tiagohs.popmovies.model.credits.MediaCreditCast;
import br.com.tiagohs.popmovies.model.credits.MediaCreditCrew;
import br.com.tiagohs.popmovies.model.dto.ImageDTO;
import br.com.tiagohs.popmovies.model.dto.ItemListDTO;
import br.com.tiagohs.popmovies.model.dto.MovieListDTO;
import br.com.tiagohs.popmovies.model.dto.PersonListDTO;
import br.com.tiagohs.popmovies.model.keyword.Keyword;
import br.com.tiagohs.popmovies.model.movie.Genre;
import br.com.tiagohs.popmovies.model.movie.Movie;
import br.com.tiagohs.popmovies.model.person.PersonFind;
import br.com.tiagohs.popmovies.model.person.PersonInfo;

public class DTOUtils {

    public static List<MovieListDTO> createMovieListDTO(List<Movie> movies) {
        List<MovieListDTO> moviesDTO = new ArrayList<>();

        for (Movie movie : movies) {
            if (movie.getPosterPath() != null)
                moviesDTO.add(new MovieListDTO(movie.getId(), movie.getTitle(), movie.getPosterPath(), movie.getVoteAverage()));
        }

        return moviesDTO;
    }

    public static List<ItemListDTO> createKeywordsItemsListDTO(List<Keyword> keywords) {
        List<ItemListDTO> list = new ArrayList<>();

        for (Keyword k : keywords)
            list.add(new ItemListDTO(k.getId(), k.getName()));

        return list;
    }

    public static List<PersonListDTO> createCastPersonListDTO(List<MediaCreditCast> cast) {
        List<PersonListDTO> personListDTO = new ArrayList<>();

        for (MediaCreditCast c : cast)
            personListDTO.add(new PersonListDTO(c.getId(), c.getArtworkPath(), c.getName(), c.getCharacter()));

        return personListDTO;
    }

    public static List<PersonListDTO> createCrewPersonListDTO(List<MediaCreditCrew> cast) {
        List<PersonListDTO> personListDTO = new ArrayList<>();

        for (MediaCreditCrew c : cast)
            personListDTO.add(new PersonListDTO(c.getId(), c.getArtworkPath(), c.getName(), c.getDepartment()));

        return personListDTO;
    }

    public static List<PersonListDTO> createPersonListDTO(List<PersonFind> persons) {
        List<PersonListDTO> personListDTO = new ArrayList<>();

        for (PersonFind c : persons)
            personListDTO.add(new PersonListDTO(c.getId(), c.getProfilePath(), c.getName(), ""));

        return personListDTO;
    }

    public static List<ItemListDTO> createGenresItemsListDTO(List<Genre> genres) {
        List<ItemListDTO> list = new ArrayList<>();

        for (Genre g : genres)
            list.add(new ItemListDTO(g.getId(), g.getName()));

        return list;
    }

    public static List<ImageDTO> createPersonImagesDTO(PersonInfo person, int numTotalImages, List<Artwork> images) {
        numTotalImages = images.size() <= numTotalImages ? images.size(): numTotalImages;
        List<ImageDTO> imageDTOs = new ArrayList<>();

        for (int cont = 0; cont < numTotalImages; cont++) {
            Artwork image = images.get(cont);
            imageDTOs.add(new ImageDTO(person.getId(), image.getId(), image.getFilePath()));
        }

        return imageDTOs;
    }

    public static List<ImageDTO> createPersonImagesBackgroundDTO(PersonInfo person, int numImages, List<ArtworkMedia> images) {
        List<ImageDTO> imageDTOs = new ArrayList<>();

        for (int cont = 0; cont < numImages; cont++) {
            Artwork image = images.get(cont);
            imageDTOs.add(new ImageDTO(person.getId(), image.getId(), image.getFilePath()));
        }

        return imageDTOs;
    }

    public static List<MovieListDTO> createPersonKnowForMoviesDTO(List<CreditMovieBasic> personsMovies, int maxSize) {
        int numMovies = personsMovies.size() < maxSize ? personsMovies.size() : maxSize;
        List<MovieListDTO> moviesMovieListDTO = new ArrayList<>();

        for (int cont = 0; cont < numMovies; cont++) {
            CreditMovieBasic person = personsMovies.get(cont);
            moviesMovieListDTO.add(new MovieListDTO(person.getId(), person.getTitle(), person.getArtworkPath(), null));
        }

        return moviesMovieListDTO;
    }

}