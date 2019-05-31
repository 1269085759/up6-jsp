
/*删除分页*/
DROP PROCEDURE IF EXISTS `sp_page`;

/*创建分页*/
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_page`(
	_fields VARCHAR ( 1000 ),
	_tables VARCHAR ( 100 ),
	_where VARCHAR ( 2000 ),
	_orderby VARCHAR ( 200 ),
	_pageindex INT,
	_pageSize INT 
	)
BEGIN
	
	SET @startRow = _pageSize * ( _pageIndex - 1 );
	
	SET @pageSize = _pageSize;
	
	SET @rowindex = 0;#行号
	
	SET @strsql = CONCAT(
		'select sql_calc_found_rows ',
		_fields,
		' from ',
		_tables,
		CASE
				IFNULL( _where, '' ) 
				WHEN '' THEN ''
				WHEN length(_where) < 1 THEN ''
				ELSE CONCAT( ' where ', _where ) 
			END,
		CASE
				IFNULL( _orderby, '' ) 
				WHEN '' THEN ''
				WHEN length(_orderby) < 1 THEN ''
				ELSE CONCAT( ' order by ', _orderby ) 
			END,
			' limit ',
			@startRow,
			',',
			@pageSize 
		);
	PREPARE strsql 
	FROM
		@strsql;
	EXECUTE strsql;
	DEALLOCATE PREPARE strsql;

END