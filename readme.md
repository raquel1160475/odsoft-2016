# odsoft-2016

The goal of this repository is to serve as a template for the projects developed during the ODSOFT and EDOM courses of the MEI Master Program, edition 2016/15.

** Please follow the instructions of the teachers of your course because they will always prevail over what is stated in this readme file. **
  
## Structure of Folders  

### ofbiz 
In this folder you will find a **full** version of ofbiz adapted to be used in the master program. The structure of the folder follows the structure of the original ofbiz project.
This ofbiz version is based on the original version "ofbiz-trunk-2016-09-11-v1760251".
This folder is also an Eclipse project.

### ofbiz-rest-client-demo
In this folder you will find a small java application that exemplifies how to access ofbiz by REST. 
This folder is also an Eclipse project.

### OSDOFT
In this folder you should add **all** artifacts developed in exercises and projects of the ODSOFT course.

You should create a subfolder for each exercise or project. For instance, subfolder "exercise1" will contain all the artifacts for exercise 1 of odsoft; subfolder "exercise2" will contain all the artifacts for exercise 2 of odsoft; subfolder "project" will contain all the artifacts of the final project of "odsoft", etc.  

**Note:** If for some reason you need to bypass these guidelines please ask for directions with your teacher and **always** state the exceptions in your commits and issues in bitbucket.

### EDOM


In this folder you should add **all** artifacts developed in exercises and projects of the EDOM course.

You should create a subfolder for each exercise or project. For instance, subfolder "exercise1" will contain all the artifacts for exercise 1 of edom; subfolder "exercise2" will contain all the artifacts for exercise 2 of edom; subfolder "project" will contain all the artifacts of the final project of "edom", etc.  

**Note:** If for some reason you need to bypass these guidelines please ask for directions with your teacher and **always** state the exceptions in your commits and issues in bitbucket.


## Issues and Commits

Each team will usually have a clone of this repository in bitbucket.

It is a good practice to create an **Issue** in bitbucket for each exercise or task that your team will be developing.

Since this repository may be shared accross courses we strongly purpose that the description of the issue **always** starts with the short name of the course. For example, for the first exercise of EDOM you should create an issue named "EDOM - Exercise 1".

Each commit in your repository should always make a reference to the corresponding issue. For instance, in the previous example, if the issue created has an id 22, then in the commit message you should make a ref to that issue. Example: "EDOM - Added class diagram for exercise 1, ref #22". You should also state the short name of the course.

You should also close the issue when the exercise or task is finished.

For each deadline/submission of your task/exercise/project you should also create a tag in the repository.

## How to Open the Projects in Eclipse

In order to open the projects in Eclipse (after you have a clone of the repository in your local computer) you should:

* Create a new workspace. This new workspace should be create in a new folder **not related to the folder where the clone of the repository is located**
* Follow the readme instructions in the ofbiz folder. Before importing the ofbiz project into eclipse you should run the command **gradlew eclipse** so that the ofbiz folder becomes suitable to be imported as an eclipse project.
* Run Eclipse
* Import the projects of the repository into the workspace. You should use "File"/"Import..." and then "Existing Projects into Workspace". Then select the folder of the project that you want to import. Repeat this step for each project you want to import.

The projects "ofbiz" and "ofbiz-rest-client-demo" can be imported into the eclipse workspace as described.

**Note:** As stated before, you should create your artifacts inside folders of the repository (according to the guidelines). When your task involves the creation of eclipse projects (they are also artifacts) you should create them in the folders of the repository as described. By doing so, your team colleges and teachers can easily open your projects in eclipse.