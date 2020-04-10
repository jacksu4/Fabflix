## CS 122B Project 1

#### Web Demo URL



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

     

#### Members' Contribution

Su Jingcheng

- Initialize the project

- Create MovieList page, SingleStar page and added related html, js files

  

Jin Zeyu

- Create SingleMovie page and added related html, js files
- added CSS files and bootstrap classes to beautify all three pages