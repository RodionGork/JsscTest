# JsscTest
Test of arduino interfacing with serial port from java

1. Load arduino project in the IDE, compile and upload to the chip
2. Build the java project with `mvn package`
3. Connect arduino to computer, make sure the serial port have appeared
4. Run the java project with `mvn exec:java`, choose the port and enter some text
5. Arduino will respond with line lengths of each received string.
