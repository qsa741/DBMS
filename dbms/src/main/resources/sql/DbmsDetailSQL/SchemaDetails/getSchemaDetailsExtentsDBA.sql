SELECT SEGMENT_TYPE AS TABLESPACE, '' AS SEGMENT_NAME, SEGMENT_NAME AS OBJECT_NAME, FILE_ID, BLOCK_ID, BLOCKS 
FROM DBA_EXTENTS
WHERE OWNER = ? 
ORDER BY SEGMENT_NAME