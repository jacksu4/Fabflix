package main.java;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {
    Connection dbcon;
    Document dom;
    private Map<String, String> catMap = new HashMap<String, String>();

    public XMLParser(){
        catMap.put("Susp","Thriller");
        catMap.put("CnR","Crime");
        catMap.put("Dram","Drama");
        catMap.put("West","Western");
        catMap.put("Myst","Mystery");
        catMap.put("S.F.","Sci-Fi");
        catMap.put("Advt","Adventure");
        catMap.put("Horr","Horror");
        catMap.put("Romt","Romance");
        catMap.put("Comd","Comedy");
        catMap.put("Musc","Musical");
        catMap.put("Docu","Documentary");
        catMap.put("Porn","Pornography");
        catMap.put("Noir","Black");
        catMap.put("BioP","Biography");
        catMap.put("TV","TV Show");
        catMap.put("TVs","TV Series");
        catMap.put("TVm","TV Miniseries");
    }

    private void run(){
        try{
            connectToDatabase();
            System.out.println("Successfully connected to database");
            parseMainsFile();
            parseMovies();
            dbcon.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    private void connectToDatabase(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            dbcon = DriverManager.getConnection("jdbc:mysql" + ":///" + "moviedb" + "?autoReconnect=true&useSSL=false",
                    "mytestuser", "mypassword");
            if(dbcon!=null){
                System.out.println("Database Connection Established");
            }
        }catch(Exception e){
            System.out.println("Connection failed");
        }


    }

    private void parseMainsFile(){
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("data_files/mains243.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseMovies() throws SQLException {
        System.out.println("parsing movies");
        Element docEle = dom.getDocumentElement();
        NodeList nl = docEle.getElementsByTagName("film");
        if(nl != null && nl.getLength()>0){
            for(int i=0; i<nl.getLength(); i++){
                Element el = (Element) nl.item(i);
                String id, title, director;
                int year;

                try{
                    id = getTextValue(el,"fid");
                    if(id.trim().length()==0)
                        id = "null";
                }catch(NullPointerException e){
                    //System.out.println("id not found");
                    id = "null";
                }
                try{
                    title = getTextValue(el, "t");
                    if(title.trim().length()==0)
                        title = "null";
                }catch(NullPointerException e){
                    //System.out.println("title not found");
                    title = "null";
                }

                try{
                    year = getIntValue(el, "year");
                }catch(NumberFormatException e){
                    //System.out.println("invalid year");
                    year = -1;
                }
                try{
                    director = getTextValue(el,"dirn");
                    if(director.trim().length()==0)
                        director = "null";
                }catch(NullPointerException e){
                    //System.out.println("director not found");
                    director = "null";
                }

                //System.out.println("id: "+id+" title: "+title+" year: "+year+" director: "+director);

                //insert movie record
                if(!id.equals("null") && !title.equals("null") && year!=-1 && !director.equals("null")){
                    //check for duplicate movies
                    try{
                        String query = "select id, title, year, director from movies_temp where movies_temp.title = ?";
                        PreparedStatement statement = dbcon.prepareStatement(query);
                        statement.setString(1,title);
                        ResultSet rs = statement.executeQuery();
                        if(rs.next() && title == rs.getString("title") && year == rs.getInt("year") && director == rs.getString("director")){
                            System.out.println("Skip duplicate movie record id: "+id+" title: "+title+" year: "+year+" director: "+director);
                        }else{
                            try{
                                String insert_query = "insert into movies_temp (id, title, year, director) values (?,?,?,?)";
                                PreparedStatement insert_statement = dbcon.prepareStatement(insert_query);
                                insert_statement.setString(1, id);
                                insert_statement.setString(2,title);
                                insert_statement.setInt(3,year);
                                insert_statement.setString(4,director);
                                //System.out.println(insert_statement);

                                insert_statement.executeUpdate();
                                insert_statement.close();

                                //System.out.println("movie recored successfully inserted");
                                NodeList cl = el.getElementsByTagName("cat");
                                if(cl!=null && cl.getLength()>0){
                                    for(int j=0;j<cl.getLength();j++){
                                        Element ce = (Element) cl.item(j);
                                        String genre = ce.getFirstChild().getNodeValue();
                                        if(catMap.containsKey(genre)){
                                            genre = catMap.get(genre);
                                            //Add new genre if not exist in database
                                            String genre_query = "select * from genres_temp where genres_temp.name = ?";
                                            PreparedStatement genre_statement = dbcon.prepareStatement(genre_query);
                                            genre_statement.setString(1,genre);
                                            ResultSet genre_rs = genre_statement.executeQuery();
                                            if(!genre_rs.next()){
                                                String genre_insert = "insert into genres_temp (name) values (?)";
                                                PreparedStatement genreIS = dbcon.prepareStatement(genre_insert);
                                                genreIS.setString(1,genre);
                                                genreIS.executeUpdate();
                                                genreIS.close();
                                            }
                                            String genre_insert = "insert into genres_in_movies_temp (genreId, movieId) values ((select id from genres_temp where genres_temp.name = ?),?)";
                                            PreparedStatement genreIS = dbcon.prepareStatement(genre_insert);
                                            genreIS.setString(1,genre);
                                            genreIS.setString(2,id);
                                            genreIS.executeUpdate();
                                            genreIS.close();
                                        }else{
                                            //System.out.println("Invalid movie genre id: "+id+" title: "+title+" year: "+year+" director: "+director+" genre: "+genre);
                                        }
                                    }
                                }


                            }catch(SQLException e){
                                System.out.println("SQLException catched for movie record id: "+id+" title: "+title+" year: "+year+" director: "+director);
                                System.out.println(e.getMessage());
                            }
                        }
                        rs.close();
                        statement.close();
                    }catch(SQLException e){
                        System.out.println("SQLException catched");
                        System.out.println(e.getMessage());
                    }
                }else{
                    System.out.println("Skip Invalid movie record id: "+id+" title: "+title+" year: "+year+" director: "+director);
                }
            }
        }
    }


    private void parseActorsFile(){
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("data_files/actors63.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    private void parseCastsFile(){
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("data_files/casts124.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    //helper functions
    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }
    private int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    public static void main(String[] args){
        XMLParser parser = new XMLParser();
        parser.run();
    }

}
