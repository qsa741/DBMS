SELECT OBJECT_TYPE, OBJECT_NAME
FROM ALL_OBJECTS
WHERE OWNER = ? AND OBJECT_TYPE = ?