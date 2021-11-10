# JavaHostel

Example Web application used in lectures on Web Development in Java. It's supposed to be the website of a hostel that could be used by people to register as guests and book rooms for their trips to the island of Java (*), but only the guest registration feature is implemented as an example.

(*) Actually, the name comes from the fact that its first version was implemented in Java. As I decided to try and implement it in other Web platforms to enrich the example, I changed the story a little bit. :)

Lectures published (in Portuguese) in [prof. Vítor Souza](http://www.inf.ufes.br/~vitorsouza)'s [YouTube Channel](https://www.youtube.com/c/ProfVitorSouza/).

Each folder is a different implementation using different platforms/tools:

* `javaee8` [:link:](javaee8/): this is the original implementation, in its latest update it was built with [Eclipse IDE](https://www.eclipse.org/eclipseide/) 2020-03 manually integrated with [WildFly](https://www.wildfly.org/) 19.1 and [MySQL](https://dev.mysql.com/) 8. If you need help installing and integrating these tools, you can follow the [first part of JButler's tutorial](https://github.com/dwws-ufes/jbutler/wiki/Tutorial00) (assuming it has not been updated to another platform/tool);

* `jakartaee9` [:link:](jakartaee9/): upgrade of the original implementation to Jakarta EE 9, using [Visual Studio Code](https://code.visualstudio.com/), [WildFly](https://www.wildfly.org/), [MySQL](https://dev.mysql.com/) and [Maven](https://maven.apache.org/).


