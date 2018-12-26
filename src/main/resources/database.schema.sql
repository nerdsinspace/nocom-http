CREATE TABLE Servers (
	id int PRIMARY KEY AUTOINCREMENT,
	hostname varchar NOT NULL UNIQUE
);

CREATE TABLE Dimensions (
	id int PRIMARY KEY AUTOINCREMENT,
	ordinal integer NOT NULL UNIQUE,
	name varchar
);

CREATE TABLE Players (
	id int PRIMARY KEY AUTOINCREMENT,
	uuid varchar NOT NULL UNIQUE
);

CREATE TABLE Positions (
  id int PRIMARY KEY AUTOINCREMENT,
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
	id int PRIMARY KEY AUTOINCREMENT,
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

