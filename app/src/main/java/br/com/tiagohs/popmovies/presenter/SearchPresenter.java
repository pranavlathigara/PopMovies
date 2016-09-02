package br.com.tiagohs.popmovies.presenter;

import br.com.tiagohs.popmovies.util.enumerations.SearchType;
import br.com.tiagohs.popmovies.view.SearchView;

public interface SearchPresenter extends BasePresenter<SearchView> {

    void searchMovies(String query,
                             Boolean includeAdult,
                             Integer searchYear,
                             Integer primaryReleaseYear,
                             Integer currentPage);

    void searchPersons(String query,
                              Boolean includeAdult,
                              SearchType searchType,
                              Integer currentPage);
}
