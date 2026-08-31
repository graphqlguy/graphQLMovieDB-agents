package com.graphqlguy.moviedb;

import graphql.schema.GraphQLSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.GraphQlSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The AI agents course curates an allow-list of exactly these operations in its Class 2,
 * and generates its tool catalog from them. If one disappears or is renamed here, the
 * catalog and every recorded transcript in the course stop matching this service, and the
 * failure shows up as a confusing model error rather than as a build failure. This test
 * is the contract between the backend and the lessons.
 */
@SpringBootTest
class AgentsCourseContractTest {

    @Autowired
    private GraphQlSource graphQlSource;

    private GraphQLSchema schema;

    @BeforeEach
    void setUp() {
        schema = graphQlSource.schema();
    }

    @Test
    void theSixAllowListedQueriesExist() {
        assertThat(schema.getQueryType().getFieldDefinitions())
                .extracting("name")
                .contains("movie", "movies", "searchMovies", "person", "tvShow", "myWatchLists");
    }

    @Test
    void theOnePermittedWriteExists() {
        assertThat(schema.getMutationType().getFieldDefinitions())
                .extracting("name")
                .contains("addWatchListItem");
    }

    @Test
    void addWatchListItemReturnsTypedDataRatherThanBareItem() {
        var field = schema.getMutationType().getFieldDefinition("addWatchListItem");
        assertThat(field.getType().toString()).contains("AddWatchListItemResponse");
    }

    @Test
    void theFlatWatchlistFromTheSeedIsGone() {
        assertThat(schema.getQueryType().getFieldDefinitions())
                .extracting("name")
                .doesNotContain("watchlist");
        assertThat(schema.getMutationType().getFieldDefinitions())
                .extracting("name")
                .doesNotContain("addToWatchlist", "setWatchStatus", "removeFromWatchlist");
    }

    @Test
    void thereIsExactlyOneInterfaceOverMovieAndTvShow() {
        assertThat(schema.getType("Content")).isNotNull();
        assertThat(schema.getType("Title")).isNull();
    }
}
