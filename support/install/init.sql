DROP TRIGGER IF EXISTS provider_metadata_update_lastmodified ON provider_metadataformat; 
DROP TRIGGER IF EXISTS provider_identity_update_lastmodified ON provider_identity; 
DROP TRIGGER IF EXISTS provider_sets_update_lastmodified ON provider_sets;
DROP TRIGGER IF EXISTS provider_records_update_lastmodified ON provider_records;  
        
CREATE OR REPLACE FUNCTION update_lastmodified_column() 
        RETURNS TRIGGER AS ' 
  BEGIN 
    NEW.lastmodified = NOW(); 
    RETURN NEW; 
  END; 
' LANGUAGE 'plpgsql';

CREATE TABLE IF NOT EXISTS provider_metadataformat  (id SERIAL PRIMARY KEY, metadataPrefix varchar(1024), schemaTxt varchar(1024), metadataNamespace varchar(1024), lastmodified TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP); 
CREATE TRIGGER provider_metadata_update_lastmodified BEFORE UPDATE ON provider_metadataformat FOR EACH ROW EXECUTE PROCEDURE update_lastmodified_column(); 

CREATE TABLE IF NOT EXISTS provider_identity (id SERIAL PRIMARY KEY, xmlEntry text, lastmodified TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP); 
CREATE TRIGGER provider_identity_update_lastmodified BEFORE UPDATE ON provider_identity FOR EACH ROW EXECUTE PROCEDURE update_lastmodified_column(); 

CREATE TABLE IF NOT EXISTS provider_sets (id SERIAL PRIMARY KEY, spec varchar(1024), xmlEntry text, lastmodified TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP); 
CREATE TRIGGER provider_sets_update_lastmodified BEFORE UPDATE ON provider_sets FOR EACH ROW EXECUTE PROCEDURE update_lastmodified_column();
DO $$
BEGIN
IF NOT EXISTS (
    SELECT 1 FROM pg_class c JOIN   pg_namespace n ON n.oid = c.relnamespace WHERE  c.relname = 'specidx' AND n.nspname = 'public'
    ) THEN
    CREATE UNIQUE INDEX specIdx ON provider_sets (spec);
END IF;
END$$; 


CREATE TABLE IF NOT EXISTS provider_records (id SERIAL PRIMARY KEY, metadataPrefix varchar(1024), source text, recordId varchar(1024), xmlEntry text, lastmodified TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP); 
CREATE TRIGGER provider_records_update_lastmodified BEFORE UPDATE ON provider_records FOR EACH ROW EXECUTE PROCEDURE update_lastmodified_column();
DO $$
BEGIN
IF NOT EXISTS (
    SELECT 1 FROM pg_class c JOIN   pg_namespace n ON n.oid = c.relnamespace WHERE  c.relname = 'recordididx' AND n.nspname = 'public'
    ) THEN
    CREATE INDEX recordIdIdx ON provider_records (recordId);
END IF;
END$$; 

DO $$
BEGIN
IF NOT EXISTS (
    SELECT 1 FROM provider_identity
    ) THEN
    INSERT INTO provider_identity (xmlEntry) VALUES ('<Identify>    <repositoryName>ReDBox</repositoryName>    <baseURL>http://demo.redboxresearchdata.com.au/mint/default</baseURL>    <protocolVersion>2.0</protocolVersion>    <adminEmail>fascinator@usq.edu.au</adminEmail>      <earliestDatestamp>0001-01-01T00:00:00Z</earliestDatestamp>    <deletedRecord>persistent</deletedRecord>    <granularity>YYYY-MM-DDThh:mm:ssZ</granularity>    <description>        <oai-identifier xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai-identifier http://www.openarchives.org/OAI/2.0/oai-identifier.xsd" xmlns="http://www.openarchives.org/OAI/2.0/oai-identifier">            <scheme>oai</scheme>            <repositoryIdentifier>fascinator.usq.edu.au</repositoryIdentifier>            <delimiter>:</delimiter>            <sampleIdentifier>oai:fascinator.usq.edu.au:5e8ff9bf55ba3508199d22e984129be6</sampleIdentifier>        </oai-identifier>    </description></Identify>');
	INSERT INTO provider_sets (spec, xmlEntry) VALUES ('Parties_People', '<set><setSpec>Parties_People</setSpec><setName>Parties - People</setName></set>');
	INSERT INTO provider_sets (spec, xmlEntry) VALUES ('Parties_Groups', '<set><setSpec>Parties_Groups</setSpec><setName>Parties - Groups</setName></set>');
	INSERT INTO provider_sets (spec, xmlEntry) VALUES ('Services', '<set><setSpec>Services</setSpec><setName>Services</setName></set>');
	INSERT INTO provider_sets (spec, xmlEntry) VALUES ('Dataset', '<set><setSpec>Dataset</setSpec><setName>Dataset</setName></set>');
	INSERT INTO provider_metadataformat (metadataPrefix, schemaTxt, metadataNamespace) VALUES ('eac-cpf', 'urn:isbn:1-931666-33-4 http://eac.staatsbibliothek-berlin.de/schema/cpf.xsd', 'urn:isbn:1-931666-33-4');
	INSERT INTO provider_metadataformat (metadataPrefix, schemaTxt, metadataNamespace) VALUES ('oai_dc', 'http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd', 'http://purl.org/dc/elements/1.1/');
	INSERT INTO provider_metadataformat (metadataPrefix, schemaTxt, metadataNamespace) VALUES ('rif', 'http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/home/orca/schemata/registryObjects.xsd', 'http://ands.org.au/standards/rif-cs/registryObjects');
END IF;
END$$; 
