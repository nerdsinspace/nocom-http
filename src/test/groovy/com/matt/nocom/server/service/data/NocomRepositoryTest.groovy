package com.matt.nocom.server.service.data

import com.matt.nocom.server.model.data.Dimension
import com.matt.nocom.server.model.data.Track
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.tools.jdbc.MockConnection
import org.jooq.tools.jdbc.MockExecuteContext
import org.jooq.tools.jdbc.MockResult
import spock.lang.Specification

import java.time.Duration
import java.time.Instant

import static com.matt.nocom.server.postgres.codegen.Tables.*

class NocomRepositoryTest extends Specification {
  NocomRepository api;
  Closure<MockResult[]> mock = { DSLContext dsl, MockExecuteContext exe -> [] as MockResult[] };

  def mockDSL(Closure<MockResult[]> cl) {
    this.mock = cl;
  }

  def setup() {
    final provider = { MockExecuteContext exe -> mock.call(DSL.using(SQLDialect.DEFAULT), exe) };
    final connection = new MockConnection(provider);
    api = new NocomRepository(DSL.using(connection));
  }

  def "GetServers"() {
    given: 'a list servers'
    final MOCK_HOSTS = ['mc1.example.com', 'mc2.example.com'];

    and: 'a mock dsl'
    mockDSL { DSLContext dsl, exe ->
      final r0 = dsl.newResult(SERVERS.HOSTNAME);
      MOCK_HOSTS.each { hn -> r0.add(dsl.newRecord(SERVERS.HOSTNAME).values(hn)) }

      [new MockResult(1, r0)] as MockResult[];
    };

    when: 'getting a list of servers'
    def ret = api.getServers();

    then: 'should have two servers'
    ret.size() == 2

    and: 'should return a distinct set of servers'
    ret.containsAll(MOCK_HOSTS);
  }

  def "GetDimensions"() {
    given: 'a list dimensions'
    final MOCK_DIMS = [Dimension.NETHER, Dimension.OVERWORLD, Dimension.END];

    and: 'a mock dsl'
    mockDSL { DSLContext dsl, exe ->
      final r0 = dsl.newResult(DIMENSIONS.ORDINAL);
      MOCK_DIMS.each { d -> r0.add(dsl.newRecord(DIMENSIONS.ORDINAL).values(d.getOrdinal() as short)) }

      [new MockResult(1, r0)] as MockResult[];
    };

    when: 'getting a list of dimensions'
    def ret = api.getDimensions();

    then: 'should have three dimensions'
    ret.size() == 3

    then: 'should return a distinct list of dimensions'
    ret.containsAll(MOCK_DIMS);
  }

  def "GetMostRecentTracks"() {
    given: 'a track with a previous dimension'
    final track1 = [
        id            : 0,
        created_at    : Instant.ofEpochMilli(100_000),
        x             : 16 * 2 as Integer,
        z             : 16 * 7 as Integer,
        dimension     : Dimension.OVERWORLD.getOrdinal() as short,
        track_id      : 10,
        prev_track_id : 9,
        prev_dimension: Dimension.NETHER.getOrdinal() as short
    ];

    and: 'a track without a previous dimension'
    final track2 = [
        id            : 1,
        created_at    : Instant.ofEpochMilli(110_000),
        x             : 16 * 8 as Integer,
        z             : 16 * 5 as Integer,
        dimension     : Dimension.OVERWORLD.getOrdinal(),
        track_id      : 11,
        prev_track_id : null,
        prev_dimension: null
    ];

    and: 'a list of the tracks'
    final MOCK_TRACKS = [track1, track2];

    and: 'a mock dsl'
    mockDSL { DSLContext dsl, exe ->
      final fields = [
          HITS.CREATED_AT,
          HITS.X,
          HITS.Z,
          HITS.DIMENSION,
          HITS.TRACK_ID,
          TRACKS.as('prev').ID.as('prev_track_id'),
          TRACKS.as('prev').ID.as('prev_dimension')
      ];

      final r0 = dsl.newResult(fields);

      MOCK_TRACKS.each { tr ->
        r0.add(dsl.newRecord(fields).with { rec -> rec.from(tr); rec });
      }

      [new MockResult(r0.size(), r0)] as MockResult[]
    };

    when: 'getting recent tracks'
    def ret = api.getMostRecentTracks('fake.org', Instant.ofEpochSecond(100), Duration.ofSeconds(200));

    then: 'tracks must be equal'
    checkTrack(ret.get(0), track1);
    checkTrack(ret.get(1), track2);
  }

  void checkTrack(Track track, o) {
    assert track.createAt == o.created_at;
    assert track.dimension == Dimension.byOrdinal(o.dimension as int);
    assert track.x == o.x * 16;
    assert track.z == o.z * 16;
    assert track.server == o.server;
    assert track.trackId == o.track_id;
    assert track.previousTrackId == o.prev_track_id;
    if (track.previousDimension != null) {
      assert track.previousDimension == Dimension.byOrdinal(o.prev_dimension as int);
    }
  };

