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

4. View resource dashboard: This dashboard supports the below functionalities.
   - View resource types: You can view all resources you have entered in the system, including their type name, total units, and location.
   - Search by type name: If you want to view a specific resource, you can use the search bar to find it by the type name.
   - Sort by total units: You can order all resources by the number of their total units, either lowest units first or highest units first.
   - Add new resource type: You can add new resources by specifying the resource type, total units available, and the location.
   - Delete resource type: You can delete resources that you no longer have.

5. View task dashboard: This dashboard supports the below functionalities.
   - View tasks: You can view all tasks you have entered in the system, including their task ID, task name, priority, start time, and end time.
   - Search by task ID: If you want to view a specific task, you can use the search bar to find it by its task ID.
   - Sort by priority: You can order all tasks by their priority, either highest priority first (priority 1 is the highest) or lowest priority first (priority 5 is the lowest).
   - Add new task: You can add a new task by specifying the task name, priority, start time, end time, and location.
   - Delete task: You can delete tasks that you no longer have.
   - View task details: You can select a task from the table of all tasks to view its details.
     - View all details: You can view the task ID, task name, priority, start time, end time, location, and resources needed.
     - Manage resources needed: You can manage the resources needed by the task by either adding a new resource type needed with its required amount or modifying the quantity of an existing, required resource type.

6. View schedule dashboard: This dashboard supports the below functionalities. 
    - View schedule: You can view the generated schedule where each task in the system is matched with its required resources if the resources are available for the task.
    - Generate/update schedule: You can generate a schedule for the first time or update the schedule by specifying the maximum distance between task locations and resources. Available resources will be assigned to tasks with higher priority first.
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

Add URL

A successful connection should lead you to a homepage that displays this:


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


