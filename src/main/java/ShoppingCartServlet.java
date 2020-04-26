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

@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet{
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet( HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");

        if(!(request.getParameter("movie_id")==null)){
            System.out.println("modify item in cart");
            String id = request.getParameter("movie_id");
            if(request.getParameter("change").equals("decrease")){
                System.out.println("decrease "+id);
                cart.decrease(id);
            }else if(request.getParameter("change").equals("increase")){
                System.out.println("increase "+id);
                cart.add(id);
            }else if(request.getParameter("change").equals("delete")){
                System.out.println("delete "+id);
                cart.delete(id);
            }else{
                System.out.println("parameter change unrecognized");
            }
            session.setAttribute("cart", cart);
        }

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            Iterator iter = cart.getIterator();
            JsonArray ja = new JsonArray();
            while(iter.hasNext()){
                Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iter.next();
                String movie_id = entry.getKey();
                int quantity = entry.getValue();
                String movie_title;
                int movie_price;

                String query = "select movies.title, movies_price.price from movies, movies_price where movies.id = ? and movies_price.movieId = movies.id";
                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.setString(1,movie_id);
                ResultSet rs = statement.executeQuery();
                JsonObject jo = new JsonObject();

                while(rs.next()){
                    movie_title = rs.getString("title");
                    movie_price = rs.getInt("price");
                    jo.addProperty("movie_title", movie_title);
                    jo.addProperty("movie_price", movie_price);
                    jo.addProperty("movie_id", movie_id);
                    jo.addProperty("quantity",quantity);
                }
                ja.add(jo);
            }

            response.getWriter().write(ja.toString());
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

