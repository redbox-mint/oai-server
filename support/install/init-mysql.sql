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
  
INSERT INTO provider_identity (xmlEntry) VALUES ('<Identify>    <repositoryName>ReDBox</repositoryName>    <baseURL>http://demo.redboxresearchdata.com.au/redbox/default</baseURL>    <protocolVersion>2.0</protocolVersion>    <adminEmail>support@redboxresearchdata.com.au</adminEmail>      <earliestDatestamp>0001-01-01T00:00:00Z</earliestDatestamp>    <deletedRecord>persistent</deletedRecord>    <granularity>YYYY-MM-DDThh:mm:ssZ</granularity>    <description>        <oai-identifier xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai-identifier http://www.openarchives.org/OAI/2.0/oai-identifier.xsd" xmlns="http://www.openarchives.org/OAI/2.0/oai-identifier">            <scheme>oai</scheme>            <repositoryIdentifier>fascinator.usq.edu.au</repositoryIdentifier>            <delimiter>:</delimiter>            <sampleIdentifier>oai:fascinator.usq.edu.au:5e8ff9bf55ba3508199d22e984129be6</sampleIdentifier>        </oai-identifier>    </description></Identify>');
INSERT INTO provider_sets (spec, xmlEntry) VALUES ('Parties_People', '<set><setSpec>Parties_People</setSpec><setName>Parties - People</setName></set>');
INSERT INTO provider_sets (spec, xmlEntry) VALUES ('Parties_Groups', '<set><setSpec>Parties_Groups</setSpec><setName>Parties - Groups</setName></set>');
INSERT INTO provider_sets (spec, xmlEntry) VALUES ('Services', '<set><setSpec>Services</setSpec><setName>Services</setName></set>');
INSERT INTO provider_sets (spec, xmlEntry) VALUES ('Dataset', '<set><setSpec>Dataset</setSpec><setName>Dataset</setName></set>');
INSERT INTO provider_metadataformat (metadataPrefix, schemaTxt, metadataNamespace) VALUES ('eac-cpf', 'urn:isbn:1-931666-33-4 http://eac.staatsbibliothek-berlin.de/schema/cpf.xsd', 'urn:isbn:1-931666-33-4');
INSERT INTO provider_metadataformat (metadataPrefix, schemaTxt, metadataNamespace) VALUES ('oai_dc', 'http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd', 'http://purl.org/dc/elements/1.1/');
INSERT INTO provider_metadataformat (metadataPrefix, schemaTxt, metadataNamespace) VALUES ('rif', 'http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/home/orca/schemata/registryObjects.xsd', 'http://ands.org.au/standards/rif-cs/registryObjects');