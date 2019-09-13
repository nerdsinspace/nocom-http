-- add dimensions. this replaces the old code which inserted the dimensions when the application started

-- [jooq ignore start]
-- disable foreign keys to prevent anything tied to the dimension table from being dropped
PRAGMA foreign_keys = OFF;

DROP TABLE Dimensions;

CREATE TABLE Dimensions (
    id integer PRIMARY KEY
    ,ordinal integer NOT NULL UNIQUE
    ,name varchar
);

INSERT INTO Dimensions (id, ordinal, name)
    VALUES (1, -1, 'Nether'),
           (2, 0, 'Overworld'),
           (3, 1, 'End');

-- PRAGMA foreign_keys = ON;
-- foreign keys should be off by default
-- [jooq ignore stop]
