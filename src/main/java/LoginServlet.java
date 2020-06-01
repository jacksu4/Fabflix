package main.java;

import com.google.gson.JsonObject;

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
import java.io.StringWriter;
import java.sql.*;
import java.lang.reflect.Field;

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    private DataSource dataSource;
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        //gRecaptchaResponse = "android";

        try {
            // Get a connection from dataSource
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = dataSource.getConnection();

            // Declare our statement

            String query = "select customers.email as Username, customers.password as Password from customers \n" +
                    "where customers.email = ?";

            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1, username);

            ResultSet rs = statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();

            try{
                if(!gRecaptchaResponse.equals("android")){
                    System.out.println("verify recaptcha");
                    RecaptchaVerifyUtils.verify(gRecaptchaResponse);
                }
                if (!rs.next()) {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
                } else {
                    System.out.println("encrpting password");
                    String encryptedPassword = rs.getString("password");
                    boolean success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                    if (!success) {

                        responseJsonObject.addProperty("status", "fail");
                        responseJsonObject.addProperty("message", "incorrect password");
                    } else {
                        request.getSession().setAttribute("user", new User(username));
                        request.getSession().setAttribute("cart", new Cart(username));

                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("message", "success");
                    }
                }
            }catch (Exception e){
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "recaptcha failed");
            }

            response.getWriter().write(responseJsonObject.toString());

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
    }
}

