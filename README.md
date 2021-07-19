# CAT Module 2
This module serves as a backend for adaptive and classical online-tests in GeoGebra.

## Getting Started

### Technologies
- Maven 3.6.3
- Spring-Boot 2.3.4
- MySQL 8.0.22
- R 3.4.3 (This will be replaced in later stages of the project.)

### Preconditions
Make sure that Maven(https://maven.apache.org/install.html), MySQL(https://dev.mysql.com/doc/mysql-installation-excerpt/5.7/en/) and R(https://www.r-project.org/) are installed.

### Setup
1. Create a MySQL Database available via Port 3306 (Username=user, Password=user) --> https://ladvien.com/data-analytics-mysql-localhost-setup/
2. To install all dependencies defined in the pom.xml file run 'mvn install' via the shell.

### Running the module
1. In order to start the program run 'mvn spring-boot:run' via the shell.
2. The application is now accessable on port 8080 via the REST-API (https://app.getpostman.com/join-team?invite_code=505cb6400bfef4865222e8b50b4c72ed&ws=f08bad50-e58a-403e-8a5e-d926863467ab).

#### Further information on running the module
In order to create an adaptive quiz the database of the module needs to be filled, currently it can be filled with testing data using the REST-API or by importing the data directly into the database (testing data is provided via this repository).

## Code
The code is split into two packages:
1. entities
This contains all the spring entities as well as the MaintenanceController which contains the REST-API for CRUD-operations on the database.

2. logic
This contains all the classes that contain the logic of the module, as well as the QuizController which contains the REST-API for the execution of the quizzes.
The TestEngine class serves as an administrational core, which manages all the quizzes and forwards the requests and responses between the QuizController and the quiz-instances.
The AdaptiveQuiz and ClassiQuiz classes contain quizlogic, like selecting the next question, etc.

## Tests
No tests are currently implemented, this will be done in the later stages of the project.
