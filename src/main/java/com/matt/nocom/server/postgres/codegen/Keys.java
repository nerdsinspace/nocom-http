/*
 * This file is generated by jOOQ.
 */
package com.matt.nocom.server.postgres.codegen;


import com.matt.nocom.server.postgres.codegen.tables.*;
import com.matt.nocom.server.postgres.codegen.tables.records.*;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;

import javax.annotation.processing.Generated;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code></code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<DbscanRecord, Integer> IDENTITY_DBSCAN = Identities0.IDENTITY_DBSCAN;
    public static final Identity<HitsRecord, Long> IDENTITY_HITS = Identities0.IDENTITY_HITS;
    public static final Identity<PlayersRecord, Integer> IDENTITY_PLAYERS = Identities0.IDENTITY_PLAYERS;
    public static final Identity<ServersRecord, Short> IDENTITY_SERVERS = Identities0.IDENTITY_SERVERS;
    public static final Identity<TracksRecord, Integer> IDENTITY_TRACKS = Identities0.IDENTITY_TRACKS;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<DbscanRecord> DBSCAN_PKEY = UniqueKeys0.DBSCAN_PKEY;
    public static final UniqueKey<DbscanToUpdateRecord> DBSCAN_TO_UPDATE_PKEY = UniqueKeys0.DBSCAN_TO_UPDATE_PKEY;
    public static final UniqueKey<DimensionsRecord> DIMENSIONS_PKEY = UniqueKeys0.DIMENSIONS_PKEY;
    public static final UniqueKey<DimensionsRecord> DIMENSIONS_NAME_KEY = UniqueKeys0.DIMENSIONS_NAME_KEY;
    public static final UniqueKey<HitsRecord> HITS_PKEY_2 = UniqueKeys0.HITS_PKEY_2;
    public static final UniqueKey<LastByServerRecord> LAST_BY_SERVER_PKEY = UniqueKeys0.LAST_BY_SERVER_PKEY;
    public static final UniqueKey<NotesRecord> NOTES_SERVER_ID_DIMENSION_X_Z_KEY = UniqueKeys0.NOTES_SERVER_ID_DIMENSION_X_Z_KEY;
    public static final UniqueKey<PlayersRecord> PLAYERS_PKEY = UniqueKeys0.PLAYERS_PKEY;
    public static final UniqueKey<PlayersRecord> PLAYERS_UUID_KEY = UniqueKeys0.PLAYERS_UUID_KEY;
    public static final UniqueKey<ServersRecord> SERVERS_PKEY = UniqueKeys0.SERVERS_PKEY;
    public static final UniqueKey<ServersRecord> SERVERS_HOSTNAME_KEY = UniqueKeys0.SERVERS_HOSTNAME_KEY;
    public static final UniqueKey<SpatialRefSysRecord> SPATIAL_REF_SYS_PKEY = UniqueKeys0.SPATIAL_REF_SYS_PKEY;
    public static final UniqueKey<StatusesRecord> STATUSES_PLAYER_ID_SERVER_ID_KEY = UniqueKeys0.STATUSES_PLAYER_ID_SERVER_ID_KEY;
    public static final UniqueKey<TracksRecord> TRACKS_PKEY = UniqueKeys0.TRACKS_PKEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<AssociationsRecord, DbscanRecord> ASSOCIATIONS__ASSOCIATIONS_CLUSTER_ID_FKEY = ForeignKeys0.ASSOCIATIONS__ASSOCIATIONS_CLUSTER_ID_FKEY;
    public static final ForeignKey<AssociationsRecord, PlayersRecord> ASSOCIATIONS__ASSOCIATIONS_PLAYER_ID_FKEY = ForeignKeys0.ASSOCIATIONS__ASSOCIATIONS_PLAYER_ID_FKEY;
    public static final ForeignKey<BlocksRecord, DimensionsRecord> BLOCKS__BLOCKS_DIMENSION_FKEY = ForeignKeys0.BLOCKS__BLOCKS_DIMENSION_FKEY;
    public static final ForeignKey<BlocksRecord, ServersRecord> BLOCKS__BLOCKS_SERVER_ID_FKEY = ForeignKeys0.BLOCKS__BLOCKS_SERVER_ID_FKEY;
    public static final ForeignKey<ChatRecord, PlayersRecord> CHAT__CHAT_REPORTED_BY_FKEY = ForeignKeys0.CHAT__CHAT_REPORTED_BY_FKEY;
    public static final ForeignKey<ChatRecord, ServersRecord> CHAT__CHAT_SERVER_ID_FKEY = ForeignKeys0.CHAT__CHAT_SERVER_ID_FKEY;
    public static final ForeignKey<DbscanRecord, DimensionsRecord> DBSCAN__DBSCAN_DIMENSION_FKEY = ForeignKeys0.DBSCAN__DBSCAN_DIMENSION_FKEY;
    public static final ForeignKey<DbscanRecord, ServersRecord> DBSCAN__DBSCAN_SERVER_ID_FKEY = ForeignKeys0.DBSCAN__DBSCAN_SERVER_ID_FKEY;
    public static final ForeignKey<DbscanRecord, DbscanRecord> DBSCAN__DBSCAN_CLUSTER_PARENT_FKEY = ForeignKeys0.DBSCAN__DBSCAN_CLUSTER_PARENT_FKEY;
    public static final ForeignKey<DbscanToUpdateRecord, DbscanRecord> DBSCAN_TO_UPDATE__DBSCAN_TO_UPDATE_DBSCAN_ID_FKEY = ForeignKeys0.DBSCAN_TO_UPDATE__DBSCAN_TO_UPDATE_DBSCAN_ID_FKEY;
    public static final ForeignKey<HitsRecord, ServersRecord> HITS__HITS_SERVER_ID_FKEY = ForeignKeys0.HITS__HITS_SERVER_ID_FKEY;
    public static final ForeignKey<HitsRecord, DimensionsRecord> HITS__HITS_DIMENSION_FKEY = ForeignKeys0.HITS__HITS_DIMENSION_FKEY;
    public static final ForeignKey<HitsRecord, TracksRecord> HITS__HITS_TRACK_ID_FKEY = ForeignKeys0.HITS__HITS_TRACK_ID_FKEY;
    public static final ForeignKey<LastByServerRecord, ServersRecord> LAST_BY_SERVER__LAST_BY_SERVER_SERVER_ID_FKEY = ForeignKeys0.LAST_BY_SERVER__LAST_BY_SERVER_SERVER_ID_FKEY;
    public static final ForeignKey<NotesRecord, DimensionsRecord> NOTES__NOTES_DIMENSION_FKEY = ForeignKeys0.NOTES__NOTES_DIMENSION_FKEY;
    public static final ForeignKey<NotesRecord, ServersRecord> NOTES__NOTES_SERVER_ID_FKEY = ForeignKeys0.NOTES__NOTES_SERVER_ID_FKEY;
    public static final ForeignKey<PlayerSessionsRecord, PlayersRecord> PLAYER_SESSIONS__PLAYER_SESSIONS_PLAYER_ID_FKEY = ForeignKeys0.PLAYER_SESSIONS__PLAYER_SESSIONS_PLAYER_ID_FKEY;
    public static final ForeignKey<PlayerSessionsRecord, ServersRecord> PLAYER_SESSIONS__PLAYER_SESSIONS_SERVER_ID_FKEY = ForeignKeys0.PLAYER_SESSIONS__PLAYER_SESSIONS_SERVER_ID_FKEY;
    public static final ForeignKey<SignsRecord, DimensionsRecord> SIGNS__SIGNS_DIMENSION_FKEY = ForeignKeys0.SIGNS__SIGNS_DIMENSION_FKEY;
    public static final ForeignKey<SignsRecord, ServersRecord> SIGNS__SIGNS_SERVER_ID_FKEY = ForeignKeys0.SIGNS__SIGNS_SERVER_ID_FKEY;
    public static final ForeignKey<StatusesRecord, PlayersRecord> STATUSES__STATUSES_PLAYER_ID_FKEY = ForeignKeys0.STATUSES__STATUSES_PLAYER_ID_FKEY;
    public static final ForeignKey<StatusesRecord, ServersRecord> STATUSES__STATUSES_SERVER_ID_FKEY = ForeignKeys0.STATUSES__STATUSES_SERVER_ID_FKEY;
    public static final ForeignKey<StatusesRecord, DimensionsRecord> STATUSES__STATUSES_DIMENSION_FKEY = ForeignKeys0.STATUSES__STATUSES_DIMENSION_FKEY;
    public static final ForeignKey<TracksRecord, HitsRecord> TRACKS__TRACKS_FIRST_HIT_ID_FKEY = ForeignKeys0.TRACKS__TRACKS_FIRST_HIT_ID_FKEY;
    public static final ForeignKey<TracksRecord, HitsRecord> TRACKS__TRACKS_LAST_HIT_ID_FKEY = ForeignKeys0.TRACKS__TRACKS_LAST_HIT_ID_FKEY;
    public static final ForeignKey<TracksRecord, DimensionsRecord> TRACKS__TRACKS_DIMENSION_FKEY = ForeignKeys0.TRACKS__TRACKS_DIMENSION_FKEY;
    public static final ForeignKey<TracksRecord, ServersRecord> TRACKS__TRACKS_SERVER_ID_FKEY = ForeignKeys0.TRACKS__TRACKS_SERVER_ID_FKEY;
    public static final ForeignKey<TracksRecord, TracksRecord> TRACKS__TRACKS_PREV_TRACK_ID_FKEY = ForeignKeys0.TRACKS__TRACKS_PREV_TRACK_ID_FKEY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<DbscanRecord, Integer> IDENTITY_DBSCAN = Internal.createIdentity(Dbscan.DBSCAN, Dbscan.DBSCAN.ID);
        public static Identity<HitsRecord, Long> IDENTITY_HITS = Internal.createIdentity(Hits.HITS, Hits.HITS.ID);
        public static Identity<PlayersRecord, Integer> IDENTITY_PLAYERS = Internal.createIdentity(Players.PLAYERS, Players.PLAYERS.ID);
        public static Identity<ServersRecord, Short> IDENTITY_SERVERS = Internal.createIdentity(Servers.SERVERS, Servers.SERVERS.ID);
        public static Identity<TracksRecord, Integer> IDENTITY_TRACKS = Internal.createIdentity(Tracks.TRACKS, Tracks.TRACKS.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<DbscanRecord> DBSCAN_PKEY = Internal.createUniqueKey(Dbscan.DBSCAN, "dbscan_pkey", Dbscan.DBSCAN.ID);
        public static final UniqueKey<DbscanToUpdateRecord> DBSCAN_TO_UPDATE_PKEY = Internal.createUniqueKey(DbscanToUpdate.DBSCAN_TO_UPDATE, "dbscan_to_update_pkey", DbscanToUpdate.DBSCAN_TO_UPDATE.DBSCAN_ID);
        public static final UniqueKey<DimensionsRecord> DIMENSIONS_PKEY = Internal.createUniqueKey(Dimensions.DIMENSIONS, "dimensions_pkey", Dimensions.DIMENSIONS.ORDINAL);
        public static final UniqueKey<DimensionsRecord> DIMENSIONS_NAME_KEY = Internal.createUniqueKey(Dimensions.DIMENSIONS, "dimensions_name_key", Dimensions.DIMENSIONS.NAME);
        public static final UniqueKey<HitsRecord> HITS_PKEY_2 = Internal.createUniqueKey(Hits.HITS, "hits_pkey_2", Hits.HITS.ID);
        public static final UniqueKey<LastByServerRecord> LAST_BY_SERVER_PKEY = Internal.createUniqueKey(LastByServer.LAST_BY_SERVER, "last_by_server_pkey", LastByServer.LAST_BY_SERVER.SERVER_ID);
        public static final UniqueKey<NotesRecord> NOTES_SERVER_ID_DIMENSION_X_Z_KEY = Internal.createUniqueKey(Notes.NOTES, "notes_server_id_dimension_x_z_key", Notes.NOTES.SERVER_ID, Notes.NOTES.DIMENSION, Notes.NOTES.X, Notes.NOTES.Z);
        public static final UniqueKey<PlayersRecord> PLAYERS_PKEY = Internal.createUniqueKey(Players.PLAYERS, "players_pkey", Players.PLAYERS.ID);
        public static final UniqueKey<PlayersRecord> PLAYERS_UUID_KEY = Internal.createUniqueKey(Players.PLAYERS, "players_uuid_key", Players.PLAYERS.UUID);
        public static final UniqueKey<ServersRecord> SERVERS_PKEY = Internal.createUniqueKey(Servers.SERVERS, "servers_pkey", Servers.SERVERS.ID);
        public static final UniqueKey<ServersRecord> SERVERS_HOSTNAME_KEY = Internal.createUniqueKey(Servers.SERVERS, "servers_hostname_key", Servers.SERVERS.HOSTNAME);
        public static final UniqueKey<SpatialRefSysRecord> SPATIAL_REF_SYS_PKEY = Internal.createUniqueKey(SpatialRefSys.SPATIAL_REF_SYS, "spatial_ref_sys_pkey", SpatialRefSys.SPATIAL_REF_SYS.SRID);
        public static final UniqueKey<StatusesRecord> STATUSES_PLAYER_ID_SERVER_ID_KEY = Internal.createUniqueKey(Statuses.STATUSES, "statuses_player_id_server_id_key", Statuses.STATUSES.PLAYER_ID, Statuses.STATUSES.SERVER_ID);
        public static final UniqueKey<TracksRecord> TRACKS_PKEY = Internal.createUniqueKey(Tracks.TRACKS, "tracks_pkey", Tracks.TRACKS.ID);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<AssociationsRecord, DbscanRecord> ASSOCIATIONS__ASSOCIATIONS_CLUSTER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.DBSCAN_PKEY, Associations.ASSOCIATIONS, "associations__associations_cluster_id_fkey", Associations.ASSOCIATIONS.CLUSTER_ID);
        public static final ForeignKey<AssociationsRecord, PlayersRecord> ASSOCIATIONS__ASSOCIATIONS_PLAYER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.PLAYERS_PKEY, Associations.ASSOCIATIONS, "associations__associations_player_id_fkey", Associations.ASSOCIATIONS.PLAYER_ID);
        public static final ForeignKey<BlocksRecord, DimensionsRecord> BLOCKS__BLOCKS_DIMENSION_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.DIMENSIONS_PKEY, Blocks.BLOCKS, "blocks__blocks_dimension_fkey", Blocks.BLOCKS.DIMENSION);
        public static final ForeignKey<BlocksRecord, ServersRecord> BLOCKS__BLOCKS_SERVER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.SERVERS_PKEY, Blocks.BLOCKS, "blocks__blocks_server_id_fkey", Blocks.BLOCKS.SERVER_ID);
        public static final ForeignKey<ChatRecord, PlayersRecord> CHAT__CHAT_REPORTED_BY_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.PLAYERS_PKEY, Chat.CHAT, "chat__chat_reported_by_fkey", Chat.CHAT.REPORTED_BY);
        public static final ForeignKey<ChatRecord, ServersRecord> CHAT__CHAT_SERVER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.SERVERS_PKEY, Chat.CHAT, "chat__chat_server_id_fkey", Chat.CHAT.SERVER_ID);
        public static final ForeignKey<DbscanRecord, DimensionsRecord> DBSCAN__DBSCAN_DIMENSION_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.DIMENSIONS_PKEY, Dbscan.DBSCAN, "dbscan__dbscan_dimension_fkey", Dbscan.DBSCAN.DIMENSION);
        public static final ForeignKey<DbscanRecord, ServersRecord> DBSCAN__DBSCAN_SERVER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.SERVERS_PKEY, Dbscan.DBSCAN, "dbscan__dbscan_server_id_fkey", Dbscan.DBSCAN.SERVER_ID);
        public static final ForeignKey<DbscanRecord, DbscanRecord> DBSCAN__DBSCAN_CLUSTER_PARENT_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.DBSCAN_PKEY, Dbscan.DBSCAN, "dbscan__dbscan_cluster_parent_fkey", Dbscan.DBSCAN.CLUSTER_PARENT);
        public static final ForeignKey<DbscanToUpdateRecord, DbscanRecord> DBSCAN_TO_UPDATE__DBSCAN_TO_UPDATE_DBSCAN_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.DBSCAN_PKEY, DbscanToUpdate.DBSCAN_TO_UPDATE, "dbscan_to_update__dbscan_to_update_dbscan_id_fkey", DbscanToUpdate.DBSCAN_TO_UPDATE.DBSCAN_ID);
        public static final ForeignKey<HitsRecord, ServersRecord> HITS__HITS_SERVER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.SERVERS_PKEY, Hits.HITS, "hits__hits_server_id_fkey", Hits.HITS.SERVER_ID);
        public static final ForeignKey<HitsRecord, DimensionsRecord> HITS__HITS_DIMENSION_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.DIMENSIONS_PKEY, Hits.HITS, "hits__hits_dimension_fkey", Hits.HITS.DIMENSION);
        public static final ForeignKey<HitsRecord, TracksRecord> HITS__HITS_TRACK_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.TRACKS_PKEY, Hits.HITS, "hits__hits_track_id_fkey", Hits.HITS.TRACK_ID);
        public static final ForeignKey<LastByServerRecord, ServersRecord> LAST_BY_SERVER__LAST_BY_SERVER_SERVER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.SERVERS_PKEY, LastByServer.LAST_BY_SERVER, "last_by_server__last_by_server_server_id_fkey", LastByServer.LAST_BY_SERVER.SERVER_ID);
        public static final ForeignKey<NotesRecord, DimensionsRecord> NOTES__NOTES_DIMENSION_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.DIMENSIONS_PKEY, Notes.NOTES, "notes__notes_dimension_fkey", Notes.NOTES.DIMENSION);
        public static final ForeignKey<NotesRecord, ServersRecord> NOTES__NOTES_SERVER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.SERVERS_PKEY, Notes.NOTES, "notes__notes_server_id_fkey", Notes.NOTES.SERVER_ID);
        public static final ForeignKey<PlayerSessionsRecord, PlayersRecord> PLAYER_SESSIONS__PLAYER_SESSIONS_PLAYER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.PLAYERS_PKEY, PlayerSessions.PLAYER_SESSIONS, "player_sessions__player_sessions_player_id_fkey", PlayerSessions.PLAYER_SESSIONS.PLAYER_ID);
        public static final ForeignKey<PlayerSessionsRecord, ServersRecord> PLAYER_SESSIONS__PLAYER_SESSIONS_SERVER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.SERVERS_PKEY, PlayerSessions.PLAYER_SESSIONS, "player_sessions__player_sessions_server_id_fkey", PlayerSessions.PLAYER_SESSIONS.SERVER_ID);
        public static final ForeignKey<SignsRecord, DimensionsRecord> SIGNS__SIGNS_DIMENSION_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.DIMENSIONS_PKEY, Signs.SIGNS, "signs__signs_dimension_fkey", Signs.SIGNS.DIMENSION);
        public static final ForeignKey<SignsRecord, ServersRecord> SIGNS__SIGNS_SERVER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.SERVERS_PKEY, Signs.SIGNS, "signs__signs_server_id_fkey", Signs.SIGNS.SERVER_ID);
        public static final ForeignKey<StatusesRecord, PlayersRecord> STATUSES__STATUSES_PLAYER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.PLAYERS_PKEY, Statuses.STATUSES, "statuses__statuses_player_id_fkey", Statuses.STATUSES.PLAYER_ID);
        public static final ForeignKey<StatusesRecord, ServersRecord> STATUSES__STATUSES_SERVER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.SERVERS_PKEY, Statuses.STATUSES, "statuses__statuses_server_id_fkey", Statuses.STATUSES.SERVER_ID);
        public static final ForeignKey<StatusesRecord, DimensionsRecord> STATUSES__STATUSES_DIMENSION_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.DIMENSIONS_PKEY, Statuses.STATUSES, "statuses__statuses_dimension_fkey", Statuses.STATUSES.DIMENSION);
        public static final ForeignKey<TracksRecord, HitsRecord> TRACKS__TRACKS_FIRST_HIT_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.HITS_PKEY_2, Tracks.TRACKS, "tracks__tracks_first_hit_id_fkey", Tracks.TRACKS.FIRST_HIT_ID);
        public static final ForeignKey<TracksRecord, HitsRecord> TRACKS__TRACKS_LAST_HIT_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.HITS_PKEY_2, Tracks.TRACKS, "tracks__tracks_last_hit_id_fkey", Tracks.TRACKS.LAST_HIT_ID);
        public static final ForeignKey<TracksRecord, DimensionsRecord> TRACKS__TRACKS_DIMENSION_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.DIMENSIONS_PKEY, Tracks.TRACKS, "tracks__tracks_dimension_fkey", Tracks.TRACKS.DIMENSION);
        public static final ForeignKey<TracksRecord, ServersRecord> TRACKS__TRACKS_SERVER_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.SERVERS_PKEY, Tracks.TRACKS, "tracks__tracks_server_id_fkey", Tracks.TRACKS.SERVER_ID);
        public static final ForeignKey<TracksRecord, TracksRecord> TRACKS__TRACKS_PREV_TRACK_ID_FKEY = Internal.createForeignKey(com.matt.nocom.server.postgres.codegen.Keys.TRACKS_PKEY, Tracks.TRACKS, "tracks__tracks_prev_track_id_fkey", Tracks.TRACKS.PREV_TRACK_ID);
    }
}
