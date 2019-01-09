CREATE KEYSPACE IF NOT EXISTS MmoBots
  WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

USE MmoBots;

CONSISTENCY ONE;

CREATE TABLE IF NOT EXISTS Places (
  Id varchar PRIMARY KEY,
  PosX int,
  PosY int,
  Type varchar,
  Gold int
);

CREATE TABLE IF NOT EXISTS Locks (
    BotId varchar,
    Place varchar,
    Time timestamp,
    Type varchar,

    PRIMARY KEY (Place, BotId, Time)
);

CREATE TABLE IF NOT EXISTS Logs (
  BotId varchar,
  Start timestamp,
  End timestamp,
  Place varchar,
  Gold int,

  PRIMARY KEY (BotId, Start, End)
);

CREATE TABLE IF NOT EXISTS Requests (
  BotId varchar PRIMARY KEY,
  Requests counter
);

CREATE TABLE IF NOT EXISTS Partitions (
  Time timestamp PRIMARY KEY,
  Description varchar
);
