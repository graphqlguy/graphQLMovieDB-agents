package com.graphqlguy.moviedb.watchlist;

/**
 * The result of addWatchListItem. A missing list or a missing title is an outcome the
 * caller can anticipate, so it travels in the data as a typed error and not in the
 * errors array. Authorization failures are deliberately not modelled here: a caller
 * reaching for someone else's list has not made a recoverable mistake, so that stays
 * an execution error.
 *
 * Adding a title already on the list is not a failure. The service returns the existing
 * item, which makes the mutation idempotent and therefore safe for an agent to retry.
 */
public record AddWatchListItemResponse(boolean success, WatchListItem item, AddWatchListItemError error) {

    public static AddWatchListItemResponse added(WatchListItem item) {
        return new AddWatchListItemResponse(true, item, null);
    }

    public static AddWatchListItemResponse failed(AddWatchListItemError error) {
        return new AddWatchListItemResponse(false, null, error);
    }
}
