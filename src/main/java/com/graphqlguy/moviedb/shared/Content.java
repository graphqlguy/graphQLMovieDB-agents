package com.graphqlguy.moviedb.shared;

/**
 * The GraphQL Content interface: the fields a Movie and a TvShow have in common.
 * Declaring the accessors here makes the shared contract compiler-checked (both
 * entities already expose them via Lombok). Spring for GraphQL resolves the concrete
 * type, Movie or TvShow, by matching the class name to the schema type.
 */
public interface Content {

    /** The persistent identifier. A watchlist item returns Content, and an agent
     *  that reads one needs the id to do anything further with the title. */
    Long getId();

    String getTitle();

    Genre getGenre();

    Double getRating();

    String getPosterUrl();
}
