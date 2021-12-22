SELECT DBMS_LOB.SUBSTR( DBMS_METADATA.GET_DDL( 'TABLE',  TABLE_NAME, OWNER), DBMS_LOB.GETLENGTH( DBMS_METADATA.GET_DDL( 'TABLE', TABLE_NAME, OWNER)))
FROM ALL_TABLES
WHERE OWNER = ?
	AND TABLE_NAME = ?