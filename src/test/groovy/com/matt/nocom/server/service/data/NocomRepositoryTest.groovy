package com.matt.nocom.server.service.data

import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.tools.jdbc.MockConnection
import org.jooq.tools.jdbc.MockDataProvider
import org.jooq.tools.jdbc.MockExecuteContext
import org.jooq.tools.jdbc.MockResult
import spock.lang.Specification

import java.time.Instant

import static com.matt.nocom.server.postgres.codegen.Tables.PLAYERS
import static com.matt.nocom.server.postgres.codegen.Tables.PLAYER_SESSIONS

class NocomRepositoryTest extends Specification {
  def "sql must be valid"() {
    MockDataProvider dataProvider = { MockExecuteContext exe ->
      def ctx = DSL.using(SQLDialect.POSTGRES);
      def r0 = ctx.newResult(PLAYER_SESSIONS.JOIN);
      def r1 = ctx.newResult(PLAYER_SESSIONS.LEAVE);
      def r2 = ctx.newResult(PLAYERS.UUID);
      def r3 = ctx.newResult(PLAYERS.USERNAME);
      return [
          new MockResult(0, r0),
          new MockResult(0, r1),
          new MockResult(0, r2),
          new MockResult(0, r3)
      ] as MockResult[];
    }

    def connection = new MockConnection(dataProvider);
    def ctx = DSL.using(connection);

    def repo = new NocomRepository(ctx);

    expect: "there should be no results"
    repo.getPlayerSessions("example.com", Instant.MIN, Instant.MAX, 1, UUID.randomUUID()).isEmpty()
  }
}
