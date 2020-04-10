package main.java;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet{

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    protected void doGet( HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = "select movies.id, movies.title, movies.year, movies.director, ratings.rating FROM movies, ratings \n" +
                    "where movies.id=ratings.movieId order by ratings.rating desc limit 20";

            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            while (rs.next()){ //need to be changed for proj1
                ArrayList<String> movie_genres = new ArrayList<>();
                ArrayList<String> movie_stars = new ArrayList<>();

                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_id = rs.getString("id");

                String genre_star_query = "select distinct genres.name as genres_name, stars.name as stars_name from stars, stars_in_movies, genres, genres_in_movies\n" +
                        "where genres_in_movies.movieId = ? and genres.id = genres_in_movies.genreId \n" +
                        "and stars_in_movies.movieId = ? and stars.id = stars_in_movies.starId limit 3";

                PreparedStatement genre_star_statement = dbcon.prepareStatement(genre_star_query);

                genre_star_statement.setString(1, movie_id);
                genre_star_statement.setString(2, movie_id);

                ResultSet genre_star_rs = genre_star_statement.executeQuery();

                while(genre_star_rs.next()){
                    movie_genres.add(genre_star_rs.getString("genres_name"));
                    movie_stars.add(genre_star_rs.getString("stars_name"));
                }
                genre_star_rs.close();

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genres_one", movie_genres.get(0));
                jsonObject.addProperty("movie_genres_two", movie_genres.get(1));
                jsonObject.addProperty("movie_genres_three", movie_genres.get(2));
                jsonObject.addProperty("movie_stars_one", movie_stars.get(0));
                jsonObject.addProperty("movie_stars_two", movie_stars.get(1));
                jsonObject.addProperty("movie_stars_three", movie_stars.get(2));

                jsonArray.add(jsonObject);
            }
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();
    }
}
