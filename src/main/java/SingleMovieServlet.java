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
import java.sql.*;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet{
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet( HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String id = request.getParameter("id");
        PrintWriter out = response.getWriter();
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            //title, year, director, rating

            String query = "select movies.title, movies.year, movies.director from movies where movies.id=?";

            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1,id);

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()){

                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id",id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);

                jsonArray.add(jsonObject);
            }

            //genres and stars
            String genre_query = "select genres.name from genres, genres_in_movies where genres_in_movies.movieId=? and genres_in_movies.genreId=genres.id order by genres.name asc";
            PreparedStatement genre_statement = dbcon.prepareStatement(genre_query);
            genre_statement.setString(1,id);
            ResultSet rs_genre = genre_statement.executeQuery();
            ArrayList<String> genres = new ArrayList<>();

            while (rs_genre.next()){
                genres.add(rs_genre.getString("name"));
            }

            String star_query = "select stars.name, stars.id from stars, stars_in_movies use index(idx_stars_in_movies)" +
            "where stars_in_movies.movieId = ? and stars.id = stars_in_movies.starId " +
            "order by (select count(stars_in_movies.movieId) from stars_in_movies where stars_in_movies.starId=stars.id) desc, stars.name asc";

            PreparedStatement star_statement = dbcon.prepareStatement(star_query);
            star_statement.setString(1,id);
            ResultSet rs_star = star_statement.executeQuery();
            ArrayList<String> stars_id = new ArrayList<>();
            ArrayList<String> stars_name = new ArrayList<>();

            while (rs_star.next()){
                stars_id.add(rs_star.getString("id"));
                stars_name.add(rs_star.getString("name"));
            }

            JsonArray genres_JsonArray = new Gson().toJsonTree(genres).getAsJsonArray();
            JsonArray stars_id_JsonArray = new Gson().toJsonTree(stars_id).getAsJsonArray();
            JsonArray stars_name_JsonArray = new Gson().toJsonTree(stars_name).getAsJsonArray();

            JsonObject jo = new JsonObject();
            jo.add("genres", genres_JsonArray);
            jo.add("stars_id", stars_id_JsonArray);
            jo.add("stars_name", stars_name_JsonArray);

            jsonArray.add(jo);

            //ratings
            String rating_query = "select ratings.rating from ratings where ratings.movieId=?";
            PreparedStatement rating_statement = dbcon.prepareStatement(rating_query);
            rating_statement.setString(1,id);
            ResultSet rating_rs = rating_statement.executeQuery();
            JsonObject ro = new JsonObject();
            while (rating_rs.next()){
                String movie_rating = rating_rs.getString("rating");
                ro.addProperty("movie_rating",movie_rating);
            }
            if(ro.has("movie_rating")) {
                jsonArray.add(ro);
            }

            String url = (String) request.getSession().getAttribute("movielist_url");
            System.out.println("url: "+url);

            JsonObject uo = new JsonObject();
            uo.addProperty("movielist_url",url);
            jsonArray.add(uo);

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
