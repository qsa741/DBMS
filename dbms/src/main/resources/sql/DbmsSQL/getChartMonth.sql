SELECT MONTH
FROM ( SELECT *
	FROM ACTIONDATA
	WHERE YEAR = ? )
GROUP BY MONTH
ORDER BY MONTH DESC