#Init_CreateTableLocation
CREATE TABLE IF NOT EXISTS Locations (
	id integer PRIMARY KEY AUTOINCREMENT,
	found_time integer NOT NULL,
	upload_time integer NOT NULL,
	pos_id integer NOT NULL,
	server_id integer NOT NULL,
	dimension_id integer NOT NULL,
	UNIQUE (found_time, pos_id, server_id, dimension_id) ON CONFLICT IGNORE,
	FOREIGN KEY (pos_id) REFERENCES Positions (id) ON DELETE CASCADE ON UPDATE NO ACTION,
	FOREIGN KEY (server_id) REFERENCES Servers (id) ON DELETE CASCADE ON UPDATE NO ACTION,
	FOREIGN KEY (dimension_id) REFERENCES Dimensions (id) ON DELETE CASCADE ON UPDATE NO ACTION
);

#Init_CreateTableServer
CREATE TABLE IF NOT EXISTS Servers (
	id integer PRIMARY KEY AUTOINCREMENT,
	hostname text NOT NULL UNIQUE ON CONFLICT IGNORE
);

#Init_CreateTableDimension
CREATE TABLE IF NOT EXISTS Dimensions (
	id integer PRIMARY KEY AUTOINCREMENT,
	ordinal integer NOT NULL UNIQUE ON CONFLICT IGNORE,
	name text
);

#Init_CreateTablePlayers_Online
CREATE TABLE IF NOT EXISTS Players_Online (
	player_id integer NOT NULL,
	pos_id integer NOT NULL,
	UNIQUE (player_id, pos_id) ON CONFLICT IGNORE,
	PRIMARY KEY (player_id, pos_id),
	FOREIGN KEY (player_id) REFERENCES Players (id) ON DELETE CASCADE ON UPDATE NO ACTION,
	FOREIGN KEY (pos_id) REFERENCES Positions (id) ON DELETE CASCADE ON UPDATE NO ACTION
);

#Init_CreateTablePlayer
CREATE TABLE IF NOT EXISTS Players (
	id integer PRIMARY KEY AUTOINCREMENT,
	uuid text NOT NULL UNIQUE ON CONFLICT IGNORE
);

#Init_CreateTablePositions
CREATE TABLE IF NOT EXISTS Positions (
  id integer PRIMARY KEY AUTOINCREMENT,
	x integer NOT NULL,
	z integer NOT NULL,
	UNIQUE (x, z) ON CONFLICT IGNORE
);

#Init_InsertDimensions
INSERT OR IGNORE INTO Dimensions (ordinal, name) VALUES (-1, 'Nether'), (0, 'Overworld'), (1, 'End');

#Init_InsertUnknownPlayer
INSERT OR IGNORE INTO Players (uuid) VALUES ('00000000-0000-0000-0000-000000000000');

#InsertLocation
INSERT INTO Locations
  (
    found_time,
    upload_time,
    pos_id,
    server_id,
    dimension_id
  )
  VALUES
  (
    ?,
    ?,
    (SELECT id FROM Positions WHERE x=? AND z=?),
    (SELECT id FROM Servers WHERE hostname=?),
    (SELECT id FROM Dimensions WHERE ordinal=?)
  );

#InsertPlayer
INSERT INTO Players (uuid) VALUES (?);

#InsertPosition
INSERT INTO Positions (x, z) VALUES (?, ?);

#InsertServer
INSERT INTO Servers (hostname) VALUES (?);

#SelectAllLocations
SELECT
  Locations.found_time AS foundTime,
  Locations.upload_time AS uploadTime,
  Servers.hostname AS server,
  Positions.x AS x,
  Positions.z AS z,
  Dimensions.ordinal AS dimension
FROM Locations
  INNER JOIN Servers ON Servers.id=Locations.server_id
  INNER JOIN Positions ON Positions.id=Locations.pos_id
  INNER JOIN Dimensions ON Dimensions.id=Locations.dimension_id;

#SelectLocationsWithFilter
SELECT
  Locations.found_time AS foundTime,
  Locations.upload_time AS uploadTime,
  Servers.hostname AS server,
  Positions.x AS x,
  Positions.z AS z,
  Dimensions.ordinal AS dimension
FROM Locations
  INNER JOIN Servers ON Servers.id=Locations.server_id
  INNER JOIN Positions ON Positions.id=Locations.pos_id
  INNER JOIN Dimensions ON Dimensions.id=Locations.dimension_id
WHERE
  Servers.hostname = ?
AND
  Locations.found_time BETWEEN ? AND ?
AND
  Dimensions.ordinal = ?;

