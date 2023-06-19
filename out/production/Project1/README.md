
# Project Title

A Java project about Telegram on  


## Presequites

We are using IntelliJ IDEA Community Edition for this project
Link download: https://www.jetbrains.com/idea/download/?var=1&section=windows

- First, we are going to build the libraries required for this project

    More information: https://tdlib.github.io/td/build.html?language=Java

- Inside the project1 file, most of our commands stay at services file:
    - Inside services file, there are some parameters you need to change to make the program works normally:
    In Authorize.java, we need to change 2 parameters which are request.apiId and request.apiHash to your own apiId and apiHash. You can find both of the parameters here: https://my.telegram.org/auth, after adding your phone number to the login interface, it will ask you to insert your confirmation code. The confirmation code will appear on your Telegram app.
    ```java
    request.apiId = 21253741;
    request.apiHash = "9df061bb226225982dad3aa34ae47647";
    ```



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

