# **PS2 Klient**

This software is basically a GUI wrapper for the old and good [ps2client by Naomi Peori aka ooPo.](https://github.com/ps2dev/ps2client)

Back around 2008-2009 I've used RadHostClient by [Radad1](http://psx-scene.com/forums/f19/radhostclient-1-8-a-54519/), though I've been unable to use it since I started using OSX. The only binaries I could find were compatible with Linux and Windows only, and couldn't find its source code anywhere.

Since I'm familiar with Java and Kotlin (a JVM based language) and learned a little bit of JavaFX back in the college, I thought it would be a good challenge trying and creating a wrapper for ps2client which is 1-) compatible with all the three major operating systems and 2-) open source, as it can be extended by anyone interested in doing so.

The project name (PS2 Klient) is obviously based on the underlying software used for communication with the PS2 and comes from the fact that many Kotlin libraries have names that [include the letter "K" in it.](https://github.com/mcxiaoke/awesome-kotlin)

I would like to rewrite the PS2Client in Kotlin following the protocol specification [available here](https://github.com/ps2dev/ps2client/blob/master/doc/ps2link-protocol.txt), though that's not a top priority now since the only limitation of the ps2client used is that apparently it's not possible to rename files and folders when acessing the shared folders through uLaunchELF. Other than that, it's totally usable, including usage with Simple Media System (SMS) for watching videos.

# **Features**

- Compatible with [SMS - Simple Media System](http://members.casema.nl/eugene_plotnikov/) and [uLaunchELF](http://psx-scene.com/forums/official-ulaunchelf-forums/)
- Status bar showing connection status
- Automatically restart underlying ps2client when the specified IP address becomes reachable again (after navigating from uLaunchELF to SMS for example)
- Automatically restart ps2client when user changes the IP address in the text field

# **Coding**

I have been using IntelliJ IDEA Community 2018.2.6 with both [TornadoFX](https://github.com/edvin/tornadofx) and Kotlin plugins installed.

# **Requirements**

The requirement for running the program is having [Java 8 JRE installed.](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
If you want to build the project, it's necessary to have [Java 8 JDK installed.](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

# **Screenshots**

Thanks to JavaFX being a technology which doesn't depend on any specific OS, the app runs on any of the three major operating systems and has a look and feel of a native application, such as ilustrated by the screenshots below.

**macOS**

<img width="400" alt="screen shot 2018-02-26 at 2 49 35 am" src="https://user-images.githubusercontent.com/11051024/36655397-29ac352c-1aa1-11e8-9e96-ad3d43eccf57.png">

**Windows**

<img width="400" alt="screen shot 2018-02-26 at 2 52 22 am" src="https://user-images.githubusercontent.com/11051024/36655398-29cf39fa-1aa1-11e8-9e5b-391b50b7dada.png">

**Ubuntu**

<img width="400" alt="screen shot 2018-02-26 at 2 56 17 am" src="https://user-images.githubusercontent.com/11051024/36655399-29ed6006-1aa1-11e8-8859-db0f3f97f9c6.png">
