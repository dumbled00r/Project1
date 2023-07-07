
# Project Title

A Java project about Telegram on  


## Presequites
We are using IntelliJ IDEA Community Edition for this project
Link download: https://www.jetbrains.com/idea/download/?var=1&section=windows

- First, we are going to build the libraries required for this project

    More information: https://tdlib.github.io/td/build.html?language=Java
- After that, we need to set up our external libraries by pressing right click in project1 file:
    - ![external_libraries_1](https://github.com/dumbled00r/Project1/blob/develop_2/img/external_lib_1.jpg)
- Then, you add a new project library by pressing the "+" button
    - ![external_libraries_2](https://github.com/dumbled00r/Project1/blob/develop_2/img/external_lib_2.jpg)
- After that, you need to look for the following library:
    - Json, you should search for org.json, the version we want to download is: org.json:json:20230227
      ![org.json](https://github.com/dumbled00r/Project1/blob/develop_2/img/org.json.jpg)
    - opencsv, you should search for com.opencsv, the version we want to download is: com.opencsv:opencsv:5.7.1
      ![opencsv](https://github.com/dumbled00r/Project1/blob/develop_2/img/opencsv.jpg)
- Then press apply to set up the libraries
  
* NOTE: change the parameters down below if you want to use the program for your own use of telegram, if not it's better to just run the MainApp.java file without changing anything
- Inside the project1 file, most of our commands stay at services file:
    - Inside services file, there are some parameters you need to change to make the program works properly: 
        - In Authorize.java, we need to change 2 parameters which are request.apiId and request.apiHash to your own apiId and apiHash. You can find both of the parameters here: https://my.telegram.org/auth, after adding your phone number to the login interface, it will ask you to insert your confirmation code. The confirmation code will appear on your Telegram app.
    ```java
    request.apiId = 21253741;
    request.apiHash = "9df061bb226225982dad3aa34ae47647";
    ```
    - Inside our AirTableUtils file at AirTable.java, we need to change some parameters too which are: personal_access_token, base_id, table_id. You can find your personal_access_token here: https://airtable.com/create/tokens. Create a new token with all scope and access (After creating your token, you should save it somewhere because it only appears once). And for base_id, table_id you only need to get to your table, take the parameters from the url. For example: https://airtable.com/base_id/table_id/viwq1XaqKB0szrjSf?blocks=hide. Then change it in the code:
    ```java
    static String personal_access_token = "your personal_access_token";
    static String baseId = "your base_id";
    static String tableId = "your table_id";
    ```
      
## Features

- Get information of one or many user_id in a group
- Get all chat groups information
- Send message to an user or a group
- Get group invite link
- Add users to an existing group





