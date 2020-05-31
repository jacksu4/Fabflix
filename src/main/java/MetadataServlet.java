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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet(name = "MetadataServlet", urlPatterns = "/api/metadata")
public class MetadataServlet extends HttpServlet{
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = dataSource.getConnection();

            DatabaseMetaData meta = dbcon.getMetaData();

            ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"});

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String table_name = rs.getString("TABLE_NAME");
                ResultSet rs_att = meta.getColumns(null, null, table_name, null);
                JsonArray sub_jsonArray = new JsonArray();
                while(rs_att.next()){
                    String column_name = rs_att.getString("COLUMN_NAME");
                    String column_type = rs_att.getString("DATA_TYPE");
                    JsonArray small_jsonArray = new JsonArray();
                    small_jsonArray.add(table_name);
                    small_jsonArray.add(column_name);
                    small_jsonArray.add(column_type);
                    sub_jsonArray.add(small_jsonArray);
                }
                jsonArray.add(sub_jsonArray);
                rs_att.close();
            }

            out.write(jsonArray.toString());

            response.setStatus(200);
            rs.close();
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