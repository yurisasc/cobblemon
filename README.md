# Cobblemon

An open-source Pokémon mod for Minecraft Java Edition, written in Kotlin for Fabric and Forge.

## Getting started

To set up the development workspace, first clone the project and open the build.gradle with Intellij.  There will be
errors until you go to File -> Settings -> Build, Execution, Deployment -> Build Tools -> Gradle and change the Gradle JVM
to 17. After changing this, click the icon for reloading the project. This will automatically
put together all the dependencies and project setup.

Run `./gradlew genEclipseRuns`, and then in Intellij go to your run configurations, and change whichever you plan on
running to use a Java 17 JRE. runClient is as if you booted up in singleplayer, runServer is a server without a client,
and runData is for summoning the Eldritch gods or something, idk.

## Contributing

If you're interested in contributing to the project, you can simply fork the repository and comment on any of the issues 
[here](https://gitlab.com/cable-mc/cobblemon/-/issues?scope=all&state=opened&label_name[]=accepted&assignee_id=None).

If you want a more involved role in the project, involving branches on the main repository instead of forking and involvement
in design discussions, add and message Hiroku#4373 on our Discord here: https://discord.gg/AsNpJSST7G.
