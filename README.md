# COMS 4156: Innov8 Team Project - Client App

This is the GitHub repository for the **client app portion** of the team project for group 'Innov8' associated with COMS 4156 W Advanced Software Engineering at Columbia University.

## Viewing the Service Repository
Please use the following link to view the repository relevant to the service: https://github.com/anavilohia/Innov8

## Team Name

Innov8

## Team Members

Anavi Lohia (al3750) \
Jane Lim (jl6094) \
Jonghyun Lee (jl6509) \
Jungyun Kim (jk4661)

## About Our App
Our app targets healthcare workers and is designed to provide them with 
an intuitive interface to manage resources, tasks, and schedules based on location and priority. 
With free and robust management functionalities, it is easier than ever for healthcare workers
to optimize resource allocation and task prioritization efficiently.

The following sections describe the app's functionality in more detail and how the app integrates with the LiveSched service.

### App Functionality
1. Register your organization with our service: Our app provides a simple way of registering first time organizations.
Just go to the main homepage, which is the login page, and type in your desired Client ID. This will create your account and save your data so that 
when you log in next time with the same Client ID, you can view your data again.

2. Login to the app: Go to the main homepage and login with your Client ID.

3. View all dashboards: Once logged in, you will see the LiveSched Dashboards page where you can direct yourself to one
of the three dashboards - resource, task, and schedule.

4. Resource dashboard: This dashboard supports the below functionalities.
   - View resource types: You can view all resources you have entered in the system, including their type name, total units, and location.
   - Search by type name: If you want to view a specific resource, you can use the search bar to find it by the type name.
   - Sort by total units: You can order all resources by the number of their total units, either lowest units first or highest units first.
   - Add new resource type: You can add new resources by specifying the resource type, total units available, and the location.
   - Delete resource type: You can delete resources that you no longer have.

5. Task dashboard: This dashboard supports the below functionalities.
   - View tasks: You can view all tasks you have entered in the system, including their task ID, task name, priority, start time, and end time.
   - Search by task ID: If you want to view a specific task, you can use the search bar to find it by its task ID.
   - Sort by priority: You can order all tasks by their priority, either highest priority first (priority 1 is the highest) or lowest priority first (priority 5 is the lowest).
   - Add new task: You can add a new task by specifying the task name, priority, start time, end time, and location.
   - Delete task: You can delete tasks that you no longer have.
   - View task details: You can select a task from the table of all tasks to view its details.
     - View all details: You can view the task ID, task name, priority, start time, end time, location, and resources needed.
     - Manage resources needed: You can manage the resources needed by the task by either adding a new resource type needed with its required amount or modifying the quantity of an existing, required resource type.

6. Schedule dashboard: This dashboard supports the below functionalities. 
    - View schedule: You can view the generated schedule where each task in the system is matched with its required resources if the resources are available for the task.
    - Generate/update schedule: You can generate a schedule for the first time or update the schedule by specifying the maximum distance between task locations and resources. Available resources that are within the maximum distance will be assigned to tasks with higher priority first.
    - Unschedule task: You can unschedule a task from the schedule once the task is done.

7. Logout: Once you log out, you will be redirected to the homepage.

### How It Works With Our Service
The client app has its own frontend interface and a backend. When a user interacts with the client, such as adding a task, 
a request is sent to the backend of the client app. This backend then sends a request to the LiveSched service if necessary.
The service processes the request, applies any updates to its database, and returns the result, which is displayed in the frontend of the app.

### What Makes Our App Better Than Prior Solutions
1. Free to use: The app is entirely free, with no hidden costs. 
2. Privacy ensured: No user data ever gets sold to third parties.
3. Rich functionality: Offers robust and complete suite of features for task and resource management.
4. Simple UI: Designed for ease of use with intuitive navigation.

## Building and Running a Local Instance
In order to build and use our app you must install the following (This guide assumes Mac, so please make changes as needed for Windows):

