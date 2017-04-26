# sbt-standalone-jar-template
A standalone jar file created using the sbt assembly

This is a template that you can build upon when you want to have a standa alone jar file created using the sbt-assembly plugin

First clone the project
git clone https://github.com/joesan/sbt-standalone-jar-template.git

To run the project and produce the jar file - 
sbt assembly

To run the jar file - 
java -cp target/scala-2.11/XXX.jar path.to.your.MainClass args(0)


