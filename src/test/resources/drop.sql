/*
Linked tables must be dropped in reverse order as of creation
*/
DROP TABLE IF EXISTS progress;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS taskStatuses;
DROP TABLE IF EXISTS users;


DROP SEQUENCE IF EXISTS HIBERNATE_SEQUENCE;
