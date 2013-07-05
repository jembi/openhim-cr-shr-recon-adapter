use interoperability_layer;

create table property (
	id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
	name varchar(128) NOT NULL,
	value text,
	description text
) CHARSET=UTF8;

alter table property add index `name` (`name`);

insert into property (`name`, `value`, `description`) values ('openEMPI_lastIdentifierUpdateEvents_poll', null, 'The date time value of the last OpenEMPI identifierUpdateEvents poll. Formatted as yyyy-MM-dd''T''HH:mm:ssZ');
