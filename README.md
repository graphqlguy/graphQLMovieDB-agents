# graphQLMovieDB-agents

The backend application for the [GraphQL for AI Agents (Beyond MCP) course](https://graphqlguy.com/docs/tutorial-ai-agents/overview). It provides the data our requests ask for: the agent we build during the course, in [`graphQLBeyondMCP`](https://github.com/graphqlguy/graphQLBeyondMCP), queries this service's GraphQL API over HTTP. (The course is publishing soon; the link goes live with it.)

You do not write any code here. Clone this repository, start it, and leave it running in its own terminal while you work through the course in the other one.

The application is a Spring for GraphQL movie database (Spring Boot 4, Java 21, in-memory H2): movies, TV shows, people, and reviews behind one GraphQL endpoint, with JWT-backed user accounts, named watch lists, live TMDB community ratings, a review subscription, and query instrumentation. It began in the site's [Spring GraphQL tutorial](https://graphqlguy.com/docs/tutorial-SpringGraphQL/your-first-graphql-service) and gained a few extra domain objects for this course, so clone it even if you built that tutorial's version yourself.

## Why this is separate from graphQLMovieDB-MCP

[`graphQLMovieDB-MCP`](https://github.com/graphqlguy/graphQLMovieDB-MCP) is the backend for the GraphQL + MCP course. The two are deliberately separate copies of the same starting application, so that either course can change its backend without disturbing the other.

The visible difference is the watch list. The MCP course gives each user one flat list (`watchlist`, `addToWatchlist`, `setWatchStatus`). This course has named lists that are private or public (`myWatchLists`, `publicWatchLists`, `createWatchList`, `addWatchListItem`, `markWatchListItemWatched`), which is what gives the safety class a write worth putting behind an approval gate, and gives the tool-generation class an idempotent mutation to describe. This copy also carries longer, agent-facing descriptions on the read operations the course allow-lists, and a `Content` interface over `Movie` and `TvShow`.

## Running

The course expects the service on port 8081, because `graphQLBeyondMCP` runs on the default 8080:

```bash
git clone https://github.com/graphqlguy/graphQLMovieDB-agents.git
cd graphQLMovieDB-agents
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

GraphQL is then at `:8081/graphql` and GraphiQL at `:8081/graphiql`. `./mvnw test` runs the test suite.

The database is in-memory and seeded on every start, so each run gives the same movies, people, reviews, and watch lists. Sign in through the `login` mutation with `user` / `user123`, the account that owns the two private demo lists the course reads. `admin` / `admin123` owns the one public list.

## Branches

Only `main`. This service stays the same throughout the course, so it needs no per-class branches; those live in [`graphQLBeyondMCP`](https://github.com/graphqlguy/graphQLBeyondMCP) as `agents_class_N`.
