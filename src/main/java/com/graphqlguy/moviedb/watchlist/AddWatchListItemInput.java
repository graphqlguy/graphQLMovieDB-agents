package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.title.TitleType;

public record AddWatchListItemInput(
    String watchListId,
    String titleId,
    TitleType titleType,
    String userNotes
) {}
