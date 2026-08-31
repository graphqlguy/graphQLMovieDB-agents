package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.title.TitleType;

/** No movie or TV show with that id. Distinguishable so the controller can type it. */
public class TitleNotFoundException extends RuntimeException {
    private final TitleType titleType;
    private final Long titleId;

    public TitleNotFoundException(TitleType titleType, Long titleId) {
        super("Title not found: " + titleType + " " + titleId);
        this.titleType = titleType;
        this.titleId = titleId;
    }

    public TitleType getTitleType() {
        return titleType;
    }

    public Long getTitleId() {
        return titleId;
    }
}
