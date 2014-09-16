-- Create tables SQL scripts for File download service (Tiedostopalvelu)
-- Compatible and tested with PostgreSQL 9.x

DROP TABLE IF EXISTS daily_download_statistic;
DROP TABLE IF EXISTS download_statistic;
DROP TABLE IF EXISTS open_data_order_files;
DROP TABLE IF EXISTS open_data_order;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS jcr_permission;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS access_token;
DROP TABLE IF EXISTS token_type;

CREATE TABLE customer
(
   id BIGSERIAL PRIMARY KEY,
   first_name VARCHAR(100),
   last_name VARCHAR(100),
   organisation VARCHAR(256),
   email VARCHAR(256) NOT NULL,
   language VARCHAR(3),
   created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE token_type
(
   id SMALLINT,
   type VARCHAR(25) NOT NULL
);

CREATE TABLE access_token 
(
   id BIGSERIAL PRIMARY KEY,
   token VARCHAR(100) NOT NULL,
   token_type SMALLINT,
   created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   expires TIMESTAMP DEFAULT NULL,
   UNIQUE(token)
);

CREATE TABLE open_data_order
(
   id BIGSERIAL PRIMARY KEY,
   customer_id BIGINT,
   access_token_id BIGINT,
   created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT open_data_order_customer_id_fkey FOREIGN KEY (customer_id)
   REFERENCES customer(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE SET NULL,
   CONSTRAINT open_data_order_access_token_id_fkey FOREIGN KEY (access_token_id)
   REFERENCES access_token(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE SET NULL
);

CREATE TABLE open_data_order_files
(
   id BIGSERIAL PRIMARY KEY,
   jcr_path VARCHAR(512) NOT NULL,
   open_data_order_id BIGINT NOT NULL,
   CONSTRAINT open_data_order_files_open_data_order_id_fkey FOREIGN KEY (open_data_order_id)
   REFERENCES open_data_order(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE account
(
   id SERIAL PRIMARY KEY,
   username VARCHAR(100) NOT NULL,
   is_internal BOOLEAN DEFAULT FALSE,
   created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   UNIQUE(username)
);

CREATE TABLE jcr_permission
(
   id SERIAL PRIMARY KEY,
   account_id INTEGER,
   jcr_path VARCHAR(512) NOT NULL,
   privileges VARCHAR(50) ARRAY NOT NULL,
   CONSTRAINT jcr_permission_account_id_fkey FOREIGN KEY (account_id)
   REFERENCES account(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE download_statistic
(
   remoteip VARCHAR(100),
   service VARCHAR(25),
   username VARCHAR(100),
   dataset VARCHAR(50),
   dataset_version VARCHAR(50),
   jcr_path VARCHAR(512),
   format VARCHAR(50),
   crs VARCHAR(50),
   bytes BIGINT,
   downloaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE daily_download_statistic
(
   day DATE NOT NULL,
   dataset VARCHAR(50),
   dataset_version VARCHAR(50),
   files_count BIGINT,
   bytes_total BIGINT
);   

-- Insert base data
INSERT INTO token_type(id,type) VALUES(1,'token');
INSERT INTO token_type(id,type) VALUES(2,'apikey');

-- Internal built-in accounts
INSERT INTO account(username,is_internal) VALUES('_system_',true);
INSERT INTO account(username,is_internal) VALUES('_opendata_',true);

-- Grant all JCR privileges to internal system account
INSERT INTO jcr_permission(account_id,jcr_path,privileges) VALUES(
 (SELECT id FROM account WHERE username = '_system_'),'/',
 ARRAY['read','add_node','set_property','remove','remove_child_nodes','index_workspace']);
 
CREATE INDEX access_token_token_token_type_idx ON access_token(token,token_type);
CREATE INDEX jcr_permission_account_id_idx ON jcr_permission(account_id);