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

CREATE TABLE IF NOT EXISTS Auth_Users (
  id integer PRIMARY KEY AUTOINCREMENT,
  username text NOT NULL UNIQUE,
  password text NOT NULL,
  enabled integer NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS Auth_Groups (
  id integer PRIMARY KEY AUTOINCREMENT,
  name text NOT NULL UNIQUE,
  level integer default 666
);

CREATE TABLE IF NOT EXISTS Auth_User_Groups (
  id integer PRIMARY KEY AUTOINCREMENT,
  user_id integer NOT NULL,
  group_id integer NOT NULL,
  UNIQUE (user_id, group_id),
  FOREIGN KEY (user_id) REFERENCES Auth_Users (id) ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY (group_id) REFERENCES Auth_Groups (id) ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE TABLE IF NOT EXISTS Auth_Tokens (
  token text PRIMARY KEY,
  address text NOT NULL,
  expires_on bigint NOT NULL,
  user_id integer NOT NULL,
  FOREIGN KEY (user_id) REFERENCES Auth_Users (id) ON DELETE CASCADE ON UPDATE NO ACTION
);