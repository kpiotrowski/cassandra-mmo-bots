CREATE KEYSPACE IF NOT EXISTS MmoBots
  WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

USE MmoBots;

CREATE TABLE Bots (
  Id varchar,
  CollectedGold int,
  BackpackSpace int,
  PosX int,
  PosY int,
  State varchar,
  PRIMARY KEY(Id)
);

CREATE TABLE Resources (
  Id varchar,
  PosX int,
  PosY int,
  GoldLeft int,
  PRIMARY KEY(Id)
);

CREATE TABLE Cities (
  Id varchar,
  Name varchar,
  PosX int,
  PosY int,
  Gold int,
  PRIMARY KEY(Id)
);
