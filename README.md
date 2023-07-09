
## Telegram Management Service

A Java project about Telegram on managing services for user to do automatical actions such as adding user to a existing chat or get chat information


## Presequites
We are using IntelliJ IDEA Community Edition for this project
Link download: https://www.jetbrains.com/idea/download/?var=1&section=windows

- First, we are going to build the libraries required for this project

    More information: https://tdlib.github.io/td/build.html?language=Java
- After that, we need to set up our external libraries by pressing right click in project1 file:
    - ![external_libraries_1](https://github.com/dumbled00r/Project1/blob/main/image/external_lib_1.jpg)
- Then, you add a new project library by pressing the "+" button and choose Java
- After that, you find the lib file and press OK:
    - ![lib_project](https://github.com/dumbled00r/Project1/blob/main/image/lib_project.png)
- Then press apply to set up the libraries
- After running MainApp.java, if you encounter this error
    - ![error_1](https://github.com/dumbled00r/Project1/blob/main/image/error_1.png)
      Move the tdjni.dll to your system32 file (system32 is located at C:\Windows\System32)

## Demo
- First, we need to run the file MainApp.java   
    - Then the app will ask you to enter your phone number (Remember the format will be: +84xxxxxxxxx)
    - ![enter_authentication](https://github.com/dumbled00r/Project1/blob/main/image/enter_authentication.png)
    - After that, it will ask you for your confimation code, this code will appear in your Telegram app
    - ![login_code](https://github.com/dumbled00r/Project1/blob/main/image/login_code.png)
- After that, you need to input the command for it to work, just input "help" and the list of command will appear
    - ![enter_command](https://github.com/dumbled00r/Project1/blob/main/image/enter_command.png)
- For example, you want to send message to an existing chat
    - First, you need to find chatid by input command "getallchat"
    - ![get_all_chat](https://github.com/dumbled00r/Project1/blob/main/image/get_all_chat.png)
    - Second, find the chatid you want to message to then input command "sm <chatid> <message>"
    - ![sm](https://github.com/dumbled00r/Project1/blob/main/image/sm.png)
    - If it appears like this, then you are done!, you have sent a message to an existing chat
    - ![test_a_b_c](https://github.com/dumbled00r/Project1/blob/main/image/test_a_b_c.png)

## Task Scheduler
Task Scheduler is used to rerun the program once a day, this is how you do it
- First you need to extract ScheduledUpdate.rar file
- Modify the run.bat files **(cd path/to/run.bat)** to your extracted path where it contains **run.bat** 
- After that, we need to create task ( It's located on the right hand side)
    - ![create_task](https://github.com/dumbled00r/Project1/blob/main/image/create_task.png)
- Second, enter task name and task description as you please (NOTE: Remember to tick on the "Run with highest privileges" box)
    - ![run_with](https://github.com/dumbled00r/Project1/blob/main/image/run_with.png)
- Third, you need to switch to triggers tab, add a new trigger
    - ![daily](https://github.com/dumbled00r/Project1/blob/main/image/daily.png)
- Then you create a new action
    - ![run_bat_file](https://github.com/dumbled00r/Project1/blob/main/image/run_bat_file.png)


You are done! Now the program will be running daily!

* NOTE: change the parameters down below if you want to use the program for your own use of telegram, if not it's better to just run the MainApp.java file without changing anything
- Inside the project1 file, most of our commands stay at services file:
    - Inside Utils file, there are some parameters you need to change to make the program works properly: 
        - In Authorize.java, we need to change 2 parameters which are request.apiId and request.apiHash to your own apiId and apiHash. You can find both of the parameters here: https://my.telegram.org/auth, after adding your phone number to the login interface, it will ask you to insert your confirmation code. The confirmation code will appear on your Telegram app.
    ```java
    request.apiId = your_api_id;
    request.apiHash = "your_api_hash";
    ```
    - Inside our AirTableUtils file at AirTable.java, we need to change some parameters too which are: personal_access_token, base_id, table_id. You can find your personal_access_token here: https://airtable.com/create/tokens. Create a new token with all scope and access (After creating your token, you should save it somewhere because it only appears once). And for base_id, table_id you only need to get to your table, take the parameters from the url. For example: https://airtable.com/base_id/table_id/viwq1XaqKB0szrjSf?blocks=hide. Then change it in the code:
    ```java
    static String personal_access_token = "your personal_access_token";
    static String baseId = "your base_id";
    static String tableId = "your table_id";
    ```
      
## Features

- Get Members Of A Chat Group
- Get All Administrated Chat Information
- Get My Information
- Send Message To An Existing Chat
- Add User To An Existing Chat
- Kick User Out Of An Existing Chat
- Sync To AirTable
- Get Messages History Of An Existing Chat





