CREATE TABLE Servers (
	id integer PRIMARY KEY AUTOINCREMENT
	,hostname varchar NOT NULL UNIQUE
-- [jooq ignore start]
	    ON CONFLICT IGNORE
-- [jooq ignore stop]
);

CREATE TABLE Dimensions (
	id integer PRIMARY KEY AUTOINCREMENT
	,ordinal integer NOT NULL UNIQUE
-- [jooq ignore start]
	    ON CONFLICT IGNORE
-- [jooq ignore stop]
    ,name varchar
);

CREATE TABLE Players (
	id integer PRIMARY KEY AUTOINCREMENT
	,uuid varchar NOT NULL UNIQUE
-- [jooq ignore start]
	    ON CONFLICT IGNORE
-- [jooq ignore stop]
);

CREATE TABLE Positions (
  id integer PRIMARY KEY AUTOINCREMENT
	,x integer NOT NULL
	,z integer NOT NULL
	,UNIQUE (x, z)
-- [jooq ignore start]
	    ON CONFLICT IGNORE
-- [jooq ignore stop]
);

CREATE TABLE Players_Online (
  player_id integer NOT NULL
  ,pos_id integer NOT NULL
  ,UNIQUE (player_id, pos_id)
-- [jooq ignore start]
      ON CONFLICT IGNORE
-- [jooq ignore stop]
  ,PRIMARY KEY (player_id, pos_id)
  ,FOREIGN KEY (player_id) REFERENCES Players (id)
-- [jooq ignore start]
      ON DELETE CASCADE ON UPDATE NO ACTION
-- [jooq ignore stop]
  ,FOREIGN KEY (pos_id) REFERENCES Positions (id)
-- [jooq ignore start]
      ON DELETE CASCADE ON UPDATE NO ACTION
-- [jooq ignore stop]
);

CREATE TABLE Locations (
  id integer PRIMARY KEY AUTOINCREMENT
  ,found_time bigint NOT NULL
  ,upload_time bigint NOT NULL
  ,pos_id integer NOT NULL
  ,server_id integer NOT NULL
  ,dimension_id integer NOT NULL
  ,UNIQUE (found_time, pos_id, server_id, dimension_id)
-- [jooq ignore start]
    ON CONFLICT IGNORE
-- [jooq ignore stop]
  ,FOREIGN KEY (pos_id) REFERENCES Positions (id)
-- [jooq ignore start]
      ON DELETE CASCADE ON UPDATE NO ACTION
-- [jooq ignore stop]
  ,FOREIGN KEY (server_id) REFERENCES Servers (id)
-- [jooq ignore start]
      ON DELETE CASCADE ON UPDATE NO ACTION
-- [jooq ignore stop]
  ,FOREIGN KEY (dimension_id) REFERENCES Dimensions (id)
-- [jooq ignore start]
      ON DELETE CASCADE ON UPDATE NO ACTION
-- [jooq ignore stop]
);

CREATE TABLE Auth_Users (
  id integer PRIMARY KEY AUTOINCREMENT
  ,username varchar NOT NULL UNIQUE
  ,password varchar NOT NULL
  ,enabled integer NOT NULL DEFAULT 1
);

CREATE TABLE Auth_Groups (
  id integer PRIMARY KEY AUTOINCREMENT
  ,name varchar NOT NULL UNIQUE
  ,level integer default 666
);

CREATE TABLE Auth_User_Groups (
  id integer PRIMARY KEY AUTOINCREMENT
  ,user_id integer NOT NULL
  ,group_id integer NOT NULL
  ,UNIQUE (user_id, group_id)
  ,FOREIGN KEY (user_id) REFERENCES Auth_Users (id)
-- [jooq ignore start]
      ON DELETE CASCADE ON UPDATE NO ACTION
-- [jooq ignore stop]
  ,FOREIGN KEY (group_id) REFERENCES Auth_Groups (id)
-- [jooq ignore start]
      ON DELETE CASCADE ON UPDATE NO ACTION
-- [jooq ignore stop]
);

CREATE TABLE Auth_Tokens (
  token varchar PRIMARY KEY
  ,address varchar NOT NULL
  ,expires_on bigint NOT NULL
  ,user_id integer NOT NULL
  ,FOREIGN KEY (user_id) REFERENCES Auth_Users (id)
-- [jooq ignore start]
      ON DELETE CASCADE ON UPDATE NO ACTION
-- [jooq ignore stop]
);