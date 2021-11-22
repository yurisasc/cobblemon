# Pokémon Cobbled

An open-source Pokémon mod written in Kotlin.

## Getting started

To set up the development workspace, first clone the project and open the build.gradle with Intellij. This will automatically
put together all the dependencies and project setup.

Run `gradlew genIntellijRuns`, and then in Intellij go to your run configurations, and change whichever you plan on 
running to use a Java 16 JRE. runClient is as if you booted up in singleplayer, runServer is a server without a client, 
and runData is for summoning the Eldritch gods or something, idk.