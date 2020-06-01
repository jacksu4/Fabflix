package main.java;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

@WebServlet(name = "AddToCartServlet", urlPatterns = "/api/addToCart")
public class AddToCartServlet extends HttpServlet{


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        response.setContentType("text/html");
        PrintWriter out=response.getWriter();

        HttpSession session=request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        String id = request.getParameter("id");
        System.out.println(id);
        cart.add(id);
        response.setStatus(200);

        out.close();
    }
}
