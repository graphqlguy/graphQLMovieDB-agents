package com.graphqlguy.moviedb.watchlist;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The schema maps these fields to the DateTime scalar, and ExtendedScalars.DateTime
 * serializes OffsetDateTime. A LocalDateTime here compiles and the application starts,
 * then the field fails at runtime the first time any client selects it. That is how the
 * original defect survived: no human client had ever asked for createdAt, and the first
 * thing to ask was an agent generating its selection set mechanically.
 */
class WatchListSchemaTest {

    @Test
    void watchListCreatedAtIsOffsetDateTime() throws Exception {
        Field f = WatchList.class.getDeclaredField("createdAt");
        assertThat(f.getType()).isEqualTo(OffsetDateTime.class);
    }

    @Test
    void watchListItemAddedAtIsOffsetDateTime() throws Exception {
        Field f = WatchListItem.class.getDeclaredField("addedAt");
        assertThat(f.getType()).isEqualTo(OffsetDateTime.class);
    }

    @Test
    void watchListItemWatchedAtIsOffsetDateTime() throws Exception {
        Field f = WatchListItem.class.getDeclaredField("watchedAt");
        assertThat(f.getType()).isEqualTo(OffsetDateTime.class);
    }
}
