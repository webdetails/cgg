# Community Graphics Generator #

**CGG** is a Pentaho plugin that allows the user to export CCC/CDE charts as images, enabling the inclusion of CDE charts inside Pentaho reports. This plugin is able to render, on server-side, the same chart that is rendered on the browser by CDE/CDF.


#### Pre-requisites for building the project:
* Maven, version 3+
* Java JDK 1.8
* This [settings.xml](https://github.com/pentaho/maven-parent-poms/blob/master/maven-support-files/settings.xml) in your <user-home>/.m2 directory


#### Building it


```
$ mvn clean install
```

This will build, unit test, and package the whole project (all of the sub-modules). The artifact will be generated in: ```target```
The resulting zip package can be unzipped and dropped inside your Pentaho Server system folder.


#### Running the tests

__Unit tests__

This will run all tests in the project (and sub-modules).
```
$ mvn test
```

If you want to remote debug a single java unit test (default port is 5005):
```
$ cd core
$ mvn test -Dtest=<<YourTest>> -Dmaven.surefire.debug
```

__IntelliJ__

* Don't use IntelliJ's built-in maven. Make it use the same one you use from the commandline.
  * Project Preferences -> Build, Execution, Deployment -> Build Tools -> Maven ==> Maven home directory
