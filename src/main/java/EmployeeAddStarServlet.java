package main.java;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
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

@WebServlet(name = "EmployeeAddStarServlet", urlPatterns = "/api/employee_add_star")
public class EmployeeAddStarServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starname = request.getParameter("starname");
        String starbirthyear = request.getParameter("starbirthyear");

        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement get_statement = dbcon.createStatement();

            String get_largest_id_query = "select max(id) as max_id from stars";
            ResultSet max_rs = get_statement.executeQuery(get_largest_id_query);
            String id = "";
            while(max_rs.next()){
                id = "nm" + (Integer.parseInt(max_rs.getString("max_id").substring(2)) + 1);

                System.out.println(max_rs.getString("max_id"));
                System.out.println(id);
            }

            if (starbirthyear.equals("")==false){
                String insert_query = "insert into stars (id, name, birthYear) values (?,?,?)";
                PreparedStatement statement = dbcon.prepareStatement(insert_query);
                statement.setString(1, id);
                statement.setString(2, starname);
                statement.setInt(3, Integer.parseInt(starbirthyear));

                System.out.println(statement);
                statement.executeUpdate();
                statement.close();
            } else {
                String insert_query = "insert into stars (id, name) values (?,?)";
                PreparedStatement statement = dbcon.prepareStatement(insert_query);
                statement.setString(1, id);
                statement.setString(2, starname);

                System.out.println(statement);
                statement.executeUpdate();
                statement.close();
            }

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
    }
}


