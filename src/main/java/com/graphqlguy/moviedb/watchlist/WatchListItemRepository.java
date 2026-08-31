package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.title.TitleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchListItemRepository extends JpaRepository<WatchListItem, Long> {
    List<WatchListItem> findByWatchListId(Long watchListId);

    /** Loads the items for many lists in one query, for the WatchList.items batch mapping. */
    List<WatchListItem> findByWatchListIdIn(List<Long> watchListIds);
    Optional<WatchListItem> findByWatchListIdAndTitleIdAndTitleType(
        Long watchListId, Long titleId, TitleType titleType);
}
