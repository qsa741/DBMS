SELECT CONSTRAINT_NAME
FROM ( SELECT *
	FROM ALL_CONS_COLUMNS
	WHERE OWNER = ?
		AND TABLE_NAME = ? )
GROUP BY CONSTRAINT_NAME