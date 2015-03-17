USE `proai`;

CREATE TABLE IF NOT EXISTS `provider_metadataformat` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `metadataPrefix` varchar(1024) DEFAULT NULL,
  `schemaTxt` varchar(1024) DEFAULT NULL,
  `metadataNamespace` varchar(1024) DEFAULT NULL,
  `lastmodified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `provider_identity` (
	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
	`xmlEntry` text, 
	`lastmodified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8; 

CREATE TABLE IF NOT EXISTS `provider_sets` (
	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
	`spec` varchar(1024), 
	`xmlEntry` text, 
	`lastmodified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `id_UNIQUE` (`id` ASC),
	INDEX `specidx` (`spec` ASC)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;  


CREATE TABLE IF NOT EXISTS provider_records (
	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
	`metadataPrefix` varchar(1024), 
	`source` text, 
	`recordId` varchar(1024), 
	`xmlEntry` text, 
	`lastmodified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `id_UNIQUE` (`id` ASC),
    INDEX `recordIdIdx` (`recordId` ASC)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;  
