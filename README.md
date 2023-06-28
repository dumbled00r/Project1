
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

- Inside the project1 file, most of our commands stay at services file:
    - Inside services file, there are some parameters you need to change to make the program works properly: 
    (NOTE: change the parameters down below if you want to use the program for your own use of telegram, if not it's better to just run the MainApp.java file without changing anything)
        - In Authorize.java, we need to change 2 parameters which are request.apiId and request.apiHash to your own apiId and apiHash. You can find both of the parameters here: https://my.telegram.org/auth, after adding your phone number to the login interface, it will ask you to insert your confirmation code. The confirmation code will appear on your Telegram app.
    ```java
    request.apiId = 21253741;
    request.apiHash = "9df061bb226225982dad3aa34ae47647";
    ```
        - In 


## Features

- Get one or many user_id in a group
- Get all chat groups information
- Send message to an user or a group
- Get group invite link
- Add users to an existing group



## Environment Variables

To run this project, you will need to add the following environment variables to your .env file

`API_KEY`

`ANOTHER_API_KEY`