1. [Maven 3.9.5](https://maven.apache.org/docs/3.9.5/release-notes.html)
2. [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
3. [IntelliJ IDE](https://www.jetbrains.com/idea/download/?section=mac) (Recommended IDE, optionally you may use any other)
4. [Clone](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository) this repository using git clone or IntelliJ
5. Please make sure you followed the build instructions on the service repository  (linked on top) and ran the service first, as the service must be operational in order to launch the client app.
6. Once this repository is cloned into a directory of your choice, please open it in IntelliJ as a project, and navigate to the `LiveSchedClient` directory.
7. Go into `LiveSchedService.java` and alter the `BASE_URL` of the service as needed. By default, it assumes that the service is running on localhost, port 8080.
8. You can then build the client app by using `mvn -B clean install --file pom.xml` in the terminal (make sure you switched to the `LiveSchedClient` directory) and then execute the `LiveSchedClientApplication.java` file to launch the app.
9. The app can be accessed in your browser at localhost:8081. Once you confirm that the code is running, navigate there to begin interacting with the web app.

## Running a Cloud Based Instance

This app is currently available as a Google cloud based instance that can be accessed using the following URL:

https://innov8-liveschedclientapp.uk.r.appspot.com/

A successful connection should lead you to a homepage that displays this:
<img width="1224" alt="Screenshot 2024-12-03 at 6 06 45 PM" src="https://github.com/user-attachments/assets/f0e3d017-ada9-480d-aa79-6d4bf67f28e2">

## Running Checkstyle
You can run checkstyle by using either one of these commands in the terminal.

```
mvn checkstyle:check
mvn checkstyle:checkstyle
```

## Running Tests
All unit tests are located under the directory `src/test`.

They can be run individually in IntelliJ using right-click on the test files. Or, you can run the whole test suite using the following command:

```
mvn clean test
```

## End-To-End Testing
In order to properly perform end-to-end tests, please follow the steps below 
and compare the results of your actions to the expected results provided.

The following is a manual test checklist that ensures all core features of 
the client app and its integration with the service are functional. 
Perform the steps **IN ORDER** to maintain consistency.

0. If running locally, please ensure no other applications are running that use up ports on your system.
1. Follow the build guide in the service's repository to run the service locally or confirm that a cloud instance of the service is operational.
   - If running locally, ensure the service is available at http://127.0.0.1:8080.
2. Follow the build guide above to run the client app locally or confirm that a cloud instance is operational.
    - If running locally, ensure the app is available at http://127.0.0.1:8081. The expected result of this action should be the display of the homepage, which is the login page, headlined with "Welcome to LiveSched".
3. Enter a client ID, which can be "endTest" for this testing purpose (make sure that you haven't used our app with this client ID before, as that can lead to different results than expected below; if you have used our app with this client ID before, please pick a fresh ID to start), and click the 'Login' button.
   - You should see the 'LiveSched Dashboards' page with your client ID "endTest" noted on the top right.
4. Click on the 'Go to Resources' button to view the resource dashboard.
    - Since you just signed in with a new client ID, it is expected for you to see the message indicating that there are no resource types found.
5. Click on the 'Add New Resource Type' button to add a new resource.
    - You should be directed to a page where you can type in all required fields.
6. Type in the following for each field: resource type name "Nurse", total units available "3", latitude "40.84", and longitude "-73.94". Then, click the 'Add Resource Type' button.
    - You should be directed back to the resource dashboard where you can see the resource that you just added. Confirm that the details are correct.
7. To test the search bar and sort functionalities, add another resource type by repeating the previous steps (5-6) but this time with these values: resource type name "Doctor", total units available "1", latitude "40.84", and longitude "-73.94".
    - You should be directed back to the resource dashboard where you now see two resources (Nurse and Doctor) in the table. Confirm that the details are correct.
8. To test the search bar functionality, type in "Nurse" in the search bar and click the 'Search/Sort' button.
    - You should only see one row, which corresponds to Nurse, in the table and not Doctor.
9. To test that the search bar handles invalid inputs, type in a resource type name that doesn't exist yet, such as "Bed".
    - You should see all resource types you have entered (Nurse and Doctor) and a message indicating that there are no resource type with name Bed.
10. To test the sort functionality, click on 'Sort by Total Units', select 'Lowest Units First', and click the 'Search/Sort' button.
    - You should see two rows in the table where Doctor appears first and Nurse appears next because we are sorting by lowest units first. You can check that the other way "Highest Units First" works as intended as well.
11. Now, click on the 'All Dashboards' button on the top right to go back to the 'LiveSched Dashboards' page.
12. Then, click on the 'Go to Tasks' button to view the task dashboard.
     - Since you haven't added any tasks yet, it is expected for you to see the message indicating that there are no tasks found.
13. Click on the 'Add New Task' button to add a new task.
    - You should be directed to a page where you can type in all required fields.
14. Type in the following for each field: task name "Checkup", priority "4", start time "2024-12-17 10:00", end time "2024-12-17 10:30", latitude "40.81", and longitude "-73.96". Then, click the 'Add Task' button.
    - You should be directed to the 'Task Details' page where you can view all details of the task you just created. Confirm that all details are correct and that the task ID is 1 since this is your first task.
15. You should have noticed that the 'Resources Needed' field says that there are no resources added yet. Now, it's time to add the required resources for this task. Click the 'Add New Resource' button.
    - You should see that a form appears below the button. 
16. Please enter "Doctor" for the resource type and "1" for the quantity. Then, click the 'Add' button.
    - You should see that "Doctor: 1" is added to the table. It is also expected for you to see the "Modify" link right next to it.
17. To test the modify functionality, click on the 'Modify' link.
    - You should see a form appearing on the right side of "Doctor: 1".
18. Enter a new quantity, such as "2". Then, click the 'Update' button.
    - You should see that the quantity has been updated to "Doctor: 2".
19. The modify form also allows you to remove the resource from resources needed. To test that, repeat the previous steps (17-18) but with quantity "0" this time.
    - You are expected to no longer see "Doctor: 2" and instead see the message indicating that there are no resources added yet.
20. For the remaining testing purposes, please repeat steps 15-16. Ensure that you should see "Doctor: 1" again.
21. Click on the 'Back to Task Dashboard' button to go back to the task dashboard.
    - You should see that the task you just created now appears in the table.
22. To test the search bar and sort functionalities, add another task by repeating the previous steps (13-16). This time, use these values: task name "Emergency", priority "1", start time "2024-12-17 10:00", end time "2024-12-17 10:30", latitude "40.81", and longitude "-73.96". For the resources needed, add "Doctor" with quantity 1 and then "Nurse" with quantity 1.
    - Confirm that the task ID is 2 since this is the second task and that all details are correct.
23. Click on the 'Back to Task Dashboard' button to go back to the task dashboard.
    - You should now see two tasks in the table where the first row corresponds to the Checkup task with ID 1 and the second row corresponds to the Emergency task with ID 2.
24. To test the search bar functionality, type in "2" in the search bar and click the 'Search/Sort' button.
    - You should only see one row in the table which corresponds to the Emergency task with ID 2.
25. To test that the search bar handles invalid inputs, type in a task ID that doesn't exist yet, such as "3".
    - You should see two tasks in the table and a message indicating that there are no task with ID 3.
26. To test the sort functionality, click on 'Sort by Priority', select 'Highest Priority First', and click the 'Search/Sort' button.
    - You should see two tasks in the table where Emergency appears first and Checkup appears next because we are sorting by highest priority first. You can check that the other way "Lowest Priority First" works as intended as well.
27. Now, click on the 'All Dashboards' button on the top right to go back to the 'LiveSched Dashboards' page.
28. Then, click on the 'Go to Schedules' button to view the schedule dashboard.
    - Since you haven't generated a schedule yet, it is expected for you to see the message indicating that there is no schedule found.
29. Click the 'Generate/Update Schedule' button to generate a schedule.
    - You should see a modal or popup window that displays an instruction and a box to enter the maximum distance. The maximum distance entered here will be used to match tasks with resources nearby that are within the maximum distance.
30. Enter "10" for the maximum distance and click the 'Update Schedule' button.
    - You should now see the schedule table that lists all tasks that are matched with available resources that are within the maximum distance.
    - In this case, you will see only the Emergency task with ID 2 in the schedule. This is expected because based on the setup that we went through above, there are 1 Doctor and 3 Nurse, but both of the tasks that we added require 1 Doctor each at the same time. Since the scheduling algorithm matches available resources with tasks that have higher priority, it is expected that the Emergency task with priority 1 was matched with the one and only doctor.
31. Now, suppose that the task was completed. You can now unschedule the task from the schedule by clicking the 'Unschedule' button.
    - You will see a confirmation dialogue. Click 'OK' to proceed. Then, you will again see the message indicating that there is no schedule.
32. To test the remaining functionalities, which are the delete operations, click the 'All Dashboards' button to go back to the 'LiveSched Dashboards' page.
33. Click the 'Go to Tasks' button to go to the task dashboard again.
34. Confirm that you can still see the two tasks, and click on the 'Delete' button on the first row of the table, which corresponds to the Checkup task.
    - You will see a confirmation dialogue. Click 'OK' to proceed. Then, you should see only one task remaining in the table, which corresponds to the Emergency task.
35. This time, click on the Emergency task from the table and go to the 'Task Details' page for it. Click the 'Delete Task' button there.
    - Again, you will see a confirmation dialogue. Click 'OK' to proceed. Now, you will be redirected to the task dashboard and should see the message indicating that there are no tasks found.
36. Now, click the 'All Dashboards' button to go back to the 'LiveSched Dashboards' page again.
37. Then, click on the 'Go to Resources' button to view the resource dashboard.
38. Confirm that you can still see the two resource types in the table, and click on the 'Delete' button on the second row of the table, which corresponds to Doctor.
    - A confirmation dialogue should appear. Click 'OK' to proceed. You should now only see Nurse in the table.
39. Now, to test that the data persists, first click the 'Logout' button on the top right to log out of the app.
    - You should be redirected to the main homepage, which is the login page.
40. Type in the client ID that you entered in step 3 and click the 'Login' button.
41. Confirm that the client ID is correctly displayed on the top right, and click the 'Go the Resources' button.
    - You should see the same resource dashboard as before with only Nurse in the table.
42. You can also check that the task dashboard remains the same with nothing there because we deleted all tasks.

End of testing

## Checkstyle Report
This is the most recent checkstyle report with the most updated code on Nov 27, 2024.

<img width="654" alt="client checkstyle 2024-11-27 at 4 23 13 PM" src="https://github.com/user-attachments/assets/9f82b0d5-7191-4597-af3a-fc7e732588da">
<img width="1459" alt="client checkstyle 2024-11-27 at 4 24 23 PM" src="https://github.com/user-attachments/assets/07f13e7a-c3c6-4e83-b930-df8565aff12d">

## A Note to Developers
This client app shows just one example of how one can use the service located in the linked repository on top of this document. 
There are many possible ways to use the service to make different kinds of client apps that target multiple user groups.
Please review the API documentation we have for the service to understand available endpoints, including their required parameters and possible responses,
and use an HTTP client to interact with the endpoints.

## Tools Used

The following tools were used in the development and modification of this repository:

* Maven Package Manager
* GitHub Branch Protection Rules
   * Requires at least one review approval for every pull request into 'main' branch.
* Checkstyle
   * Checks that the code follows style guidelines, generating warnings or errors as needed.
* JUnit
   * JUnit tests can be manually ran using the code specified in above sections.

---------------------------------------------

`citations.txt` is located at the root level of this repository, and it specifies URLs for all resources used as reference in the development of this repository.


