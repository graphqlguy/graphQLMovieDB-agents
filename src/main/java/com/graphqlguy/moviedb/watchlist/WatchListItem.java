package com.graphqlguy.moviedb.watchlist;

import com.graphqlguy.moviedb.title.TitleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "watch_list_items",
       uniqueConstraints = @UniqueConstraint(columnNames = {"watch_list_id", "title_id", "title_type"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class WatchListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "watch_list_id", nullable = false)
    private WatchList watchList;

    @Column(name = "title_id", nullable = false)
    private Long titleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "title_type", nullable = false, length = 16)
    private TitleType titleType;

    @Column(name = "added_at", nullable = false)
    @Builder.Default
    private OffsetDateTime addedAt = OffsetDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private Boolean watched = false;

    @Column(name = "watched_at")
    private OffsetDateTime watchedAt;

    @Column(name = "user_notes", length = 1000)
    private String userNotes;
}
