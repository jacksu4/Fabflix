package main.java;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.sql.Date;

@WebServlet(name = "OrderConfirmationServlet", urlPatterns = "/api/order-confirmation")
public class OrderConfirmationServlet extends HttpServlet{
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet( HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("doGet start");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        User user = (User) session.getAttribute("user");
        String user_email = user.getUsername();
        System.out.println("user_email = "+user_email);

        try {
            System.out.println("getting connection");
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            //obtain customers.id
            System.out.println("obtaining customers.id");

            String query = "select customers.id from customers where customers.email = ?";
            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1,user_email);
            ResultSet rs = statement.executeQuery();
            rs.next();
            int customerId = rs.getInt("id");
            System.out.println(customerId);
            rs.close();
            statement.close();

            Date saleDate = new Date(System.currentTimeMillis());
            System.out.println(saleDate);
            int quantity, movie_price, saleId;
            String movie_id, movie_title;


            Iterator iter = cart.getIterator();
            JsonArray ja = new JsonArray();
            while(iter.hasNext()){
                Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iter.next();
                movie_id = entry.getKey();
                quantity = entry.getValue();

                //obtain movie_title, movie_price
                System.out.println("obtaining movie_title, movie_price");

                String movie_query = "select movies.title, movies_price.price from movies, movies_price where movies.id = ? and movies_price.movieId = movies.id";
                PreparedStatement movie_statement = dbcon.prepareStatement(movie_query);
                movie_statement.setString(1,movie_id);
                ResultSet movie_rs = movie_statement.executeQuery();
                JsonObject jo = new JsonObject();

                while(movie_rs.next()){
                    movie_title = movie_rs.getString("title");
                    movie_price = movie_rs.getInt("price");
                    jo.addProperty("movie_title", movie_title);
                    jo.addProperty("movie_price", movie_price);
                    jo.addProperty("quantity", quantity);
                }
                movie_rs.close();
                movie_statement.close();


                //insert sale data
                System.out.println("inserting sale data");

                String insert_query = "insert into sales (customerId, movieId, saleDate, quantity) values (?,?,?,?)";
                PreparedStatement insert_statement = dbcon.prepareStatement(insert_query);
                insert_statement.setInt(1, customerId);
                insert_statement.setString(2,movie_id);
                insert_statement.setDate(3,saleDate);
                insert_statement.setInt(4,quantity);
                System.out.println(insert_statement);

                insert_statement.executeUpdate();
                insert_statement.close();

                //obtain saleId
                System.out.println("obtaining saleId");

                String saleid_query = "select last_insert_id()";
                PreparedStatement saleid_statement = dbcon.prepareStatement(saleid_query);
                ResultSet saleid_rs = saleid_statement.executeQuery();
                saleid_rs.next();
                saleId = saleid_rs.getInt("last_insert_id()");
                jo.addProperty("saleId", saleId);

                saleid_statement.close();
                saleid_rs.close();

                ja.add(jo);
            }

            response.getWriter().write(ja.toString());
            response.setStatus(200);
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
