SELECT COUNT(*)
FROM ALL_OBJECTS
WHERE OWNER = ?
	AND OBJECT_TYPE = ?