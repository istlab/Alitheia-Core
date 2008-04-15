***
*** README-RUN
***

This README explains how to run an Alitheia deployment, including the Core
Platform and the Web UI (public facing website).

***
*** Database Setup
***

Alitheia uses a Postgres database if possible, and on errors, falls back to use Derby. 
Derby is slow. Postgres is used...
* if a postgres server is running on localhost, on the default port, 
* which has a database named alitheia, 
* and a user with no password named alitheia. 


