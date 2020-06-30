package com.matt.nocom.server.service.data

import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.tools.jdbc.MockConnection
import org.jooq.tools.jdbc.MockDataProvider
import org.jooq.tools.jdbc.MockExecuteContext
import org.jooq.tools.jdbc.MockResult
import spock.lang.Specification

import java.time.Duration

import static com.matt.nocom.server.postgres.codegen.Tables.PLAYER_SESSIONS

class NocomRepositoryTest extends Specification {
  def "GetPlayerSessions"() {
    MockDataProvider dataProvider = { MockExecuteContext exe ->
      System.out.println exe.sql()
      def ctx = DSL.using(SQLDialect.POSTGRES);
      def r0 = ctx.newResult(PLAYER_SESSIONS.JOIN);
      def r1 = ctx.newResult(PLAYER_SESSIONS.LEAVE);
      return [
          new MockResult(0, r0),
          new MockResult(0, r1),
      ] as MockResult[];
    }

    def connection = new MockConnection(dataProvider);
    def ctx = DSL.using(connection);

    def repo = new NocomRepository(ctx);

    expect:
    repo.getPlayerSessions("example.com", Duration.ofDays(1), UUID.randomUUID()).isEmpty()
  }
}
