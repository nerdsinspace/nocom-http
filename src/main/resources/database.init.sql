CREATE TABLE IF NOT EXISTS Servers (
	id integer PRIMARY KEY AUTOINCREMENT,
	hostname text NOT NULL UNIQUE ON CONFLICT IGNORE
);

CREATE TABLE IF NOT EXISTS Dimensions (
	id integer PRIMARY KEY AUTOINCREMENT,
	ordinal integer NOT NULL UNIQUE ON CONFLICT IGNORE,
	name text
);

CREATE TABLE IF NOT EXISTS Players (
	id integer PRIMARY KEY AUTOINCREMENT,
	uuid text NOT NULL UNIQUE ON CONFLICT IGNORE
);

CREATE TABLE IF NOT EXISTS Positions (
  id integer PRIMARY KEY AUTOINCREMENT,
	x integer NOT NULL,
	z integer NOT NULL,
	UNIQUE (x, z) ON CONFLICT IGNORE
);

CREATE TABLE IF NOT EXISTS Players_Online (
  player_id integer NOT NULL,
  pos_id integer NOT NULL,
  UNIQUE (player_id, pos_id) ON CONFLICT IGNORE,
  PRIMARY KEY (player_id, pos_id),
  FOREIGN KEY (player_id) REFERENCES Players (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY (pos_id) REFERENCES Positions (id) ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE TABLE IF NOT EXISTS Locations (
  id integer PRIMARY KEY AUTOINCREMENT,
  found_time bigint NOT NULL,
  upload_time bigint NOT NULL,
  pos_id integer NOT NULL,
  server_id integer NOT NULL,
  dimension_id integer NOT NULL,
  UNIQUE (found_time, pos_id, server_id, dimension_id) ON CONFLICT IGNORE,
  FOREIGN KEY (pos_id) REFERENCES Positions (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY (server_id) REFERENCES Servers (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY (dimension_id) REFERENCES Dimensions (id) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT OR IGNORE INTO Dimensions (ordinal, name) VALUES (-1, 'Nether'), (0, 'Overworld'), (1, 'End');

INSERT OR IGNORE INTO Players (uuid) VALUES ('00000000-0000-0000-0000-000000000000');