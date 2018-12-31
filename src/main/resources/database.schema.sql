CREATE TABLE Servers (
	id integer PRIMARY KEY AUTOINCREMENT,
	hostname varchar NOT NULL UNIQUE
);

CREATE TABLE Dimensions (
	id integer PRIMARY KEY AUTOINCREMENT,
	ordinal integer NOT NULL UNIQUE,
	name varchar
);

CREATE TABLE Players (
	id integer PRIMARY KEY AUTOINCREMENT,
	uuid varchar NOT NULL UNIQUE
);

CREATE TABLE Positions (
  id integer PRIMARY KEY AUTOINCREMENT,
	x integer NOT NULL,
	z integer NOT NULL,
	UNIQUE (x, z)
);

CREATE TABLE Players_Online (
	player_id integer NOT NULL,
	pos_id integer NOT NULL,
	UNIQUE (player_id, pos_id),
	PRIMARY KEY (player_id, pos_id),
	FOREIGN KEY (player_id) REFERENCES Players (id),
	FOREIGN KEY (pos_id) REFERENCES Positions (id)
);

CREATE TABLE Locations (
	id integer PRIMARY KEY AUTOINCREMENT,
	found_time bigint NOT NULL,
	upload_time bigint NOT NULL,
	pos_id integer NOT NULL,
	server_id integer NOT NULL,
	dimension_id integer NOT NULL,
	UNIQUE (found_time, pos_id, server_id, dimension_id),
	FOREIGN KEY (pos_id) REFERENCES Positions (id),
	FOREIGN KEY (server_id) REFERENCES Servers (id),
	FOREIGN KEY (dimension_id) REFERENCES Dimensions (id)
);

CREATE TABLE Auth_Users (
  id integer PRIMARY KEY AUTOINCREMENT,
  username varchar NOT NULL UNIQUE,
  password varchar NOT NULL,
  enabled integer NOT NULL DEFAULT 1
);

CREATE TABLE Auth_Groups (
  id integer PRIMARY KEY AUTOINCREMENT,
  name varchar NOT NULL UNIQUE,
  level integer NOT NULL
);

CREATE TABLE Auth_User_Groups (
  id integer PRIMARY KEY AUTOINCREMENT,
  user_id integer NOT NULL,
  group_id integer NOT NULL,
  UNIQUE (user_id, group_id),
  FOREIGN KEY (user_id) REFERENCES Auth_Users (id),
  FOREIGN KEY (group_id) REFERENCES Auth_Groups (id)
);