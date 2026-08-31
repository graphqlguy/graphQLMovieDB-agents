package com.graphqlguy.moviedb.shared;

import com.graphqlguy.moviedb.movie.Movie;
import com.graphqlguy.moviedb.tvshow.TvShow;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Content is the one interface over Movie and TvShow. A watchlist item references a
 * title polymorphically and returns Content, so the interface must expose an id for
 * an agent to do anything useful with what it reads back.
 */
class ContentInterfaceTest {

    @Test
    void movieIsContent() {
        assertThat(Content.class).isAssignableFrom(Movie.class);
    }

    @Test
    void tvShowIsContent() {
        assertThat(Content.class).isAssignableFrom(TvShow.class);
    }

    @Test
    void contentExposesAnId() throws Exception {
        assertThat(Content.class.getMethod("getId").getReturnType()).isEqualTo(Long.class);
    }
}
