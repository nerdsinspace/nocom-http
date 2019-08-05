CREATE TABLE Event_Levels (
    id integer PRIMARY KEY,
    name varchar NOT NULL
);

-- [jooq ignore start]
INSERT INTO Event_Levels
    (id, name)
VALUES
    (0, 'Fatal'),
    (1, 'Error'),
    (2, 'Warn'),
    (3, 'Info'),
    (4, 'Debug'),
    (5, 'Trace');
-- [jooq ignore stop]

CREATE TABLE Event_Types (
    id integer PRIMARY KEY AUTOINCREMENT
    ,name varchar NOT NULL
    ,hash blob NOT NULL
);

CREATE TABLE Events (
    id integer PRIMARY KEY AUTOINCREMENT
    ,event_time bigint NOT NULL
    ,event_level integer DEFAULT 3
    ,event_type int NOT NULL
    ,user_id integer
    ,user_name varchar DEFAULT 'SYSTEM'
    ,message varchar NOT NULL
    ,FOREIGN KEY (user_id) REFERENCES Auth_Users (id)
-- [jooq ignore start]
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
-- [jooq ignore stop]
    ,FOREIGN KEY (event_level) REFERENCES Event_Levels (id)
-- [jooq ignore start]
        ON DELETE CASCADE
        ON UPDATE NO ACTION
-- [jooq ignore stop]
    ,FOREIGN KEY (event_type) REFERENCES Event_Types (id)
-- [jooq ignore start]
        ON DELETE CASCADE
        ON UPDATE NO ACTION
-- [jooq ignore stop]
);