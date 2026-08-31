package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.title.TitleType;
import com.graphqlguy.moviedb.user.AppUser;
import com.graphqlguy.moviedb.user.Role;
import com.graphqlguy.moviedb.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The ownership boundary. A watch list is personal data, and the AI agents course
 * exposes myWatchLists and addWatchListItem to an agent, so every one of these paths
 * is reachable by a language model deciding what to call. The source shipped this
 * logic with two assertions and no test of access control at all.
 */
@SpringBootTest
@Transactional
class WatchListAuthorizationTest {

    @Autowired private WatchListService service;
    @Autowired private UserRepository users;

    private AppUser owner;
    private AppUser stranger;
    private WatchList privateList;

    @BeforeEach
    void setUp() {
        owner = users.findByUsername("wl_owner").orElseGet(() -> newUser("wl_owner"));
        stranger = users.findByUsername("wl_stranger").orElseGet(() -> newUser("wl_stranger"));
        authenticateAs(owner);
        privateList = service.create(new CreateWatchListInput("Owner's private list", null, false));
    }

    @Test
    void ownerCanReadTheirOwnPrivateList() {
        authenticateAs(owner);
        assertThat(service.getById(privateList.getId()).getId()).isEqualTo(privateList.getId());
    }

    @Test
    void strangerCannotReadAPrivateList() {
        authenticateAs(stranger);
        assertThatThrownBy(() -> service.getById(privateList.getId()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void unauthenticatedCallerCannotReadAPrivateList() {
        SecurityContextHolder.clearContext();
        assertThatThrownBy(() -> service.getById(privateList.getId()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void aPublicListIsReadableByAStranger() {
        authenticateAs(owner);
        WatchList open = service.create(new CreateWatchListInput("Open list", null, true));
        authenticateAs(stranger);
        assertThat(service.getById(open.getId()).getId()).isEqualTo(open.getId());
    }

    @Test
    void strangerCannotAddAnItemToSomeoneElsesList() {
        authenticateAs(stranger);
        assertThatThrownBy(() -> service.addItem(privateList.getId(), 1L, TitleType.MOVIE, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void strangerCannotRenameSomeoneElsesList() {
        authenticateAs(stranger);
        assertThatThrownBy(() ->
                service.update(privateList.getId(), new UpdateWatchListInput("hijacked", null, null)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void strangerCannotDeleteSomeoneElsesList() {
        authenticateAs(stranger);
        assertThatThrownBy(() -> service.delete(privateList.getId()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void unauthenticatedCallerCannotCreateAList() {
        SecurityContextHolder.clearContext();
        assertThatThrownBy(() -> service.create(new CreateWatchListInput("nope", null, false)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void myWatchListsReturnsOnlyTheCallersLists() {
        authenticateAs(stranger);
        List<WatchList> mine = service.myWatchLists();
        assertThat(mine).noneMatch(w -> w.getId().equals(privateList.getId()));
    }

    private AppUser newUser(String username) {
        return users.save(AppUser.builder()
                .username(username)
                .email(username + "@example.com")
                .password("{noop}test")
                .role(Role.USER)
                .build());
    }

    private void authenticateAs(AppUser user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getUsername(), "test", List.of()));
    }
}
