SELECT ACC.COLUMN_NAME
FROM ALL_CONSTRAINTS AC, ALL_CONS_COLUMNS ACC
WHERE AC.OWNER = ? 
	AND AC.TABLE_NAME = ?
	AND AC.CON_TYPE = 'PRIMARY KEY'
	AND AC.CONSTRAINT_NAME = ACC.CONSTRAINT_NAME