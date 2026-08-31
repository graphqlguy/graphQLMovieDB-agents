package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchListRepository extends JpaRepository<WatchList, Long> {
    List<WatchList> findByUser(AppUser user);
    List<WatchList> findByIsPublicTrue();
}
