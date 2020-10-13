Project is developed in java8.
Use following command to compile the project

javac RingCoordinator.java Process.java 

After compiling, package it using the following command.

jar cvfm lcr.jar manifest.txt *.class

Now run the package with the following command

java -jar lcr.jar

input.dat is present in the directory and replace it or edit the file for custom input.
(First line of input.dat is ring size and following lines are unique ids of processes)
Sample output can be seen in output.JPG file.