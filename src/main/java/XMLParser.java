

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

import com.mysql.jdbc.Driver;

import java.io.FileWriter;
import java.io.IOException;

public class XMLParser {
    private Connection dbcon;
    private Document dom;
    private Map<String, String> catMap = new HashMap<String, String>();
    private Set<String> movieSet = new HashSet<>();
    private Set<String> movieSet_exist = new HashSet<>();
    private Map<String, String> starMap = new HashMap<>();
    private Map<String, String> starMap_exist = new HashMap<>();

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
            long startTime = System.nanoTime();
            connectToDatabase();
            System.out.println("Successfully connected to database");
            parseMainsFile();
            parseMovies();
            System.out.println("End of Parsing Movies");
            parseActorsFile();
            parseStars();
            System.out.println("End of Parsing Stars");
            parseCastsFile();
            parseStarsInMovies();
            System.out.println("End of Parsing StarsInMovies");
            dbcon.close();
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            System.out.println(duration/1000000);
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

    private void parseMovies() throws SQLException, IOException {
        FileWriter myWriter = new FileWriter("mains_xml_inconsistency.txt");
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
                }catch(NullPointerException e){
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
                        String query = "select id, title, year, director from movies where movies.title = ?";
                        PreparedStatement statement = dbcon.prepareStatement(query);
                        statement.setString(1,title);
                        ResultSet rs = statement.executeQuery();
                        if(rs.next() && title == rs.getString("title") && year == rs.getInt("year") && director == rs.getString("director")){
                            myWriter.write("Skip duplicate movie record id: "+id+" title: "+title+" year: "+year+" director: "+director+"\n");

                        }else{
                            try{
                                if(!movieSet.contains(id)){
                                    movieSet.add(id);
                                    String insert_query = "insert into movies (id, title, year, director) values (?,?,?,?)";
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
                                            String genre;
                                            try{
                                                genre = ce.getFirstChild().getNodeValue();
                                            }catch(NullPointerException e){
                                                genre = "null";
                                            }

                                            if(catMap.containsKey(genre)){
                                                genre = catMap.get(genre);
                                                //Add new genre if not exist in database
                                                String genre_query = "select * from genres where genres.name = ?";
                                                PreparedStatement genre_statement = dbcon.prepareStatement(genre_query);
                                                genre_statement.setString(1,genre);
                                                ResultSet genre_rs = genre_statement.executeQuery();
                                                if(!genre_rs.next()){
                                                    String genre_insert = "insert into genres (name) values (?)";
                                                    PreparedStatement genreIS = dbcon.prepareStatement(genre_insert);
                                                    genreIS.setString(1,genre);
                                                    genreIS.executeUpdate();
                                                    genreIS.close();
                                                }
                                                String genre_insert = "insert into genres_in_movies (genreId, movieId) values ((select id from genres where genres.name = ?),?)";
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
                                }
                            }catch(SQLException e){
                                myWriter.write("SQLException catched for movie record id: "+id+" title: "+title+" year: "+year+" director: "+director+"\n");
                                myWriter.write(e.getMessage()+"\n");
                            }
                        }
                        rs.close();
                        statement.close();
                    }catch(SQLException e){
                        myWriter.write("SQLException catched\n");
                        myWriter.write(e.getMessage()+"\n");
                    }
                }else{
                    myWriter.write("Skip Invalid movie record id: "+id+" title: "+title+" year: "+year+" director: "+director+"\n");
                }
            }
            myWriter.close();
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
    private void parseStars() throws SQLException, IOException {
        System.out.println("parsing stars");
        FileWriter myWriter = new FileWriter("actors_xml_inconsistency.txt");
        Element docEle = dom.getDocumentElement();
        NodeList nl = docEle.getElementsByTagName("actor");
        if(nl!=null && nl.getLength()>0){
            for(int i=0; i<nl.getLength(); i++){
                Element el = (Element) nl.item(i);

                //get id from database
                String id_query = "select max(id) from stars";
                PreparedStatement id_qs = dbcon.prepareStatement(id_query);
                ResultSet id_rs = id_qs.executeQuery();
                id_rs.next();
                String id = "nm"+(Integer.parseInt(id_rs.getString("max(id)").substring(2))+1);
                //System.out.println(id);
                id_rs.close();
                id_qs.close();

                //get name from xml
                String name;
                try{
                    name = getTextValue(el,"stagename");
                    if(name.trim().length()==0)
                        name = "null";
                }catch(NullPointerException e){
                    name = "null";
                }

                //get birthYear from xml
                int birthYear;
                try{
                    birthYear = getIntValue(el, "dob");
                }catch(NumberFormatException e){
                    birthYear = -1;
                }catch(NullPointerException e){
                    birthYear = -1;
                }

                //insert data into stars table
                //check for duplicate data
                //System.out.println("id: "+id+" name: "+name+" birthYear: "+birthYear);
                String star_query = "select * from stars where stars.name = ?";
                PreparedStatement star_qs = dbcon.prepareStatement(star_query);
                star_qs.setString(1,name);
                ResultSet star_rs = star_qs.executeQuery();
                if(!star_rs.next()){
                    if(!name.equals("null")){
                        if(!starMap.containsKey(name)){
                            starMap.put(name,id);
                            String star_insert = "insert into stars (id,name,birthYear) values (?,?,?)";
                            PreparedStatement star_is = dbcon.prepareStatement(star_insert);
                            star_is.setString(1,id);
                            star_is.setString(2,name);
                            if(birthYear!=-1){
                                star_is.setInt(3,birthYear);
                            }else{
                                star_is.setNull(3,Types.INTEGER);
                            }
                            star_is.executeUpdate();
                            star_is.close();
                        }

                    }else{
                        myWriter.write("Skip invalid star record id: "+id+" name: "+name+" birthYear: "+birthYear+"\n");
                    }
                }else{
                    String star_query_temp = "select id from stars where stars.name = ?";
                    PreparedStatement star_qs_temp = dbcon.prepareStatement(star_query_temp);
                    star_qs_temp.setString(1,name);
                    ResultSet star_rs_temp = star_qs_temp.executeQuery();
                    String starId = "null";
                    if(star_rs_temp.next()) {
                        starId = star_rs_temp.getString("id");
                    }
                    star_rs_temp.close();
                    starMap_exist.put(name, starId);
                    myWriter.write("Skip duplicate star record id: "+starId+" name: "+name+" birthYear: "+birthYear+"\n");
                }
                star_rs.close();
                star_qs.close();
            }
            myWriter.close();
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

    private void parseStarsInMovies() throws SQLException, IOException {
        System.out.println("parsing stars in movies");
        FileWriter myWriter = new FileWriter("casts_xml_inconsistency.txt");
        FileWriter dataWriter = new FileWriter("stars_in_movies_data.txt");
        dataWriter.write("starId\tmovieId\n");
        Element docEle = dom.getDocumentElement();
        NodeList nl = docEle.getElementsByTagName("m");
        if(nl!=null && nl.getLength()>0){
            for(int i=0; i<nl.getLength(); i++){
                Element el = (Element) nl.item(i);

                //get fid from xml and check if fid in db
                String movieId;
                try{
                    movieId = getTextValue(el,"f");
                    if(movieId.trim().length()==0)
                        movieId = "null";
                }catch(NullPointerException e){
                    movieId = "null";
                }

                if(!movieId.equals("null")){

                    String movie_query = "select * from movies where movies.id = ?";
                    PreparedStatement movie_qs = dbcon.prepareStatement(movie_query);
                    movie_qs.setString(1,movieId);
                    ResultSet movie_rs = movie_qs.executeQuery();
                    if(!movie_rs.next()){
                        movieId = "null";
                    }
                    movie_rs.close();
                    movie_qs.close();

//                    if(!movieSet.contains(movieId)){
//                        movieId = "null";
//                    }


                }

                //get star name from xml and find starId from db
                String starName;
                try{
                    starName = getTextValue(el,"a");
                    if(starName.trim().length()==0)
                        starName = "null";
                }catch(NullPointerException e){
                    starName = "null";
                }

                if(!starName.equals("null") && !starName.equals("sa")){

//                    String star_query = "select id from stars where stars.name = ?";
//                    PreparedStatement star_qs = dbcon.prepareStatement(star_query);
//                    star_qs.setString(1,starName);
//                    ResultSet star_rs = star_qs.executeQuery();
//                    String starId = "null";
//                    if(star_rs.next()){
//                        starId = star_rs.getString("id");
//                    }
                    String starId = "null";
                    if (starMap_exist.containsKey(starName)){
                        starId = starMap_exist.get(starName);
                    }

                    if(starMap.containsKey(starName)){
                        starId = starMap.get(starName);
                    }

                    if(!starId.equals("null") && !movieId.equals("null")){
                        /*
                        String star_insert = "insert into stars_in_movies (starId, movieId) values (?,?)";
                        PreparedStatement star_is = dbcon.prepareStatement(star_insert);
                        star_is.setString(1,starId);
                        star_is.setString(2,movieId);
                        star_is.executeUpdate();
                        star_is.close();

                         */

                        dataWriter.write(starId+"\t"+movieId+"\n");
                    }else{
                        myWriter.write("invalid stars_in_movies record starId: "+starId+" movieId: "+movieId+"\n");
                    }

                }
            }
            myWriter.close();
            dataWriter.close();
            String loadData = "load data local infile 'stars_in_movies_data.txt' into table stars_in_movies";
            PreparedStatement loadData_is = dbcon.prepareStatement(loadData);
            loadData_is.executeUpdate();
            loadData_is.close();
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
