package main.java;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.Statement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet(name = "AdvanceSearchServlet", urlPatterns = "/api/advancesearch")
public class AdvanceSearchServlet extends HttpServlet{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String title = request.getParameter("advance-title");

        JsonArray ja = new JsonArray();
        JsonObject jo = new JsonObject();
        jo.addProperty("title", title);
        ja.add(jo);

        System.out.println(ja.toString());
        out.write(ja.toString());
        response.setStatus(200);
        out.close();

    }
}
