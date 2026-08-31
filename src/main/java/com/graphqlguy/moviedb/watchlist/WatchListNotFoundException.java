package com.graphqlguy.moviedb.watchlist;

/** No watch list with that id. Distinguishable so the controller can type the outcome. */
public class WatchListNotFoundException extends RuntimeException {
    private final Long watchListId;

    public WatchListNotFoundException(Long watchListId) {
        super("WatchList not found: " + watchListId);
        this.watchListId = watchListId;
    }

    public Long getWatchListId() {
        return watchListId;
    }
}
