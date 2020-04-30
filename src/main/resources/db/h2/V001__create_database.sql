CREATE TABLE Auth_Users
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
    username        VARCHAR_IGNORECASE NOT NULL UNIQUE,
    password        BINARY(60)         NOT NULL,
    level           INTEGER            NOT NULL DEFAULT 0,
    enabled         BOOLEAN            NOT NULL DEFAULT FALSE,
    debug           BOOLEAN            NOT NULL DEFAULT FALSE,
    login_timestamp TIMESTAMP                   DEFAULT NULL,
    CHECK (LENGTH(username) < 16)
);

CREATE TABLE Auth_Token
(
    id           INTEGER PRIMARY KEY AUTO_INCREMENT,
    token        UUID        NOT NULL,
    address      BINARY(128) NOT NULL,
    created_time TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    lifespan     TIME        NOT NULL,
    user_id      INTEGER     NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Auth_Users (id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);

CREATE INDEX idx_uuid_Auth_Token ON Auth_Token (token);
CREATE INDEX idx_user_Auth_Token ON Auth_Token (user_id);
CREATE INDEX idx_user_time_Auth_Token ON Auth_Token (user_id, created_time);

CREATE TABLE JWT_SignKeyPair
(
    id          INTEGER PRIMARY KEY AUTO_INCREMENT,
    created_on  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    private_key VARBINARY NOT NULL,
    public_key  VARBINARY NOT NULL
);

CREATE INDEX idx_ctime_JWT_SignKeyPair ON JWT_SignKeyPair (created_on);

CREATE TABLE Event
(
    id           INTEGER PRIMARY KEY AUTO_INCREMENT,
    created_time TIMESTAMP          DEFAULT CURRENT_TIMESTAMP(),
    type         VARCHAR_IGNORECASE DEFAULT 'None',
    caused_by    VARCHAR_IGNORECASE DEFAULT 'Unknown',
    message      VARCHAR(255) NOT NULL,
    CHECK (LENGTH(type) < 32),
    CHECK (LENGTH(caused_by) < 32)
);

CREATE INDEX idx_time_Event ON Event (created_time);
