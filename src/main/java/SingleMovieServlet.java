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

            String query = "select movies.title, movies.year, movies.director, ratings.rating from movies,ratings where movies.id=? and ratings.movieId=?;";

            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1,id);
            statement.setString(2,id);

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()){

                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }

            //genres

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
