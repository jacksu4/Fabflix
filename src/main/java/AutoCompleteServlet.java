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

@WebServlet(name = "AutoCompleteServlet", urlPatterns = "/api/autocomplete")
public class AutoCompleteServlet extends HttpServlet {


    private DataSource dataSource;

    private static JsonObject generateJsonObject(String url, String title) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("url", url);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
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
        String title = request.getParameter("title");
        System.out.println(title);
        PrintWriter out = response.getWriter();

        try{

//            Context initContext = new InitialContext();
//            Context envContext = (Context) initContext.lookup("java:/comp/env");
//            dataSource = (DataSource) envContext.lookup("jdbc/moviedb");
//            Connection dbcon = dataSource.getConnection();
            String url = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(url, "mytestuser", "mypassword");

            String query = "select id, title from movies where match(title) against (? in boolean mode)";

            PreparedStatement statement = dbcon.prepareStatement(query);
            statement.setString(1,AddPlus(title));

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            int count = 0;

            while (rs.next() && count < 10){ //limit to 10 entries
                String rs_title = rs.getString("title");
                String rs_id = rs.getString("id");
                String rs_url = "single-movie.html?id=" + rs_id;
                jsonArray.add(generateJsonObject(rs_url,rs_title));
                count++;
            }

            rs.close();
            statement.close();
            response.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
    }
}
