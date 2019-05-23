CREATE TABLE IF NOT EXISTS `up6_folders` (
  `f_id` 				char(32) NOT NULL ,
  `f_nameLoc` 			varchar(255) default '',
  `f_pid` 				char(32) default '',
  `f_uid` 				int(11) default '0',
  `f_lenLoc` 			bigint(19) default '0',
  `f_sizeLoc` 			varchar(50) default '0',
  `f_pathLoc` 			varchar(255) default '',
  `f_pathSvr` 			varchar(255) default '',
  `f_pathRel` 			varchar(255) default '',
  `f_folders` 			int(11) default '0',
  `f_fileCount` 		int(11) default '0',
  `f_filesComplete` 	int(11) default '0',
  `f_complete` 			tinyint(1) default '0',
  `f_deleted` 			tinyint(1) default '0',
  `f_time` 				timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `f_pidRoot` 			char(32) default '',
  PRIMARY KEY  (`f_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;