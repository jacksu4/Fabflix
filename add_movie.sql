DROP PROCEDURE IF EXISTS add_movie;

DELIMITER //
CREATE PROCEDURE add_movie
(IN title varchar(100),
IN year int,
IN director varchar(100),
IN star_name VARCHAR(100),
IN genre_name VARCHAR(32),
OUT responseMsg VARCHAR(200)
)
BEGIN
	DECLARE Exist INT DEFAULT 0;
	DECLARE star_id VARCHAR(10) DEFAULT 0 ;
	DECLARE genre_id INT DEFAULT 0 ;
	
    SELECT count(*)
	INTO Exist
	FROM movies 
	WHERE  movies.title = title AND movies.year = year AND movies.director = director;

	proc_label:BEGIN
		IF Exist > 0 THEN
			SET @responseMsg = 'Movie already exists.';
		ELSE
			SELECT MAX(id) INTO @movie_id_com FROM movies;
            SET @movie_id_com = cast(substring(@movie_id_com, 3) as unsigned)+1;
            SET @movie_id_new = concat("tt", @movie_id_com);
			INSERT INTO movies (id, title, year, director) 
			VALUES (@movie_id_new, title, year, director);
            SET @responseMsg = 'Movie added. ';
            SET @responseMsg = concat(@responseMsg , @movie_id_new) ;
            
			SELECT stars.id, count(*) 
			INTO star_id, Exist 
			FROM stars 
			WHERE stars.name = star_name 
            GROUP BY stars.id LIMIT 1;

			IF Exist = 0 THEN
				SELECT MAX(id) INTO @star_id_com FROM stars;
				SET @star_id_com = cast(substring(@star_id_com, 3) as unsigned)+1;
				SET @star_id_new = concat("nm", @star_id_com);
				INSERT INTO stars (id, name) VALUES (@star_id_new, star_name);
				INSERT INTO stars_in_movies (starId, movieId) VALUES (@star_id_new, @movie_id_new);
				SET @responseMsg = concat(@responseMsg , 'Star was not found and was created. ') ;
				SET @responseMsg = concat(@responseMsg , @star_id_new) ;
			ELSE
				INSERT INTO stars_in_movies(starId, movieId) VALUES (star_id, @movie_id_new);
                SET @responseMsg = concat(@responseMsg , 'Star was found and was linked to the movie. ') ;
			END IF;
        
			SELECT count(*) 
			INTO Exist 
			FROM genres 
			WHERE genres.name = genre_name;

			IF Exist = 0 THEN
				INSERT INTO genres (name) VALUES (genre_name);
				SET @genre_id_new = LAST_INSERT_ID();
				INSERT INTO genres_in_movies (genreId, movieId) VALUES (@genre_id_new, @movie_id_new);
				SET @responseMsg = concat(@responseMsg , 'Genre was not found and was created. ') ;
                SET @responseMsg = concat(@responseMsg , @genre_id_new) ;
			ELSE
				SELECT genres.id INTO genre_id FROM genres WHERE genres.name = genre_name;
				INSERT INTO genres_in_movies (genreId, movieId) VALUES (genre_id, @movie_id_new);
                SET @responseMsg = concat(@responseMsg , 'Genre was found and was linked to the movie. ') ;
			END IF;

		END IF;
	END;
    SET responseMsg = @responseMsg;
END //
DELIMITER ;
