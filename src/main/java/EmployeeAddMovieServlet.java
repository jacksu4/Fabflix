package main.java;

import com.google.gson.JsonArray;
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

import jdk.nashorn.internal.codegen.CompilerConstants;
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "EmployeeAddMovieServlet", urlPatterns = "/api/employee_add_movie")
public class EmployeeAddMovieServlet extends HttpServlet {

    private DataSource dataSource;
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movietitle = request.getParameter("movietitle");
        String movieyear = request.getParameter("movieyear");
        String moviedirector = request.getParameter("moviedirector");
        String starname = request.getParameter("starname");
        String genrename = request.getParameter("genrename");


        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = dataSource.getConnection();

            // Declare our statement

            String query = "call add_movie(?, ?, ?, ?, ?, @responseMsg)";

            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1, movietitle);
            statement.setInt(2, Integer.parseInt(movieyear));
            statement.setString(3, moviedirector);
            statement.setString(4, starname);
            statement.setString(5, genrename);

            statement.executeQuery();

            String out_query = "select @responseMsg as response";
            Statement out_statement = dbcon.createStatement();

            ResultSet rs = out_statement.executeQuery(out_query);

            String result = "";

            while(rs.next()){
                result = rs.getString("response");
//                System.out.println(rs.getString("response"));
            }


            out.write(result);

            response.setStatus(200);
            rs.close();
            statement.close();
            out_statement.close();
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


