package com.graphqlguy.moviedb.watchlist;

/** Result of deleteWatchList, following the seed's delete-response convention. */
public record DeleteWatchListResponse(boolean success, String deletedId) {}
