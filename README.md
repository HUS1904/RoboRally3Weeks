# RoboRally3Weeks

## Project SetUp Guide
To setup the RoboRally-software:

1. Navigate to the GitHub-repository in the following link:
   https://github.com/HUS1904/RoboRally3Weeks/tree/EXPERMINTAL

2. Press the "Code"-button and choose "Download ZIP". Extract all files from the ZIP-folder. Once the files have been extracted, you should be left with 2 folders:
   - "RoboRally3Weeks-EXPERMINTAL" (All necessary software to run RoboRally).
   - "Cum" (All necessary software to run CumApplication).

3. Open MySQL and create a new connection. Name it "arkibald". Ensure that hostname, port, username, and password all match the ones in the "application.properties"-file found in directory: cum\target\classes. Create a new query and insert the following lines:

   create database arkibald;
   use arkibald;
   - and execute.

4. Open the now un-zipped folder and open "RoboRally3Weeks-EXPERMINTAL" in a given code-editing software such as IntelliJ IDEA, Visual Studio Code, or similar. Perform the same step with "Cum" in another window, so both folders are open on separate windows of your chosen software.

5. To run the server navigate to cum\src\main\java\com.example.cum\CumApplication - and run the class "CumApplication".

6. Ensure that the branch "EXPERMINTAL" is chosen for RoboRally and navigate to src\main\java\dk.dtu.compute.se.pisd\roborally\StartRoboRally - and run the class "StartRoboRally" to start the game. Make sure to run multiple instances of the game to simulate multiple players playing at the same time.

The game and server should now be running and the game is ready to play.
