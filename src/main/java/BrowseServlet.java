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
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet(name = "BrowseServlet", urlPatterns = "/api/browse")
public class BrowseServlet extends HttpServlet{

    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
//            Context initContext = new InitialContext();
//            Context envContext = (Context) initContext.lookup("java:/comp/env");
//            dataSource = (DataSource) envContext.lookup("jdbc/moviedb");
//            Connection dbcon = dataSource.getConnection();
            String url = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(url, "mytestuser", "mypassword");

            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = "select distinct genres.name as name from genres";

            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                jsonArray.add(rs.getString("name"));
            }

            out.write(jsonArray.toString());

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
