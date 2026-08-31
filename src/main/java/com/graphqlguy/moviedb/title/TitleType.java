package com.graphqlguy.moviedb.title;

/**
 * Discriminator for polymorphic references to Title (used by future types
 * like WatchListItem and Recommendation that need to reference either a
 * Movie or a TvShow). The values match the wire format expected by clients.
 */
public enum TitleType {
    MOVIE,
    TV_SHOW
}
