## CS 122B Project 

#### Web Demo URL

project1: https://www.youtube.com/watch?v=uHtuv8x6Uwg&feature=youtu.be

project2: https://www.youtube.com/watch?v=3mwuI-ZDAhk

project3: https://youtu.be/J3P5RgbeCjc


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

  

Jin Zeyu

- Create SingleMovie page and added related html, js files
- added CSS files and bootstrap classes to beautify all three pages
- implement search function, shopping cart function and payment function
- implement xml parsing function and optimization
