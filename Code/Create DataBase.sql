-- This syntax is for MYSQL
--DROP DATABASE Engine;
CREATE DATABASE Engine;


CREATE TABLE ToBeCrawled (
    URL	VARCHAR(765) CHARACTER SET utf8 COLLATE utf8_general_ci NOT Null UNIQUE
);

CREATE TABLE Crawled (
    URL		VARCHAR(765) CHARACTER SET utf8 COLLATE utf8_general_ci NOT Null UNIQUE,
	Title	VARCHAR(765) CHARACTER SET utf8 COLLATE utf8_general_ci Not NUll,
	
	FOREIGN KEY (URL) REFERENCES ToBeCrawled(URL)
);

CREATE TABLE Indexed (
    Word	VARCHAR(765) CHARACTER SET utf8 COLLATE utf8_general_ci	NOT Null,
    URL		VARCHAR(765) CHARACTER SET utf8 COLLATE utf8_general_ci	NOT Null,
    TF		Float(10)		Not Null,
	Content VARCHAR(765) CHARACTER SET utf8 COLLATE utf8_general_ci    Not Null,

	UNIQUE(Word,URL),
	
	FOREIGN KEY (URL) REFERENCES Crawled(URL)
);

CREATE TABLE BlockedURLS (
    URL		VARCHAR(765) CHARACTER SET utf8 COLLATE utf8_general_ci	NOT Null UNIQUE
);


CREATE TABLE DoneRobot (
    URL		VARCHAR(765) CHARACTER SET utf8 COLLATE utf8_general_ci	NOT Null UNIQUE
);
