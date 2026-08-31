package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.title.TitleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchListItemRepository extends JpaRepository<WatchListItem, Long> {
    List<WatchListItem> findByWatchListId(Long watchListId);
    Optional<WatchListItem> findByWatchListIdAndTitleIdAndTitleType(
        Long watchListId, Long titleId, TitleType titleType);
}
