SELECT USERNAME, USER_ID, ACCOUNT_STATUS, LOCK_DATE, EXPIRY_DATE, DEFAULT_TABLESPACE, CREATED
FROM DBA_USERS
WHERE USERNAME = ?