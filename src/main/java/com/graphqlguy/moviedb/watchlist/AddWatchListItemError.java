package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.title.TitleType;

/**
 * Why an addWatchListItem call added nothing. The code is the part a program branches
 * on; the message is for a human reading the response. An agent needs both: the code to
 * choose a recovery path, the message to explain itself to the person it is helping.
 *
 * Both cases here are mistakes an agent makes routinely. It invents a list id, or it
 * passes a title id it has not verified. Returning them as data lets the agent read
 * what went wrong and try again, which an entry in the errors array does not.
 */
public record AddWatchListItemError(String code, String message) {

    /**
     * The id was not a number at all. This is the mistake a language model makes most
     * often here: it passes the list's name, which it can see, where an opaque id
     * belongs. The message says what an id looks like, because a model that is told
     * only "invalid" tends to guess again the same way.
     */
    public static AddWatchListItemError invalidId(String field, String value) {
        return new AddWatchListItemError("INVALID_ID",
                field + " must be a numeric id such as \"2\", and \"" + value
                        + "\" is not one. Call myWatchLists to see the ids you can use.");
    }

    public static AddWatchListItemError watchListNotFound(Long watchListId) {
        return new AddWatchListItemError("WATCHLIST_NOT_FOUND",
                "No watch list exists with id " + watchListId
                        + ". Call myWatchLists to see the ids you can use.");
    }

    public static AddWatchListItemError titleNotFound(TitleType titleType, Long titleId) {
        return new AddWatchListItemError("TITLE_NOT_FOUND",
                "No " + titleType + " exists with id " + titleId);
    }
}