  def "GetTrackHistory"() {
    given: 'a hit'
    final hit = [
        x: 16 * 5 as int,
        z: 16 * 5 as int,
    ];

    and: 'a mock dsl'
    mockDSL { DSLContext dsl, exe ->
      final fields = [HITS.X, HITS.Z];

      final r0 = dsl.newResult(fields);
      r0.add(dsl.newRecord(fields).with { rec -> rec.from(hit); rec });

      [new MockResult(r0.size(), r0)] as MockResult[]
    }

    when: 'getting track history'
    def ret = api.getTrackHistory(0, 100);

    then: 'there must be one return value'
    ret.size() == 1;

    and: 'the return matches the hit expected'
    ret.get(0).x == hit.x * 16;
    ret.get(0).z == hit.z * 16;
  }

  def "GetFullTrackHistory"() {
    given: 'a mock track'
    final track = [
        track_id  : 0,
        created_at: Instant.ofEpochSecond(60),
        x         : 16 * 7,
        z         : 16 * 4,
        dimension : Dimension.OVERWORLD.getOrdinal() as short
    ];

    and: 'a mock dsl'
    mockDSL { DSLContext dsl, exe ->
      final fields = [
          DSL.field(DSL.name("track_hist", "track_id")),
          HITS.CREATED_AT,
          HITS.X,
          HITS.Z,
          HITS.DIMENSION
      ];

      final r0 = dsl.newResult(fields);
      r0.add(dsl.newRecord(fields).with { rec -> rec.from(track); rec });

      [new MockResult(r0.size(), r0)] as MockResult[]
    }

    when: 'getting the track history'
    def ret = api.getFullTrackHistory(0, 100);

    then: 'return should have one result'
    ret.size() == 1;

    and: 'return should match track'
    ret.get(0).trackId == track.track_id;
    ret.get(0).createdAt == track.created_at;
    ret.get(0).x == track.x * 16;
    ret.get(0).z == track.z * 16;
    ret.get(0).dimension == Dimension.byOrdinal(track.dimension);
  }

  def "GetRootClusters"() {
    given: 'a dbscan object'
    final dbscan = [
        id             : 1,
        x              : 420 * 16,
        z              : 69 * 16,
        dimension      : 0,
        server_id      : 0,
        is_node        : false,
        is_core        : false,
        cluster_parent : 0,
        disjoint_rank  : 420,
        disjoint_size  : 69,
        root_updated_at: 2000,
        ts_ranges      : null,
        last_init_hit  : 2000,
        first_init_hit : 1000
    ];

    and: 'a mock dsl'
    mockDSL { DSLContext dsl, exe ->
      final r0 = dsl.newResult(DBSCAN);
      r0.add(dsl.newRecord(DBSCAN).with { rec -> rec.from(dbscan); rec });

      [new MockResult(r0.size(), r0)] as MockResult[]
    }

    when: 'getting the root clusters'
    def ret = api.getRootClusters('test.example.com', Dimension.OVERWORLD);

    then: 'there should be one result'
    ret.size() == 1;

    and: 'the result should equal the mock dbscan object'
    ret.get(0).id == dbscan.id;
    ret.get(0).x == dbscan.x * 16;
    ret.get(0).z == dbscan.z * 16;
    ret.get(0).dimension == Dimension.byOrdinal(dbscan.dimension);
    ret.get(0).server == null;
    ret.get(0).core == dbscan.is_core;
    ret.get(0).clusterParent == dbscan.cluster_parent;
    ret.get(0).disjointRank == dbscan.disjoint_rank;
    ret.get(0).disjointSize == dbscan.disjoint_size;
    ret.get(0).updatedAt == Instant.ofEpochMilli(dbscan.root_updated_at);

    and: 'no leafs should exist'
    ret.get(0).leafs == null;
  }

  def "GetFullCluster"() {
  }

  def "GetClusterPlayerAssociations"() {
  }

  def "GetBotStatuses"() {
  }

  def "GetPlayerSessions"() {
//    final ctx = mockContext({ MockExecuteContext exe ->
//      final ctx = createContext();
//      def r0 = ctx.newResult(PLAYER_SESSIONS.JOIN);
//      def r1 = ctx.newResult(PLAYER_SESSIONS.LEAVE);
//      return [
//          new MockResult(0, r0),
//          new MockResult(0, r1),
//      ] as MockResult[];
//    });
//
//    def repo = new NocomRepository(ctx);
//
//    expect:
//    repo.getPlayerSessions("example.com", Duration.ofDays(1), UUID.randomUUID()).isEmpty()
  }

  def "GetPlayersConnectTime"() {
  }

  def "GetPlayersLatestSession"() {
  }

  def "GetBlockCountInCluster"() {
    given: 'the expected sum'
    final SUM = 420L;

    and: 'the dsl mock'
    mockDSL { DSLContext dsl, exe ->
      final num_blocks = DSL.field(DSL.name("num_blocks"), BigDecimal.class);

      final r0 = dsl.newResult(num_blocks);
      r0.add(dsl.newRecord(num_blocks).values(new BigDecimal(SUM)));

      [new MockResult(1, r0)] as MockResult[];
    };

    when: 'calling the method with valid parameters'
    def ret = api.getBlockCountInCluster(1, "test");

    then: 'should return an non-empty optional'
    ret.isPresent();

    and: 'the value should be what is expected'
    ret.orElse(null) == SUM;
  }
}
