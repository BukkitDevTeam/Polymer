Polymer
========
A free and open source, high performance Java 7 Minecraft server.

What is it?
-----------
Frustrated by the slow performance of the stock Minecraft server, as well as the fact that other third part implementations do not fit the need of the discerning server owner, Polymer was created. Polymer is based on the latest Java versions, libraries and development principles to ensure continued performance as new features. Parts of the core Minecraft functionality, such as complex terrain generation have been left out. The main reason for leaving out terrain generation in this case is that any third party implementation will simply not be Minecraft, and many servers use pregenerated maps. When the fabled Minecraft API comes out, Polymer will endeavour to support it entirely.

Compilation
-----------

Even if you are not a programmer, and just want to play around with Polymer, we make things very easy for you. Simply clone this repository and then build it with Maven 3. In all modern IDEs this will be automatic, if not install Maven 3 from [here](http://maven.apache.org/download.html), and then simply execute `mvn clean install` in the source code directory.

Code Style
----------
Code style is bog standard Oracle Java, follow the Bukkit guidelines if there is something you are unsure of, we will always notify you about it should you submit a pull request. One minor convention is, please refrain from using the `this` keyword, unless it is required.

Methods should be created in such a way that no Javadoc is needed other than a base description of the method. This means no `@param`, `@return` or `@throws` tags. The use of `{@link Object}`, is however encouraged.


Technology
----------
Whilst we try not to go overboard with 50mb of libraries, there are some libraries crucial to development, these and their intended usages are outlined below.

* Java 7 - Using Java 7 means we get to use all sorts of new stuff (mainly in the way of keeping code clean and networking), it also means that the server will run consistantly on the latest JRE.
* Trove - Using the Trove collections framework, or more specifically Trove4J when storing any collections revolving around primitive types, there is a huge memory and speed performances. Don't be stingy when it comes to storing data in RAM!
* Lombok - Lombok greatly reduces our boilerplate code size by allowing us to automatically generate commonly used code structs. You can read more about its features [here](http://projectlombok.org/features/index.html). If Lombok can do it, then use it!

Implemented features
--------------------
* None

Upcoming features
-----------------
* Plugin loading
* Server connection and pinging
* Basic configuration
* Successful login, including minecraft.net authentication
* Flat world
* Console