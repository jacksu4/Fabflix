package main.java;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
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

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet{

    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String creditCardNum = request.getParameter("creditCardNum");
        String expirationDate = request.getParameter("expirationDate");

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        PrintWriter out = response.getWriter();
        try {
            // Get a connection from dataSource
//            Context initContext = new InitialContext();
//            Context envContext = (Context) initContext.lookup("java:/comp/env");
//            dataSource = (DataSource) envContext.lookup("jdbc/moviedb");
//            Connection dbcon = dataSource.getConnection();
            String url = "jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection dbcon = DriverManager.getConnection(url, "mytestuser", "mypassword");

            // Declare our statement

            String query = "select * from creditcards where creditcards.id = ?";

            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1, creditCardNum);

            ResultSet rs = statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();

            if (!rs.next()) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "credit card number " + creditCardNum + " doesn't exist");
            } else {
                if (!rs.getString("firstName").equals(firstName)) {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect first name");
                }else if(!rs.getString("lastName").equals(lastName)){
                    responseJsonObject.addProperty("status","fail");
                    responseJsonObject.addProperty("message","incorrect last name");
                }else if(!rs.getString("expiration").equals(expirationDate)){
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect expiration date");
                }else{
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
            }

            response.getWriter().write(responseJsonObject.toString());
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
    }
}
