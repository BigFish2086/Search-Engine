# Search-Engine
This is a search engine project it's core is built in java and with a node js / express web-application as its interface.

##  Installation Guide For Local Testing :
1. Install Java JDK 16.0.1 or higher on your host machine.
2. Install node js and npm
3. You need to have 
4. clone the repo:
```bash
git clone https://github.com/BigFish2086/Search-Engine
```
6. After downloading The node js file run the following inside the webapp folder:
``` bash
npm init -y
```
that should install install the needed packages for the webapp to run

8. Create the mysql database using the file according to the schema defined in the `mysql_schema.sql` file

9. Then run the `Main.java` file to run the crawler and the indexer

10. Then after finishing crawling for sometime you can test the webapp to search functionality
```bash
npm run start
```

11. finally visit the webapp at `127.0.0.1:3000` and happy searching 😊
