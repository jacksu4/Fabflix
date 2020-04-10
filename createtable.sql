drop database if exists moviedb;
create database if not exists moviedb;

create table movies (
	id varchar(10) not null,
	title varchar(100) not null,
	year integer not null,
	director varchar(100) not null,
	primary key(id));

create table stars (
	id varchar(10) not null,
	name varchar(100) not null,
	birthYear integer,
	primary key(id));

create table stars_in_movies (
	starId varchar(10) not null references star.id,
	movieId varchar(10) not null references movie.id);

create table genres (
	id integer not null AUTO_INCREMENT,
	name varchar(32) not null,
    primary key(id));

create table genres_in_movies (
	genreId integer not null references genres.id,
	movieId varchar(10) not null references movies.id);

CREATE TABLE customers (
    id INTEGER NOT NULL AUTO_INCREMENT,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    ccld VARCHAR(20) NOT NULL REFERENCES creditcards.id,
    address VARCHAR(200) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(20) NOT NULL,
    PRIMARY KEY (id)
);

create table sales (
	id integer not null AUTO_INCREMENT,
	customerId integer not null references customers.id,
	movieId varchar(10) references movies.id,
	saleDate date not null,
    primary key(id)
);

create table creditcards (
	id varchar(20) not null,
	firstName varchar(50) not null,
	lastName varchar(50) not null,
	expiration date not null,
	primary key(id)
);

create table ratings (
	movieId varchar(10) not null references movies.id,
	rating float not null,
	numVotes integer not null
);