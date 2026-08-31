package com.graphqlguy.moviedb.watchlist;

/** Result of removeWatchListItem, following the seed's delete-response convention. */
public record RemoveWatchListItemResponse(boolean success, String deletedId) {}
