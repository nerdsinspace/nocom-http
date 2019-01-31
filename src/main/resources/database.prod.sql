DELETE FROM Auth_Users WHERE id IN (
  SELECT a.id FROM Auth_USERS a
    INNER JOIN Auth_User_Groups b ON (a.id = b.user_id)
    INNER JOIN Auth_Groups c ON (b.group_id = c.id)
    WHERE c.name = 'DEBUG'
);