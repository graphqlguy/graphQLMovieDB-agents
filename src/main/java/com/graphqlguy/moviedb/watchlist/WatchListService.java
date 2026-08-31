package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.movie.MovieRepository;
import com.graphqlguy.moviedb.title.TitleType;
import com.graphqlguy.moviedb.tvshow.TvShowRepository;
import com.graphqlguy.moviedb.user.AppUser;
import com.graphqlguy.moviedb.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchListService {

    private final WatchListRepository watchListRepository;
    private final WatchListItemRepository itemRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final TvShowRepository tvShowRepository;

    public WatchList getById(Long id) {
        WatchList wl = watchListRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("WatchList not found: " + id));
        if (Boolean.FALSE.equals(wl.getIsPublic())) {
            assertCallerIsOwner(wl);
        }
        return wl;
    }

    public List<WatchList> myWatchLists() {
        return watchListRepository.findByUser(currentUser());
    }

    public List<WatchList> publicWatchLists(int page, int size) {
        return watchListRepository.findByIsPublicTrue().stream()
            .skip((long) page * size)
            .limit(size)
            .toList();
    }

    @Transactional
    public WatchList create(CreateWatchListInput input) {
        AppUser current = currentUser();
        WatchList wl = WatchList.builder()
            .user(current)
            .name(input.name())
            .description(input.description())
            .isPublic(input.isPublic() != null && input.isPublic())
            .createdAt(OffsetDateTime.now())
            .build();
        return watchListRepository.save(wl);
    }

    @Transactional
    public WatchList update(Long id, UpdateWatchListInput input) {
        WatchList wl = watchListRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("WatchList not found: " + id));
        assertCallerIsOwner(wl);
        if (input.name() != null) wl.setName(input.name());
        if (input.description() != null) wl.setDescription(input.description());
        if (input.isPublic() != null) wl.setIsPublic(input.isPublic());
        return watchListRepository.save(wl);
    }

    @Transactional
    public void delete(Long id) {
        WatchList wl = watchListRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("WatchList not found: " + id));
        assertCallerIsOwner(wl);
        watchListRepository.delete(wl);
    }

    @Transactional
    public WatchListItem addItem(Long watchListId, Long titleId, TitleType titleType, String userNotes) {
        WatchList wl = watchListRepository.findById(watchListId)
            .orElseThrow(() -> new IllegalArgumentException("WatchList not found: " + watchListId));
        assertCallerIsOwner(wl);
        var existing = itemRepository.findByWatchListIdAndTitleIdAndTitleType(watchListId, titleId, titleType);
        if (existing.isPresent()) {
            return existing.get();
        }
        boolean titleExists = switch (titleType) {
            case MOVIE -> movieRepository.existsById(titleId);
            case TV_SHOW -> tvShowRepository.existsById(titleId);
        };
        if (!titleExists) {
            throw new IllegalArgumentException("Title not found: " + titleType + " " + titleId);
        }
        WatchListItem item = WatchListItem.builder()
            .watchList(wl)
            .titleId(titleId)
            .titleType(titleType)
            .userNotes(userNotes)
            .addedAt(OffsetDateTime.now())
            .watched(false)
            .build();
        return itemRepository.save(item);
    }

    @Transactional
    public void removeItem(Long itemId) {
        WatchListItem item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("WatchListItem not found: " + itemId));
        assertCallerIsOwner(item.getWatchList());
        itemRepository.delete(item);
    }

    @Transactional
    public WatchListItem markWatched(Long itemId, boolean watched) {
        WatchListItem item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("WatchListItem not found: " + itemId));
        assertCallerIsOwner(item.getWatchList());
        item.setWatched(watched);
        item.setWatchedAt(watched ? OffsetDateTime.now() : null);
        return itemRepository.save(item);
    }


    private AppUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        return userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new AccessDeniedException("Authenticated user not found"));
    }

    private void assertCallerIsOwner(WatchList wl) {
        AppUser current = currentUser();
        if (!wl.getUser().getId().equals(current.getId())) {
            throw new AccessDeniedException("WatchList belongs to a different user");
        }
    }
}
