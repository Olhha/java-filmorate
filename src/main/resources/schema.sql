create table IF NOT EXISTS RATING_MPA
(
    MPA_ID      integer generated by default as identity (exhausted),
    NAME        varchar(50),
    DESCRIPTION varchar(255),
    constraint "MPA_pk"
    primary key (MPA_ID)
    );

create table IF NOT EXISTS GENRE
(
    GENRE_ID integer generated by default as identity (exhausted),
    NAME     varchar(200),
    constraint "GENRE_pk"
    primary key (GENRE_ID)
    );

create table IF NOT EXISTS FILMS
(
    FILM_ID      integer generated by default as identity (exhausted),
    MPA_ID       integer,
    GENRE_ID     integer,
    NAME         varchar(50),
    DESCRIPTION  varchar(200),
    RELEASE_DATE date,
    DURATION     integer,
    constraint "FILMS_pk"
    primary key (FILM_ID),
    constraint "FILMS_RATING_MPA__fk"
    foreign key (MPA_ID) references RATING_MPA (MPA_ID),
    constraint "FILMS_GENRE__fk"
    foreign key (GENRE_ID) references GENRE (GENRE_ID)
    );

create table IF NOT EXISTS USERS
(
    USER_ID  integer generated by default as identity (exhausted),
    EMAIL    varchar(50),
    LOGIN    varchar(50),
    NAME     varchar(50),
    BIRTHDAY date,
    constraint "USERS_pk"
    primary key (USER_ID)
    );

create table IF NOT EXISTS FRIENDSHIP
(
    USER_FROM_ID integer,
    USER_TO_ID   integer,
    STATUS       boolean,
    constraint "USER_FROM__fk"
    foreign key (USER_FROM_ID) references USERS (USER_ID),
    constraint "USER_TO__fk"
    foreign key (USER_TO_ID) references USERS (USER_ID)
    );

create table IF NOT EXISTS FILM_LIKES
(
    FILM_ID integer,
    USER_ID integer,
    constraint "USER_ID__fk"
    foreign key (USER_ID) references USERS (USER_ID),
    constraint "FILM_ID__fk"
    foreign key (FILM_ID) references FILMS (FILM_ID)

    );

DELETE
FROM FRIENDSHIP;
DELETE
FROM FILM_LIKES;
DELETE
FROM GENRE;
ALTER TABLE GENRE
    ALTER COLUMN GENRE_ID RESTART WITH 1;
DELETE
FROM USERS;
ALTER TABLE USERS
    ALTER COLUMN USER_ID RESTART WITH 1;
DELETE
FROM FILMS;
ALTER TABLE FILMS
    ALTER COLUMN FILM_ID RESTART WITH 1;
DELETE
FROM RATING_MPA;
ALTER TABLE RATING_MPA
    ALTER COLUMN MPA_ID RESTART WITH 1;