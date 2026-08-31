package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.title.TitleType;
import com.graphqlguy.moviedb.user.AppUser;
import com.graphqlguy.moviedb.user.Role;
import com.graphqlguy.moviedb.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * addWatchListItem is the one write this course's allow-list exposes to an agent, so the
 * mistakes a model actually makes have to come back as data it can read and act on. An
 * entry in the errors array tells the model only that something went wrong.
 */
@SpringBootTest
@Transactional
class AddWatchListItemResponseTest {

    @Autowired private WatchListController controller;
    @Autowired private WatchListService service;
    @Autowired private UserRepository users;

    private WatchList list;

    @BeforeEach
    void setUp() {
        AppUser owner = users.findByUsername("resp_owner").orElseGet(() -> users.save(AppUser.builder()
                .username("resp_owner").email("resp_owner@example.com")
                .password("{noop}test").role(Role.USER).build()));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(owner.getUsername(), "test", List.of()));
        list = service.create(new CreateWatchListInput("Response test list", null, false));
    }

    @Test
    void aListNameWhereAnIdBelongsComesBackAsData() {
        AddWatchListItemResponse r = controller.addWatchListItem(
                new AddWatchListItemInput("Response test list", "1", TitleType.MOVIE, null));

        assertThat(r.success()).isFalse();
        assertThat(r.error().code()).isEqualTo("INVALID_ID");
        assertThat(r.error().message()).contains("numeric id");
    }

    @Test
    void anUnknownListIdComesBackAsData() {
        AddWatchListItemResponse r = controller.addWatchListItem(
                new AddWatchListItemInput("999999", "1", TitleType.MOVIE, null));

        assertThat(r.success()).isFalse();
        assertThat(r.error().code()).isEqualTo("WATCHLIST_NOT_FOUND");
    }

    @Test
    void anUnknownTitleIdComesBackAsData() {
        AddWatchListItemResponse r = controller.addWatchListItem(
                new AddWatchListItemInput(String.valueOf(list.getId()), "999999", TitleType.MOVIE, null));

        assertThat(r.success()).isFalse();
        assertThat(r.error().code()).isEqualTo("TITLE_NOT_FOUND");
    }

    @Test
    void addingTheSameTitleTwiceIsIdempotent() {
        AddWatchListItemInput input =
                new AddWatchListItemInput(String.valueOf(list.getId()), "1", TitleType.MOVIE, null);

        AddWatchListItemResponse first = controller.addWatchListItem(input);
        AddWatchListItemResponse second = controller.addWatchListItem(input);

        assertThat(first.success()).isTrue();
        assertThat(second.success()).isTrue();
        assertThat(second.item().getId()).isEqualTo(first.item().getId());
    }
}
