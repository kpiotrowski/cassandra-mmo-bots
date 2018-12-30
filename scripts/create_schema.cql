CREATE KEYSPACE IF NOT EXISTS MmoBots
  WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

USE MmoBots;

CREATE TABLE Places (
  Id varchar PRIMARY KEY,
  PosX int,
  PosY int,
  Type varchar,
  Gold int
);

CREATE TABLE Logs (
  BotId varchar,
  Start timestamp,
  End timestamp,
  Place varchar,

  PRIMARY KEY (BotId, Start, End)
);

CREATE TABLE Requests (
  BotId varchar PRIMARY KEY,
  Requests counter
);
