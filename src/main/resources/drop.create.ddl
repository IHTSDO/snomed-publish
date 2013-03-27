DROP TABLE IF EXISTS 'RELATIONSHIP';

CREATE TABLE RELATIONSHIP(
	'relationshipId' BIGINT(10) NOT NULL,
	'conceptId1' BIGINT(10) NOT NULL,
	'relationshipType' BIGINT(10) NOT NULL,
	'conceptId2' BIGINT(10) NOT NULL,
	'characteristicType' INT(1) NOT NULL,
	'refinability' BOOLEAN,
	'relationshipGroup' INT(1)
);