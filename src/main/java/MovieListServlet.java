package main.java;

import javax.annotation.Resource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;



@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet{


    private DataSource dataSource;

    private PreparedStatement generateStatement(PreparedStatement statement, int result_per_page, int page, int start_num) throws SQLException {
        int offset = result_per_page * page;
        statement.setInt(start_num, result_per_page);
        statement.setInt(start_num+1, offset);
        return statement;
    }

    private String AddPlus(String string){
        StringBuffer res = new StringBuffer();
        String[] strArr = string.split(" ");
        for (String str:strArr){
            char[] stringArray = ("+" + str + "*").trim().toCharArray();
            String resStr = new String(stringArray);
            res.append(resStr).append(" ");
        }
        return res.toString().trim();
    }

    protected void doGet( HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            // Get a connection from dataSource
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = dataSource.getConnection();

            String url = "index.html?";
            Enumeration<String> paramNames = request.getParameterNames();
            if(paramNames!=null){
                while(paramNames.hasMoreElements()){
                    String param = paramNames.nextElement();
                    if (!request.getParameter(param).equals("null") && !request.getParameter(param).isEmpty()){
                        url += param + "=" + request.getParameter(param) + "&";
                    }
                }
            }
            url = url.substring(0,url.length()-1);
            request.getSession().setAttribute("movielist_url",url);
            //System.out.println(url);

            // Declare our statement
//            Statement statement = dbcon.createStatement();

//            String query = "select movies.id, movies.title, movies.year, movies.director, ratings.rating FROM movies, ratings \n" +
//                    "where movies.id=ratings.movieId order by ratings.rating desc limit 20";
            ResultSet rs;
            PreparedStatement statement;
            int page = Integer.parseInt(request.getParameter("page"));
            int result_per_page = Integer.parseInt(request.getParameter("resultperpage"));
            String first_method = request.getParameter("firstmethod");
            String second_method = request.getParameter("firstmethod");
            String first_sort = request.getParameter("firstsort");
            String second_sort = request.getParameter("secondsort");

            System.out.println(request.getParameter("advance"));
            System.out.println(request.getParameter("search"));
//            System.out.println(request.getParameter("start").equals("null"));
//            if (!request.getParameter("advancesearch").equals("null") && !request.getParameter("advancesearch").isEmpty()){
//                String title;
//                title = request.getParameter("title");
//                String query = "select id, title, year, director, rating from movies, ratings where match(title) against (? in boolean mode)" +
//                        "and ratings.movieId = id";
//
//                statement = dbcon.prepareStatement(query);
//                String res_title = AddPlus(title);
//                statement.setString(1, res_title);

            if (!request.getParameter("search").equals("null") && !request.getParameter("search").isEmpty()){

//                System.out.println(request.getParameter("search"));
                String title, director, star_name;
                if(request.getParameter("title").equals("null")){
                    title = "%%";
                }else{
                    title = "%"+request.getParameter("title")+"%";
                }
                if(request.getParameter("director").equals("null")){
                    director = "%%";
                }else{
                    director = "%"+request.getParameter("director")+"%";
                }
                if(request.getParameter("star_name").equals("null")){
                    star_name = "%%";
                }else{
                    star_name = "%"+request.getParameter("star_name")+"%";
                }
//                System.out.println(title+director+star_name);
                if(!request.getParameter("year").isEmpty() && !request.getParameter("year").equals("null")){
                    String year = request.getParameter("year");
                    String query = "select T1.id, T1.title, T1.year, T1.director, ratings.rating from (select distinct(movies.id), movies.title, movies.year, movies.director From movies where movies.id in (select distinct(movies.id) as movie_id from movies, stars, stars_in_movies " +
                            "where stars.name like ? and stars.id = stars_in_movies.starId and movies.id = stars_in_movies.movieId) " +
                            "and movies.title like ? and movies.director like ? and movies.year=?) as T1 left join ratings on T1.id=ratings.movieId " +
                            "order by " + first_sort + " " + first_method + ", " + second_sort + " " + second_method + " limit ? offset ?";

                    statement = dbcon.prepareStatement(query);
                    statement.setString(1,star_name);
                    statement.setString(2,title);
                    statement.setString(3,director);
                    statement.setString(4,year);

                    generateStatement(statement, result_per_page, page, 5);

                    //System.out.println(statement);
                }else{
                    String query = "select T1.id, T1.title, T1.year, T1.director, ratings.rating from (select distinct(movies.id), movies.title, movies.year, movies.director From movies where movies.id in (select distinct(movies.id) as movie_id from movies, stars, stars_in_movies" +
                            " where stars.name like ? and stars.id = stars_in_movies.starId and movies.id = stars_in_movies.movieId) " +
                            "and movies.title like ? and movies.director like ?) as T1 left join ratings on T1.id=ratings.movieId "+
                            "order by " + first_sort + " " + first_method + ", " + second_sort + " " + second_method + " limit ? offset ?";


                    statement = dbcon.prepareStatement(query);
                    statement.setString(1,star_name);
                    statement.setString(2,title);
                    statement.setString(3,director);

                    generateStatement(statement, result_per_page, page, 4);

                    System.out.println(statement);

                    //System.out.println(statement);
                }
            }else if (!request.getParameter("start").equals("null") && !request.getParameter("start").isEmpty()){
                System.out.println("enter start");
                String start = request.getParameter("start");
                if (!start.equals("*")) {
                    String query = "select T1.id, T1.title, T1.year, T1.director, ratings.rating from (select distinct(movies.id), movies.title as title, movies.year, movies.director FROM movies \n" +
                            "where movies.title like ?) as T1 left join ratings on T1.id = ratings.movieId " +
                            "order by " + first_sort + " " + first_method + ", " + second_sort + " " + second_method + " limit ? offset ?";

                    statement = dbcon.prepareStatement(query);

                    generateStatement(statement, result_per_page, page, 2);

                    statement.setString(1, start + "%");

                    //System.out.println(statement);

                } else {
                    String query = "select T1.id, T1.title, T1.year, T1.director, ratings.rating from (select distinct(movies.id), movies.title as title, movies.year, movies.director FROM movies \n" +
                            "where movies.title REGEXP '^[^A-Za-z0-9]') as T1 left join ratings on T1.id = ratings.movieId \n" +
                            "order by " + first_sort + " " + first_method + ", " + second_sort + " " + second_method + " limit ? offset ?";

                    statement = dbcon.prepareStatement(query);

                    generateStatement(statement, result_per_page, page, 1);
                }

            }else if (!request.getParameter("genre").equals("null") && !request.getParameter("genre").isEmpty()){
                System.out.println("enter genre");
                String genre = request.getParameter("genre");
                String query = "select T1.id, T1.title, T1.year, T1.director, ratings.rating from (select movies.id, movies.title, movies.year, movies.director FROM movies, genres, genres_in_movies \n" +
                        "where genres.name = ? and genres.id = genres_in_movies.genreId and genres_in_movies.movieId = movies.id) as T1 left join ratings on T1.id = ratings.movieId \n" +
                        "order by " + first_sort + " " + first_method + ", " + second_sort + " " + second_method + " limit ? offset ?";

                statement = dbcon.prepareStatement(query);

                generateStatement(statement, result_per_page, page, 2);

                statement.setString(1,genre);
                System.out.println(statement);

            }else if (!request.getParameter("advance").equals("null") && !request.getParameter("advance").isEmpty()){
                String title;
                title = request.getParameter("title");
                String query = "select id, title, year, director, ratings.rating from movies " +
                        "left join ratings on movies.id = ratings.movieId \n" +
                        "where match(title) against (? in boolean mode)" +
                "order by " + first_sort + " " + first_method + ", " + second_sort + " " + second_method + " limit ? offset ?";

                statement = dbcon.prepareStatement(query);
                String res_title = AddPlus(title);
                statement.setString(1, res_title);
                generateStatement(statement, result_per_page, page, 2);
                System.out.println(statement);

            }else{
                System.out.println("enter default");
                String query = "select movies.id, movies.title, movies.year, movies.director, ratings.rating FROM movies left join ratings on movies.id = ratings.movieId \n" +
                        "order by " + first_sort + " " + first_method + ", " + second_sort + " " + second_method + " limit ? offset ?";

                statement = dbcon.prepareStatement(query);

                generateStatement(statement, result_per_page, page, 1);
            }

            //System.out.println(statement);
            rs = statement.executeQuery();

            System.out.println("first statement success");


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

                if(movie_rating==null){
                    movie_rating = "N/A";
                }

                String genre_query = "select genres.name as genres_name from genres, genres_in_movies " +//add first 3 genre
                        "where genres_in_movies.movieId = ? and genres.id = genres_in_movies.genreId ORDER BY genres_name limit 3";

                PreparedStatement genre_statement = dbcon.prepareStatement(genre_query);

                genre_statement.setString(1, movie_id);

                ResultSet genre_rs = genre_statement.executeQuery();

                while(genre_rs.next()){
                    movie_genres.add(genre_rs.getString("genres_name"));
                }

                String star_query = "select stars.name as stars_name, stars.id as stars_id from stars, stars_in_movies " +
                        "where stars_in_movies.movieId = ? and stars.id = stars_in_movies.starId " +
                        "order by (select count(stars_in_movies.movieId) from stars_in_movies where stars_in_movies.starId=stars.id) desc, stars.name asc limit 3";

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


