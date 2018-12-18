#CreateTableLocation
CREATE TABLE IF NOT EXISTS Location (
	id integer PRIMARY KEY AUTOINCREMENT,
	found_time integer,
	coord_id integer,
	server_id integer,
	dimension_id integer,
	UNIQUE (found_time, coord_id, server_id, dimension_id),
	FOREIGN KEY (coord_id) REFERENCES Coord (id) ON DELETE CASCADE ON UPDATE NO ACTION,
	FOREIGN KEY (server_id) REFERENCES Server (id) ON DELETE CASCADE ON UPDATE NO ACTION,
	FOREIGN KEY (dimension_id) REFERENCES Dimension (id) ON DELETE CASCADE ON UPDATE NO ACTION
);

#CreateTableServer
CREATE TABLE IF NOT EXISTS Server (
	id integer PRIMARY KEY AUTOINCREMENT,
	hostname text UNIQUE ON CONFLICT IGNORE
);

#CreateTableDimension
CREATE TABLE IF NOT EXISTS Dimension (
	id integer PRIMARY KEY AUTOINCREMENT,
	ordinal integer UNIQUE,
	name text
);

#CreateTablePlayers_Online
CREATE TABLE IF NOT EXISTS Players_Online (
	player_id integer,
	coord_id integer,
	PRIMARY KEY (player_id, coord_id),
	FOREIGN KEY (player_id) REFERENCES Player (id) ON DELETE CASCADE ON UPDATE NO ACTION,
	FOREIGN KEY (coord_id) REFERENCES Coord (id) ON DELETE CASCADE ON UPDATE NO ACTION
);

#CreateTablePlayer
CREATE TABLE IF NOT EXISTS Player (
	id integer PRIMARY KEY AUTOINCREMENT,
	uuid text UNIQUE
);

#CreateTableCoord
CREATE TABLE IF NOT EXISTS Coord (
  id integer PRIMARY KEY AUTOINCREMENT,
	x integer,
	z integer,
	UNIQUE (x, z) ON CONFLICT IGNORE
);

#InsertDimensions
INSERT OR IGNORE INTO Dimension (ordinal, name) VALUES (-1, 'Nether'), (0, 'Overworld'), (1, 'End');

