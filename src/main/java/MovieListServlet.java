package main.java;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.org.glassfish.external.statistics.annotations.Reset;


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
//            Statement statement = dbcon.createStatement();

//            String query = "select movies.id, movies.title, movies.year, movies.director, ratings.rating FROM movies, ratings \n" +
//                    "where movies.id=ratings.movieId order by ratings.rating desc limit 20";
            ResultSet rs;
            PreparedStatement statement;

//            System.out.println(request.getParameter("start"));
//            System.out.println(request.getParameter("start").equals("null"));
            if (!request.getParameter("search").equals("null") && !request.getParameter("search").isEmpty()){

                System.out.println("enter search");
                String title, director, star_name;
                if(request.getParameter("title").equals("null")){
                    title = "'%%'";
                }else{
                    title = "'%"+request.getParameter("title")+"%'";
                }
                if(request.getParameter("director").equals("null")){
                    director = "'%%'";
                }else{
                    director = "'%"+request.getParameter("director")+"%'";
                }
                if(request.getParameter("star_name").equals("null")){
                    star_name = "'%%'";
                }else{
                    star_name = "'%"+request.getParameter("star_name")+"%'";
                }
                System.out.println(title+director+star_name);
                if(!request.getParameter("year").isEmpty() && !request.getParameter("year").equals("null")){
                    String year = request.getParameter("year");
                    String query = "select movies.id, movies.title, movies.year, movies.director, ratings.rating From movies, ratings where movies.id in (select distinct(movies.id) as movie_id from movies, stars, stars_in_movies where stars.name like ? and stars.id = stars_in_movies.starId and movies.id = stars_in_movies.movieId) and movies.title like ? and movies.director like ? and movies.year=? and ratings.movieId=movies.id";
                    statement = dbcon.prepareStatement(query);
                    statement.setString(1,star_name);
                    statement.setString(2,title);
                    statement.setString(3,director);
                    statement.setString(4,year);
                }else{
                    String query = "select movies.id, movies.title, movies.year, movies.director, ratings.rating From movies, ratings where movies.id in (select distinct(movies.id) as movie_id from movies, stars, stars_in_movies where stars.name like ? and stars.id = stars_in_movies.starId and movies.id = stars_in_movies.movieId) and movies.title like ? and movies.director like ? and ratings.movieId=movies.id";
                    statement = dbcon.prepareStatement(query);
                    statement.setString(1,star_name);
                    statement.setString(2,title);
                    statement.setString(3,director);
                }
            }else if (!request.getParameter("start").equals("null") && !request.getParameter("start").isEmpty()){
                System.out.println("enter start");
                String start = request.getParameter("start");
                if (!start.equals("*")) {
                    String query = "select movies.id, movies.title, movies.year, movies.director, ratings.rating FROM movies, ratings \n" +
                            "where movies.id=ratings.movieId and movies.title like ?";

                    statement = dbcon.prepareStatement(query);

                    statement.setString(1, start + "%");
                } else {
                    String query = "select movies.id, movies.title, movies.year, movies.director, ratings.rating FROM movies, ratings \n" +
                            "where movies.id=ratings.movieId and movies.title REGEXP '^[^A-Za-z0-9]'";

                    statement = dbcon.prepareStatement(query);
                }

            }else if (!request.getParameter("genre").equals("null") && !request.getParameter("genre").isEmpty()){
                System.out.println("enter genre");
                String genre = request.getParameter("genre");
                String query = "select movies.id, movies.title, movies.year, movies.director, ratings.rating FROM movies, ratings, genres, genres_in_movies\n" +
                        "where movies.id=ratings.movieId and genres.name = ? and genres.id = genres_in_movies.genreId and genres_in_movies.movieId = movies.id";

                statement = dbcon.prepareStatement(query);

                statement.setString(1,genre);

            }else{
                System.out.println("enter default");
                String query = "select movies.id, movies.title, movies.year, movies.director, ratings.rating FROM movies, ratings \n" +
                        "where movies.id=ratings.movieId order by ratings.rating desc limit 20";

                statement = dbcon.prepareStatement(query);
            }

            rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()){ //need to be changed for proj1
                ArrayList<String> movie_genres = new ArrayList<>();
                ArrayList<String> movie_stars = new ArrayList<>();
                ArrayList<String> movie_stars_id = new ArrayList<>();

                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_id = rs.getString("id");
                String movie_rating = rs.getString("rating");

                String genre_query = "select genres.name as genres_name from genres, genres_in_movies " +//add first 3 genre
                        "where genres_in_movies.movieId = ? and genres.id = genres_in_movies.genreId ORDER BY genres_name limit 3";

                PreparedStatement genre_statement = dbcon.prepareStatement(genre_query);

                genre_statement.setString(1, movie_id);

                ResultSet genre_rs = genre_statement.executeQuery();

                while(genre_rs.next()){
                    movie_genres.add(genre_rs.getString("genres_name"));
                }

                String star_query = "select stars.name as stars_name, stars.id as stars_id from stars, stars_in_movies " +//need to change for proj2
                        "where stars_in_movies.movieId =? and stars.id = stars_in_movies.starId limit 3";

                PreparedStatement star_statement = dbcon.prepareStatement(star_query);

                star_statement.setString(1, movie_id);

                ResultSet star_rs = star_statement.executeQuery();

                while(star_rs.next()){
                    movie_stars.add(star_rs.getString("stars_name"));
                    movie_stars_id.add(star_rs.getString("stars_id"));
                }

                JsonArray movie_genres_JsonArray = new Gson().toJsonTree(movie_genres).getAsJsonArray();
                JsonArray movie_stars_JsonArray = new Gson().toJsonTree(movie_stars).getAsJsonArray();
                JsonArray movie_stars_id_JsonArray = new Gson().toJsonTree(movie_stars_id).getAsJsonArray();

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.add("movie_genres", movie_genres_JsonArray);
                jsonObject.add("movie_stars", movie_stars_JsonArray);
                jsonObject.add("movie_stars_id", movie_stars_id_JsonArray);

                jsonArray.add(jsonObject);

                genre_rs.close();
                star_rs.close();
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
