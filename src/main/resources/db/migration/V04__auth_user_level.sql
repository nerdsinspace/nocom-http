-- update Auth_Users

ALTER TABLE Auth_Users
    ADD level integer DEFAULT 0;

ALTER TABLE Auth_Users
    ADD is_debug integer DEFAULT 0;

ALTER TABLE Auth_Users
    ADD last_login bigint DEFAULT 0;

-- [jooq ignore start]
DELETE FROM Auth_Users
WHERE id IN (
    SELECT user_id
    FROM Auth_User_Groups LEFT JOIN Auth_Groups ON (Auth_User_Groups.group_id = Auth_Groups.id)
    WHERE (Auth_User_Groups.id = Auth_Users.id)
      AND (Auth_Groups.name = 'DEBUG')
);

UPDATE Auth_Users SET level = 90
WHERE EXISTS(
    SELECT * FROM Auth_User_Groups
        LEFT JOIN Auth_Groups ON (Auth_User_Groups.group_id = Auth_Groups.id)
    WHERE (Auth_User_Groups.id = Auth_Users.id)
      AND (Auth_Groups.name = 'ADMIN')
);

UPDATE Auth_Users SET level = 50
WHERE EXISTS(
    SELECT * FROM Auth_User_Groups
        LEFT JOIN Auth_Groups ON (Auth_User_Groups.group_id = Auth_Groups.id)
    WHERE (Auth_User_Groups.id = Auth_Users.id)
      AND (Auth_Groups.name = 'DEV')
);
-- [jooq ignore stop]

DROP TABLE Auth_Groups;
DROP TABLE Auth_User_Groups;
