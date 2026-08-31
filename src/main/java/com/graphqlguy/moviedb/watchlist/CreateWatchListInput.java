package com.graphqlguy.moviedb.watchlist;

public record CreateWatchListInput(String name, String description, Boolean isPublic) {}
