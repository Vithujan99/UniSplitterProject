create table gruppe(
  id serial primary key,
  gruppen_name varchar(300),
  geschlossen boolean
);

create table mitglied(
    gruppe int references gruppe(id),
    gruppe_key int,
    github_name varchar(100),
    primary key (gruppe,github_name)
);

create table rechnung(
    uuid varchar(50) primary key,
    gruppe int references gruppe(id),
    gruppe_key int,
    rechnung_name varchar(100),
    bezahlt_von varchar(100),
    betrag double precision
);

create table mitglied_rechnung(
    gruppe int references gruppe(id),
    gruppe_key int,
    rechnung varchar(50),
    mitglied_name varchar,
    primary key (rechnung,mitglied_name)
)