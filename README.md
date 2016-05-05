Project 3
=========

This is the third project for undergraduate database course in SNU, 2016 Spring. In this project, we implement a simple 'School Information System' using JDBC.

Compilation
-----------

**In Unix/Linux console (including Cygwin)**

`javac` is used for compiling this project. Compilation process can be done by :

	cd src/
	javac -cp ../jar/tibero5-jdbc.jar *.java

**In Windows command prompt**

It's almost same as that of Unix/Linux, but it uses '\' instead of '/'.

	cd src
	javac -cp ..\jar\tibero5-jdbc.jar *.java

Execution
---------

**In Unix/Linux console**

Execution process can be done by :

	cd src/
	java -cp ../jar/tibero5-jdbc.jar:. Main

**In Windows command prompt**

Replace '/' with '\', and colon(:) with semicolon(;) :
	
	cd src
	java -cp ..\jar\tibero5-jdbc.jar;. Main

**In Cygwin for Windows**

	cd src/
	java -cp "../jar/tibero5-jdbc.jar;." Main

Configuration
-------------

Environment variables, such as `JDBC_DRIVER`, `HOST`, `PORT`, `DATABASE_URL`, `USERNAME`, `PASSWORD`, are located in `Main.java`. They are shared by other classes in the project, so editing variables in `Main.java` will make effects to the whole project. In default, they are set as :

	JDBC_DRIVER = "com.tmax.tibero.jdbc.TbDriver";
	HOST = "localhost";
	PORT = "8629";
	DATABASE_URL = "jdbc:tibero:thin:@" + HOST + ":" + PORT + ":tibero";
	USERNAME = "sys";
	PASSWORD = "tibero";

