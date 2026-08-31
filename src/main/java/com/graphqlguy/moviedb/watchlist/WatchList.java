package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.user.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "watch_lists")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class WatchList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "watchList", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WatchListItem> items = new ArrayList<>();
}
