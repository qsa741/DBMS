SELECT ACC.CONSTRAINT_NAME, AC.CON_TYPE, ACC.COLUMN_NAME, ACC.POSITION, AC.DELETE_RULE, AC.R_CONSTRAINT_NAME, AC.SEARCH_CONDITION, AC.R_OWNER
FROM ALL_CONS_COLUMNS ACC, ALL_CONSTRAINTS AC
WHERE ACC.OWNER = ?
	AND ACC.TABLE_NAME = ?
	AND ACC.CONSTRAINT_NAME = AC.CONSTRAINT_NAME
ORDER BY ACC.CONSTRAINT_NAME