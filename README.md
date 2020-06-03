## CS 122B Project 

#### Web Demo URL

project1: https://www.youtube.com/watch?v=uHtuv8x6Uwg&feature=youtu.be

project2: https://www.youtube.com/watch?v=3mwuI-ZDAhk

project3: https://youtu.be/J3P5RgbeCjc

project4 (part1): https://www.youtube.com/watch?v=pjw8AcgzJHw&feature=youtu.be

project4 (part2): https://youtu.be/giFw6wOP0AM


#### Project Deployment Instruction

We use the same way to deploy as instructed:

  1.  git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74.git

  2.   you should have **Mysql User**  `mytestuser`  with password `mypassword`.

  3. 
     Open IntelliJ -> Import Project -> Choose the project you just cloned (The root path must contain the pom.xml!) -> Choose Import project from external model -> choose Maven -> Click on Finish -> The IntelliJ will load automatically

  4. For "Root Directory", right click "cs122b-proj1" -> Mark Directory as -> sources root

  5. In `WebContent/META-INF/context.xml`, make sure the mysql username is `mytestuser` and password is `mypassword`

  6. Also make sure you have the `moviedb` database

  7. To run the example, follow the instructions in [canvas](https://canvas.eee.uci.edu/courses/26486/pages/intellij-idea-tomcat-configuration)

substring matching design: support substring, e.g. %love, %eat% on __director, title and star name__

#### Prepared Statement Links
 - EmployeeAddMovieServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/EmployeeAddMovieServlet.java
 - EmployeeAddStarServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/EmployeeAddStarServlet.java
 - LoginEmployeeServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/LoginEmployeeServlet.java
 - LoginServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/LoginServlet.java
 - MovieListServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/MovieListServlet.java
 - OrderConfirmationServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/OrderConfirmationServlet.java
 - PaymentServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/PaymentServlet.java
 - ShoppingCartServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/ShoppingCartServlet.java
 - SingleMovieServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/SingleMovieServlet.java
 - SingleStarServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/SingleStarServlet.java
 - XMLParser https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/XMLParser.java
 
#### XML Parser and Optimization
 - inconsistent data is reported in corresponding .txt files in the root directory
 - Use local hashset and hashtable to record movieId and starId so that the time to check whether the movie or star exist in database is largely reduced
 - Use load data to largely reduce insert time
 - delete indexes before XMLParsing and then add back later
 - Local execution time is reduced from 159.615 seconds to 47.405 seconds

#### Members' Contribution

Su Jingcheng

- Initialize the project
- Create MovieList page, SingleStar page and added related html, js files
- implement login page, browse function, movielist sorting function
- implement recaptcha, https, encrypted password, employee login and dashboard function
- implement autocomplete and fulltext search
- implement connection pooling, master-slave replication and load balancers
  

Jin Zeyu

- Create SingleMovie page and added related html, js files
- added CSS files and bootstrap classes to beautify all three pages
- implement search function, shopping cart function and payment function
- implement xml parsing function and optimization
- implement the android app 
- implement log_processing script and jmeter testing

- # Connection Pooling
    - AutoCompleteServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/AutoCompleteServlet.java
    - EmployeeAddMovieServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/EmployeeAddMovieServlet.java
    - EmployeeAddStarServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/EmployeeAddStarServlet.java
    - LoginEmployeeServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/LoginEmployeeServlet.java
    - LoginServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/LoginServlet.java
    - MovieListServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/MovieListServlet.java
    - OrderConfirmationServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/OrderConfirmationServlet.java
    - PaymentServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/PaymentServlet.java
    - ShoppingCartServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/ShoppingCartServlet.java
    - SingleMovieServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/SingleMovieServlet.java
    - SingleStarServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/SingleStarServlet.java
    - BrowseServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/BrowseServlet.java
    - MetadataServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/MetadataServlet.java
    
    - #### We created two Resource in context.xml and two resource-ref in web.xml and specify parameters like maxTotal and maxIdle to control the pooling parameters. Instead of establishing connection and close connection in servlet physically. We precreate 100 connections, use envContext.lookup() to choose the host database we used and use getConnection() to get the connection every time we want to use it. This significantly reduce the response time and allow master-slave replication in mysql.
    
    - #### The two backend both have two resource: one is localhost and one is master's database. Whenever write to the database is needed, we use envContext.lookup() to choose the master's database and use getConnection() to get the connection. Otherwise we use localhost database to do read operations.
    

- # Master/Slave
    - context.xml https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/web/META-INF/context.xml
    - web.xml https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/web/WEB-INF/web.xml
    - OrderConfirmationServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/OrderConfirmationServlet.java (used remote master database)
    - EmployeeAddStarServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/EmployeeAddStarServlet.java (used remote master database)
    - EmployeeAddMovieServlet https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-74/blob/master/src/main/java/EmployeeAddMovieServlet.java (used remote master database)
    
    - #### We specify two resource in context.xml and two resource-ref in web.xml. Then, whenever we want to route to master's database to do write operations, We use envContext.lookup() to choose the master's database and use getConnection() to get the connection. Otherwise we use localhost database to do read operations.
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot**                 | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------                |----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | imgs/Graph-single-instance-cp-1thread.png    | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | imgs/Graph-single-instance-cp-10thread.png   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | imgs/Graph-https-single-instance-cp-10thread.png  | ??                    | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | imgs/Graph-single-instance-nocp-10thread.png  | ??                        | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot**        | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------       |----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | imgs/Graph-scaled-cp-1thread.png    | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | imgs/Graph-scaled-cp-10thread.png   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | imgs/Graph-scaled-nocp-10thread.png | ??                         | ??                                  | ??                        | ??           |
