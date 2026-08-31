package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.movie.MovieRepository;
import com.graphqlguy.moviedb.shared.Content;
import com.graphqlguy.moviedb.tvshow.TvShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WatchListController {

    private final WatchListService service;
    private final MovieRepository movieRepository;
    private final TvShowRepository tvShowRepository;
    private final WatchListItemRepository itemRepository;

    @QueryMapping
    public WatchList watchList(@Argument String id) {
        return service.getById(Long.parseLong(id));
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<WatchList> myWatchLists() {
        return service.myWatchLists();
    }

    @QueryMapping
    public List<WatchList> publicWatchLists(@Argument Integer page, @Argument Integer size) {
        return service.publicWatchLists(page != null ? page : 0, size != null ? size : 20);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public WatchList createWatchList(@Argument CreateWatchListInput input) {
        return service.create(input);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public WatchList updateWatchList(@Argument String id, @Argument UpdateWatchListInput input) {
        return service.update(Long.parseLong(id), input);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public DeleteWatchListResponse deleteWatchList(@Argument String id) {
        service.delete(Long.parseLong(id));
        return new DeleteWatchListResponse(true, id);
    }

    /**
     * The one write this course's allow-list exposes to an agent. Both failure modes an
     * agent produces routinely, an invented list id and an unverified title id, come
     * back as data so the agent can read them and correct itself. A caller reaching for
     * a list it does not own gets an AccessDeniedException, which is left to propagate.
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public AddWatchListItemResponse addWatchListItem(@Argument AddWatchListItemInput input) {
        Long watchListId = parseIdOrNull(input.watchListId());
        if (watchListId == null) {
            return AddWatchListItemResponse.failed(
                    AddWatchListItemError.invalidId("watchListId", input.watchListId()));
        }
        Long titleId = parseIdOrNull(input.titleId());
        if (titleId == null) {
            return AddWatchListItemResponse.failed(
                    AddWatchListItemError.invalidId("titleId", input.titleId()));
        }
        try {
            WatchListItem item = service.addItem(watchListId, titleId, input.titleType(), input.userNotes());
            return AddWatchListItemResponse.added(item);
        } catch (WatchListNotFoundException e) {
            return AddWatchListItemResponse.failed(
                    AddWatchListItemError.watchListNotFound(e.getWatchListId()));
        } catch (TitleNotFoundException e) {
            return AddWatchListItemResponse.failed(
                    AddWatchListItemError.titleNotFound(e.getTitleType(), e.getTitleId()));
        }
    }

    /** GraphQL's ID scalar is a string, so an id that is not a number reaches us intact. */
    private static Long parseIdOrNull(String raw) {
        try {
            return Long.valueOf(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public RemoveWatchListItemResponse removeWatchListItem(@Argument String id) {
        service.removeItem(Long.parseLong(id));
        return new RemoveWatchListItemResponse(true, id);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public WatchListItem markWatchListItemWatched(@Argument String id, @Argument boolean watched) {
        return service.markWatched(Long.parseLong(id), watched);
    }

    /**
     * A watch list item references its title by id and a discriminator, with no
     * database-enforced foreign key, so a deleted Movie or TvShow leaves a dangling
     * reference. Returning null is deliberate: the schema types this field as nullable
     * Content, so a missing title costs the caller one field instead of collapsing the
     * whole list.
     */
    @SchemaMapping(typeName = "WatchListItem", field = "title")
    public Content title(WatchListItem item) {
        return switch (item.getTitleType()) {
            case MOVIE -> movieRepository.findById(item.getTitleId()).map(m -> (Content) m).orElse(null);
            case TV_SHOW -> tvShowRepository.findById(item.getTitleId()).map(t -> (Content) t).orElse(null);
        };
    }

    /**
     * Items are a lazy collection on the entity, so reading them off a detached WatchList
     * throws outside a session. Resolving them here loads every list's items in one query
     * instead of one per list, which is the same batching the seed uses for Movie.reviews.
     */
    @BatchMapping(typeName = "WatchList", field = "items")
    public Map<WatchList, List<WatchListItem>> items(List<WatchList> lists) {
        List<Long> ids = lists.stream().map(WatchList::getId).toList();
        Map<Long, List<WatchListItem>> byListId = itemRepository.findByWatchListIdIn(ids)
                .stream().collect(Collectors.groupingBy(i -> i.getWatchList().getId()));
        return lists.stream().collect(Collectors.toMap(
                wl -> wl, wl -> byListId.getOrDefault(wl.getId(), List.of())));
    }

    @BatchMapping(typeName = "WatchList", field = "itemCount")
    public Map<WatchList, Integer> itemCount(List<WatchList> lists) {
        List<Long> ids = lists.stream().map(WatchList::getId).toList();
        Map<Long, Long> counts = itemRepository.findByWatchListIdIn(ids)
                .stream().collect(Collectors.groupingBy(i -> i.getWatchList().getId(), Collectors.counting()));
        return lists.stream().collect(Collectors.toMap(
                wl -> wl, wl -> counts.getOrDefault(wl.getId(), 0L).intValue()));
    }
}
