# mongodb

Docker command to bring up the mongoDB 
>docker run --name mongodb -v folder path:/data/db -p 27017:27017 -d mongo:latest
Port mapping to access the mongodb
volume mapping to access data without losing data while making mongodb down or restart.

Project will connect to db and insert the data in form of document to mongo DB
