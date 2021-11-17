# JavaHostel @ Jakarta EE 9

In this version, the Java Hostel example was implemented in Jakarta EE 9, using [Visual Studio Code](https://code.visualstudio.com/), [WildFly](https://www.wildfly.org/), [MySQL](https://dev.mysql.com/) and [Maven](https://maven.apache.org/). The following sections guide you through the construction of the code or you can just install/configure the necessary tools (see **Preparation**), clone the repository and run `mvn install`.

The example shows the following Jakarta EE 9 APIs in action:

- [Jakarta Contexts and Dependency Injection (CDI)](https://jakarta.ee/specifications/cdi/);
- [Jakarta Enterprise Beans (formerly EJB)](https://jakarta.ee/specifications/enterprise-beans/);
- [Jakarta Persistence (formerly JPA)](https://jakarta.ee/specifications/persistence/);
- [Jakarta Server Faces (JSF)](https://jakarta.ee/specifications/faces/), and its decorator API Facelets.

All of the above APIs are present in the Jakarta EE 9 Web profile, but you can also use a full profile dependency if you prefer.



## Preparation:

To follow the tutorial below or to clone and run the example, you will need to:

1. Have a Java Development Kit (JDK) that is compatible with the other tools. I used [OpenJDK 17](https://openjdk.java.net/projects/jdk/17/);

2. Have a version of [WildFly](https://www.wildfly.org/downloads/) that is compatible with Jakarte EE 9. At the time of this writing I used _wildfly-preview-25.0.1.Final_;

3. Have [MySQL server](https://dev.mysql.com/downloads/mysql/) (I had version 8.0.23 installed) and its administration front-end, [MySQL Workbench](https://dev.mysql.com/downloads/workbench/) (I had version 8.0.20 installed);

4. Have [Maven](https://maven.apache.org/download.cgi) (I had version 3.8.3 installed);

5. Using MySQL Workbench, create a database schema for Java Hostel and a user with full privileges on it. I created the `javahostel` schema, with encoding `utf8mb4` and a user `dwws` with password `dwws` with full privileges on `javahostel`. At the time of this writing, [the first part of JButler's tutorial](https://github.com/dwws-ufes/jbutler/wiki/Tutorial00) had more detailed instructions on this, if you need.



## Developing the JavaHostel from scratch:

If you want to develop the example from scratch and learn as you go along, follow the instructions in this section. Experienced developers please excuse the extensive explanations, I believe that they are quite useful for those that are still learning many of these things.


### Create and configure the Maven project:

In Maven, there's the concept of archetype, which are project skeletons from which you can bootstrap your own project. We will use an archetype mentioned in [a Jakarta EE 9 Hello World example](https://blog.payara.fish/getting-started-with-jakarta-ee-9-hello-world). Open a terminal, change to the directory where you want the `javahostel` project folder to be created, run the following command and provide the values for the project properties as in the example below:

```console
$ mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart
...
[INFO] Generating project in Interactive mode
[INFO] Archetype [org.apache.maven.archetypes:maven-archetype-quickstart:1.4] found in catalog remote
Define value for property 'groupId': br.ufes.inf.labes
Define value for property 'artifactId': javahostel
Define value for property 'version' 1.0-SNAPSHOT: :
Define value for property 'package' br.ufes.inf.labes: : br.ufes.inf.labes.javahostel
```

In my case, I used `br.ufes.inf.labes` as the group ID, `javahostel` as the artifact ID, accepted the default value for version and used `br.ufes.inf.labes.javahostel` as package. This creates a `javahostel` folder for our project with the following structure:

```
javahostel/
    |- pom.xml
    |- src/
        |- main/
        |   |- java/
        |       |- br/ufes/inf/labes/javahostel/App.java
        |- test/
            |- java/
                |- br/ufes/inf/labes/javahostel/AppTest.java
```

This is not, however, a complete Maven project structure, just the simplest one that works. When you run Maven, though, it will print warnings saying some folders are missing. Let's go ahead, then, and create these folders and also delete the `App.java` and `AppTest.java` classes we don't actually need. This should be the result:

```
javahostel/
    |- pom.xml
    |- src/
        |- main/
        |   |- java/
        |       |- br/ufes/inf/labes/javahostel/
        |   |- resources/
        |   |- webapp/
        |- test/
            |- java/
            |   |- br/ufes/inf/labes/javahostel/
        |   |- resources/
```

Quick introduction to Maven project structures, if you're not used to them:

- `pom.xml`: the Project Object Model, a file that contains information about our project and that tells Maven how to behave when building our software;

- `src/main`: the main source code of our project, divided in three subfolders:

  - `java/`: where we put our Java classes, i.e., the `.java` files;

  - `resources/`: where we put any other types of files (ex.: `.xml`, `.properties`, etc.) that should go on the classpath along the compiled Java classes (i.e., the `.class` files);

  - `webapp/`: the root of our Web application, in which we should put Web pages, images, stylesheets, scripts and the `WEB-INF/` folder that a Java Web application is supposed to have;

- `src/test`: the source code for tests that can be run by Maven as part of the software build process. It usually mirrors the structure of `src/main` providing, e.g., unit tests for the classes in the main source code folder (as it was the case with the `App.java` and `AppTest.java` examples from the archetype we deleted).

That being said, we should now open the `javahostel` folder on VSCode and edit the `pom.xml` file in order to configure:

- The project should be packaged as a WAR (Web Archive) file (`<packaging>`);
- The URL of the project is https://github.com/dwws-ufes/javahostel (optional, `<url>`);
- We will use a Java 17 compiler and virtual machine (`<maven.compiler.release>`, replacing `<maven.compiler.source>` and `<maven.compiler.target>`);
- Maven should not complain if `web.xml` is missing (this file is optional for Jakarta EE, `<failOnMissingWebXml>`);
- Jakarta EE 9's Web API and MySQL Connector/J should be set as a dependencies (`<dependency>`). Note JUnit was removed, as we don't focus on unit tests in this tutorial;
- The final name for the build should be `javahostel` (`<finalName>`).

And this is how the file would look (the contents of `<pluginManagement>` were removed from the code below and should be left unmodified for now, we will talk about it next):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>br.ufes.inf.labes</groupId>
  <artifactId>javahostel</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>war</packaging>

  <name>javahostel</name>
  <url>https://github.com/dwws-ufes/javahostel</url>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.release>17</maven.compiler.release>
    <failOnMissingWebXml>false</failOnMissingWebXml>
  </properties>

  <dependencies>
    <dependency>
      <groupId>jakarta.platform</groupId>
      <artifactId>jakarta.jakartaee-web-api</artifactId>
      <version>9.0.0</version>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>8.0.27</version>
      <scope>runtime</scope>
    </dependency>
  </dependencies>

  <build>
    <finalName>javahostel</finalName>

    <pluginManagement>
		  (Omitted for now, leave it alone at this point)
	  </pluginManagement>
  </build>
</project>
```

There are two more changes to `pom.xml` in order to build and deploy JavaHostel through Maven: add the `maven-war-plugin` to the `<pluginManagement>` list of plug-ins and add and configure the `wildfly-maven-plugin` to a new plug-ins list outside the `<pluginManagement>` tag. The reason for this is that the plug-ins inside the `<pluginManagement>` tag cannot configure executions and we want to configure the WildFly Maven plug-in to automatically execute during some stages of the Maven build. Here's how the `<build>` tag of the `pom.xml` should look like (plug-in versions may vary, you can look for the plug-in websites to see what is the most recent version):

```xml
  <build>
    <finalName>javahostel</finalName>

    <pluginManagement>
      <plugins>
        <plugin>
          <artifactId>maven-clean-plugin</artifactId>
          <version>3.1.0</version>
        </plugin>
        <plugin>
          <artifactId>maven-resources-plugin</artifactId>
          <version>3.0.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-compiler-plugin</artifactId>
          <version>3.8.1</version>
        </plugin>
        <plugin>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>2.22.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-jar-plugin</artifactId>
          <version>3.0.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-install-plugin</artifactId>
          <version>2.5.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-deploy-plugin</artifactId>
          <version>2.8.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-site-plugin</artifactId>
          <version>3.7.1</version>
        </plugin>
        <plugin>
          <artifactId>maven-project-info-reports-plugin</artifactId>
          <version>3.0.0</version>
        </plugin>
        <plugin>
          <artifactId>maven-war-plugin</artifactId>
          <version>3.3.2</version>
        </plugin>
      </plugins>
    </pluginManagement>

    <plugins>
      <plugin>
        <groupId>org.wildfly.plugins</groupId>
        <artifactId>wildfly-maven-plugin</artifactId>
        <version>2.1.0.Beta1</version>
        <executions>
          <!-- Undeploy the application on clean -->
          <execution>
            <id>undeploy</id>
            <phase>clean</phase>
            <goals>
              <goal>undeploy</goal>
            </goals>
            <configuration>
              <ignoreMissingDeployment>true</ignoreMissingDeployment>
            </configuration>
          </execution>
          <!-- Deploy the application on install -->
          <execution>
            <phase>install</phase>
            <goals>
              <goal>deploy</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```

Notice that `wildfly-maven-plugin` is configured to undeploy the application when you run `mvn clean` and deploy the application when you run `mvn install`. We are now ready to actually start coding the JavaHostel.


### Implement the JavaHostel proper:

We start development with a visual template so the website looks good right away. Download a free webiste template from the many free ones available and adjust it accordingly. I used a template called [Linear, by Templated](https://www.themezy.com/free-website-templates/91-minimal-linear-free-responsive-template) and I'll explain next how to adjust it:

1. Under `src/main/webapp/`, create a folder called `resources` and copy the folders `css`, `images` and `js` from the template to this new folder;

2. Again under `src/main/webapp/`, create the folder structure `WEB-INF/templates/` and create a new file called `decorator.xhtml` inside the `templates/` folder;

3. Copy the contents below to the `decorator.xhtml` file, which are based on the downloaded template (more specifically, the `no-sidebar.html` file from the Linear template) but adjusted to become a JSF/Facelets decorator (we'll talk more about it later);

```xhtml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:ui="http://java.sun.com/jsf/facelets"
  xmlns:h="http://java.sun.com/jsf/html"
>
  <h:head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>
      <h:outputText value="JavaHostel :: " /><ui:insert name="title" />
    </title>
    <meta name="description" content="" />
    <meta name="keywords" content="" />
    <link
      href="http://fonts.googleapis.com/css?family=Roboto:400,100,300,700,500,900"
      rel="stylesheet"
      type="text/css"
    />
    <h:outputStylesheet library="css" name="skel-noscript.css" />
    <h:outputStylesheet library="css" name="style.css" />
    <h:outputStylesheet library="css" name="style-desktop.css" />
  </h:head>
  <h:body>
    <!-- Header -->
    <div id="header">
      <div id="nav-wrapper">
        <!-- Nav -->
        <nav id="nav">
          <ul>
            <li>
              <h:link value="Registration" outcome="/registration/index" />
            </li>
            <li><a href="#">Login</a></li>
            <li><a href="#">Book Room</a></li>
            <li><a href="#">Contact Us</a></li>
          </ul>
        </nav>
      </div>
      <div class="container">
        <!-- Logo -->
        <div id="logo">
          <h1><a href="#">JavaHostel</a></h1>
          <span class="tag">Jakarta EE 9 Example WebApp</span>
        </div>
      </div>
    </div>
    <!-- Header -->

    <!-- Main -->
    <div id="main">
      <div id="content" class="container">
        <ui:insert name="contents">Blank page.</ui:insert>
      </div>
    </div>
    <!-- /Main -->

    <!-- Tweet -->
    <div id="tweet">
      <div class="container">
        <section>
          <blockquote>
            &ldquo;In posuere eleifend odio. Quisque semper augue mattis wisi.
            Maecenas ligula. Pellentesque viverra vulputate enim. Aliquam erat
            volutpat.&rdquo;
          </blockquote>
        </section>
      </div>
    </div>
    <!-- /Tweet -->

    <!-- Footer -->
    <div id="footer">
      <div class="container">
        <section>
          <header>
            <h2>Get in touch</h2>
            <span class="byline"
              >Integer sit amet pede vel arcu aliquet pretium</span
            >
          </header>
          <ul class="contact">
            <li>
              <a href="#" class="fa fa-twitter"><span>Twitter</span></a>
            </li>
            <li class="active">
              <a href="#" class="fa fa-facebook"><span>Facebook</span></a>
            </li>
            <li>
              <a href="#" class="fa fa-dribbble"><span>Pinterest</span></a>
            </li>
            <li>
              <a href="#" class="fa fa-tumblr"><span>Google+</span></a>
            </li>
          </ul>
        </section>
      </div>
    </div>
    <!-- /Footer -->

    <!-- Copyright -->
    <div id="copyright">
      <div class="container">
        Design: <a href="http://templated.co">TEMPLATED</a> Images:
        <a href="http://unsplash.com">Unsplash</a> (<a
          href="http://unsplash.com/cc0"
          >CC0</a
        >)
      </div>
    </div>
  </h:body>
</html>
```

4. A small adjustment also needs to be made in the `src/main/webapp/resources/css/style.css` file, replacing relative URL references that start with `url(../` to start with `url(#{request.contextPath}/resources/` instead;

5. Now create the home page `index.xhtml` under `src/main/webapp/` with the following contents:

```xhtml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<ui:composition
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:ui="http://java.sun.com/jsf/facelets"
  xmlns:f="http://java.sun.com/jsf/core"
  xmlns:h="http://java.sun.com/jsf/html"
  template="/WEB-INF/templates/decorator.xhtml"
>
  <ui:define name="title">Welcome</ui:define>
  <ui:define name="contents">
    <section>
      <header>
        <h2>Welcome to JavaHostel</h2>
      </header>
    </section>

    <p>Under development.</p>
  </ui:define>
</ui:composition>
```

6. Then, create the Web application configuration file `src/main/webapp/WEB-INF/web.xml` with the following contents:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd" version="5.0">
	<display-name>JavaHostel</display-name>
	<welcome-file-list>
		<welcome-file>index.xhtml</welcome-file>
	</welcome-file-list>
	<servlet>
		<servlet-name>Faces Servlet</servlet-name>
		<servlet-class>jakarta.faces.webapp.FacesServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>Faces Servlet</servlet-name>
		<url-pattern>*.xhtml</url-pattern>
	</servlet-mapping>
	<context-param>
		<param-name>jakarta.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE</param-name>
		<param-value>true</param-value>
	</context-param>
    <data-source>
        <name>java:app/datasources/javahostel</name>
        <class-name>com.mysql.cj.jdbc.MysqlDataSource</class-name>
        <server-name>localhost</server-name>
        <port-number>3306</port-number>
        <database-name>javahostel</database-name>
        <user>dwws</user>
        <password><![CDATA[dwws]]></password>
    </data-source>
</web-app>
```

7. Finally, if we want to test the application (see instructions in the "Deploy the JavaHostel" section next), we also need the CDI configuration file `src/main/webapp/WEB-INF/beans.xml` with the following contents (it's basically empty):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="https://jakarta.ee/xml/ns/jakartaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/beans_3_0.xsd" version="3.0" bean-discovery-mode="annotated">
</beans>
```

8. Unlike the CDI configuration, this last step is not really necessary (this simple application will work without this), but if you want you can create an empty JSF configuration file `src/main/webapp/WEB-INF/faces-config.xml` so when you need to configure anything in JSF it's already there:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<faces-config xmlns="https://jakarta.ee/xml/ns/jakartaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-facesconfig_3_0.xsd" version="3.0">
</faces-config>
```

> **Explaining the code:**
>
> The original Linear template had to be adjusted in several points:
>
> - The headers were replaced so it would become an XHTML file, which is the preferred format for web pages when using JSF (some XML tags are placed at the top and the XML namespace attributes pointing to not only XHTML but also JSF schemas are added to the root `<html>` tag);
> - The HTML tags `<body>` and `<head>` are replaced by JSF tags `<h:body>` and `<h:head>`. This allows JSF to include code that is needed for it to do its job;
> - The contents of `<title>` is replaced by a JSF `<h:outputText>` tag with a common prefix for all pages of the application plus a Facelets `<ui:insert>` tag that gets replaced by contents that come from the actual web pages (we will see that later);
> - References to scritps and stylesheets in HTML (i.e., `<script>` and `<link>` tags) were replaced by their JSF counterparts `<h:outputScript>` and `<h:outputStylesheet>`. This is necessary because the decorator can be used by pages in all levels of your application (e.g., by `index.xhtml` at the root or by `registration/success.xhtml` which is under a folder). Hence, relative references such as `href="css/style.css"` only work at the root level. The JSF tags adjust that automatically;
> - The _lorem ipsum_ text at the _main_ part of the template was replaced by another `<ui:insert>` tag that allows the actual web pages to insert their contents. Notice that this time some default contents (_Blank page._) were provided;
> - The header at the _logo_ part of the template was also adjusted, as well as the menu at the _nav_ part. A link to a _Registration_ page was already included and will be used later;
> - Finally, for the same reason as the JSF script/stylesheet tags before, relative `../` references in `style.css` had to be replaced with absolute references `#{request.contextPath}/resources/`, in which JSF replaces `#{request.contextPath}` with the full URL of the root of the web application (e.g., `http://localhost:8080/javahostel/`).
>
> With the template ready, a simple home page was created in `index.xhtml`, again with the XML headers but, this time, the root tag is not `<html>` but `<ui:composition>`, which is a Facelets tag. The Facelets namespace is included in this tag, as well as a reference to the decorator, under the `template` attribute. Then, the sections of the template (defined with the `<ui:insert>` we saw earlier) are filled in with the contents of the `<ui:define>` tags using the same names (`title` and `contents`) defined in the decorator.
>
> Next, the `WEB-INF/web.xml` file is provided indicating basically four things:
>
> 1. The `index.xhtml` page should be offered whenever the browser asks for a folder (e.g., `http://localhost:8080/javahostel/` actually opens `http://localhost:8080/javahostel/index.xhtml`);
> 2. JSF, through the `FacesServlet` class should handle all requests that end in `.xhtml`;
> 3. All date/time conversions should follow the local timezone instead of the default GMT timezone (see [the JSF specification](https://jakarta.ee/specifications/faces/3.0/jakarta-faces-3.0.html#a6088)). This last configuration is necessary, otherwise the birthdate of the guest registration feature always gets modified to the day before due to the use of the wrong timezone (go figure!);
> 4. A data source called `java:app/datasources/javahostel` over the MySQL database we created should be configured in the application server when the application is deployed.
>
> Lastly, we provided an empty `beans.xml` configuration so that CDI will behave properly. If this file is not present, WildFly will refuse to deploy the application, saying that JSF is _"Unable to find CDI BeanManager"_ (`FacesException`). As we can deduce, JSF depends on CDI to work properly. Funny enough, the JSF configuration file `faces-config.xml` is not mandatory.

Next, we configure the object/relational mapping with JPA and create the domain classes:

1. Under `src/main/resources/`, create a folder called `META-INF` and a file called `persistence.xml` inside it, with the following contents:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd" version="3.0">
	<persistence-unit name="JavaHostel" transaction-type="JTA">
		<provider>org.hibernate.ejb.HibernatePersistence</provider>
		<jta-data-source>java:app/datasources/javahostel</jta-data-source>

		<properties>
			<property name="hibernate.hbm2ddl.auto" value="update"/>
      <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect" />
		</properties>
	</persistence-unit>
</persistence>
```

2. Then, under `src/main/java/br/ufes/inf/labes/javahostel/`, create the `domain` folder and implement the following Java classes inside it (getter and setter methods were replaced by a `/* Getter and setters here. */` comment for brevity. Also, getters and setters are implemented for all attributes to simplify the example. In a real application you should evaluate which get/set methods make sense for your domain):

```java
package br.ufes.inf.labes.javahostel.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Bed {
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private Room room;

	private int number;

	private double pricePerNight;

    /* Getter and setters here. */
}
```

```java
package br.ufes.inf.labes.javahostel.domain;

import java.util.Date;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Booking {
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private Guest guest;

	@OneToMany
	private Set<Bed> beds;

	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Temporal(TemporalType.DATE)
	private Date endDate;

    /* Getter and setters here. */
}
```

```java
package br.ufes.inf.labes.javahostel.domain;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Guest {
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private String email;

	private String password;

	@Temporal(TemporalType.DATE)
	private Date birthDate;

	/* Getter and setters here. */
}
```

```java
package br.ufes.inf.labes.javahostel.domain;

import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Room {
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private int number;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "room")
	private Set<Bed> beds;

    /* Getter and setters here. */
}
```

Again, if you want to deploy the partial application now (see instructions in the "Deploy the JavaHostel" section next), if all goes well you will see that JPA will create tables automatically for you in the `javahostel` database. Open the MySQL Workbench and check it out.

> **Explaining the code:**
>
> The `META-INF/persistence.xml` file tells the application server that we will use JPA. It (1) defines a persistence unit for our application; (2) indicates Hibernate as the persistence provider (WildFly comes with it bundled, if you use another application server you should find out which is the local JPA provider or add it to the server yourself); (3) points to the data source we configured earlier in `web.xml`; (4) configures Hibernate to automatically generate tables to our persistent classes in the database and update such tables if the classes are modified (`hibernate.hbm2ddl.auto = update`); and (5) indicates the RDBMS dialect Hibernate should use, i.e., tells it to adjust the SQL queries and commands to the particular database system being used (`<property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect" />`).
>
> That being done, the classes can be implemented and receive persistence annotations such as `@Entity` (indicates the class is persistent), `@Id` (the primary key), `@GeneratedValue(strategy = GenerationType.AUTO)` (primary keys should be generated automatically by JPA or the database) and so on. Explaining all the JPA mappings is out of the scope of this tutorial.

Finally, we implement one of the features of our application: a simple guest registration. A `<h:link value="Registration" outcome="/registration/index" />` has already been included in the `decorator.xhtml` template, so we conclude the example as follows:

1. Create the `src/main/webapp/registration/` folder, make a copy of the `index.xhtml` home page inside the new folder, and replace the contents of the `<ui:define name="contents">` tag with the following:

```xhtml
<section>
  <header>
    <h2>Registration</h2>
  </header>
</section>

<p>Fill in your data to become a guest at Java Hostel.</p>

<h:form id="regForm">
  <p>
    Name: <h:inputText id="name" value="#{registrationController.guest.name}" />
  </p>

  <p>
    Birthdate:
    <h:inputText
      id="birthDate"
      value="#{registrationController.guest.birthDate}"
    >
      <f:convertDateTime pattern="dd/MM/yyyy" />
    </h:inputText>
  </p>

  <p>
    E-mail:
    <h:inputText id="email" value="#{registrationController.guest.email}" />
  </p>

  <p>
    Password:
    <h:inputSecret
      id="password"
      value="#{registrationController.guest.password}"
    />
  </p>

  <p>
    <h:commandButton
      action="#{registrationController.register}"
      value="Register"
    />
  </p>
</h:form>
```

2. Under `src/main/java/br/ufes/inf/labes/javahostel/`, create the `control` folder and implement the following Java class (the controller):

```java
package br.ufes.inf.labes.javahostel.control;

import java.io.Serializable;
import jakarta.ejb.EJB;
import jakarta.enterprise.inject.Model;
import br.ufes.inf.labes.javahostel.application.RegistrationService;
import br.ufes.inf.labes.javahostel.application.UnderAgeGuestException;
import br.ufes.inf.labes.javahostel.domain.Guest;

@Model
public class RegistrationController implements Serializable {
	@EJB
	private RegistrationService registrationService;
	private Guest guest = new Guest();
	private int age;

	public Guest getGuest() {
		return guest;
	}

	public int getAge() {
		return age;
	}

	public String register() {
		try {
			registrationService.register(guest);
		} catch (UnderAgeGuestException e) {
			age = e.getAge();
			return "/registration/underage.xhtml";
		}
		return "/registration/success.xhtml";
	}

}
```

3. Under `src/main/java/br/ufes/inf/labes/javahostel/`, create the `application` folder and implement the following Java classes (the service and the exception):

```java
package br.ufes.inf.labes.javahostel.application;

import java.util.Calendar;
import java.util.Date;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import br.ufes.inf.labes.javahostel.domain.Guest;

@Stateless
@LocalBean
public class RegistrationService {
	@PersistenceContext
	private EntityManager entityManager;

	public void register(Guest guest) throws UnderAgeGuestException {
		int age = calculateAge(guest.getBirthDate());
		if (age < 18)
			throw new UnderAgeGuestException(age);
		entityManager.persist(guest);
	}

	private static int calculateAge(Date birthDate) {
		if (birthDate == null)
			return 0;
		Calendar birth = Calendar.getInstance();
		birth.setTime(birthDate);
		Calendar today = Calendar.getInstance();
		today.setTime(new Date(System.currentTimeMillis()));
		int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
		birth.add(Calendar.YEAR, age);
		if (birth.after(today))
			age--;
		return age;
	}

}
```

```java
package br.ufes.inf.labes.javahostel.application;

public class UnderAgeGuestException extends Exception {
	private int age;

	public UnderAgeGuestException(int age) { this. age = age; }

	public int getAge() { return age; }

}
```

4. Back to `src/main/webapp/registration/`, create the pages for the two possible outcomes of the guest registration feature: `success.xhtml` and `underage.xhtml`. Again, make a copy of `index.xhtml` and replace only the contents section:

```xhtml
<section>
  <header>
    <h2>Registration</h2>
  </header>
</section>

<p>
  Dear <h:outputText value="#{registrationController.guest.name}" />, welcome to
  JavaHostel.
</p>
```

```xhtml
<section>
  <header>
    <h2>Registration</h2>
  </header>
</section>

<p>
  Dear <h:outputText value="#{registrationController.guest.name}" />,
  unfortunately underage people are not allowed to register as guests and,
  according to your birth date, you have only
  <h:outputText value="#{registrationController.age}" /> years.
</p>
```

> **Explaining the code:**
>
> As the `/registration/index` outcome of the `<h:link>` is followed, JSF renders the `src/main/webapp/registration/index.xhtml` page and shows the registration form. The form fields are bound to attributes of the `Guest` object that is itself an attribute of the `RegistrationController` object which is automatically created for us because of the `@Model` annotation over the controller class. Such annotation is equivalent of having both `@Named` (which gives `RegistrationController` the name `registrationController`) and `@RequestScoped` (which makes CDI create a new object of this class at every HTTP request). Thus, our JSF page can use _Expression Language_ (EL) terms such as `#{registrationController.guest.name}` to mean that:
>
> - When the page is rendered, CDI will provide JSF with an instance of the controller bound to the request scope, so JSF can call `getGuest()` in that instance to obtain the `Guest` object and then call `getName()` and fill in the form field with the value (which in our case is null/empty);
> - When the form is filled and then submitted, JSF will again ask CDI for the request-bound instance of the controller, call `getGuest()` to obtain the `Guest` object and now call `setName()` on that object, passing the string that was filled in the form as parameter;
> - At every request, CDI will create a new instance of the controller upon the first mention to its EL name (i.e., `#{registrationController}`) and throughout the entire HTTP request, the same object is returned everytime it is referenced in an EL expression. When the request ends, the object is discarded. CDI has other scopes, but that is out of the scope (pun intended!) of this tutorial.
>
> Other than sending the guest data to the `Guest` instance of the controller, a click on the _Register_ `<h:commandButton>` triggers JSF to call the `register()` method in that same controller object. The controller then calls the `register()` method in the `RegistrationService` object that is one of its attributes, passing the `Guest` object as parameter. Note that even though we never initialized the `registrationService` attribute, a `NullPointerException` doesn't happen here because the `@EJB` annotation tells CDI to look for an _Enterprise Bean_ that matches the `RegistrationService` type (in this case, it's the class itself, but the reference could be of an interface type) and inject it there for us.
>
> The `RegistrationService` class is annotated with `@Stateless`, meaning it's a stateless enterprise bean (i.e., doesn't have attributes that hold information, just references to other objects that are also injected by CDI), and `@LocalBean`, meaning it doesn't need an EJB interface. This simplifies the example, but in other situations separating the enterprise bean in interface and implementation should be useful. The `register()` method here calculates the guest's age using another method, then if the age is under 18 an exception is thrown. Otherwise, the `entityManager` attribute is used to tell JPA to persist the `Guest` object received as parameter. Again, that attribute is not initialized by us, but instead by CDI through the `@PersistenceContext` annotation.
>
> Back to the controller, if it catches the exception thrown in the case of underage guests, it extracts the age from the exception object, places it in the `age` attribute of the controller and returns the string `"/registration/underage.xhtml"`, which tells JSF to render that page. If all goes well, on the other hand, it returns `"/registration/success.xhtml"`. Both pages use the `<h:outputText>` JSF tag to compose messages that are shown to the user getting data from the controller. In the `underage.xhtml` page, for example, both the name and the age of the guest are used in the message.
>
> This simple feature shows the entire flow that starts in the view (the web pages), is mediated by the controller, which calls services in the business layer, that in turn send the data to the data access layer for persistence and finally come back to the view for displaying the results. Facelets provide a common decorator for all pages, JSF handles web pages and controllers, CDI manage all the objects in their respective scopes and satisfy their interdependencies for us, finally JPA provide object/relational mapping to persist the objects in the relational database.

You can now deploy and test the new feature, checking if the data was inserted in the database using the MySQL Workbench afterwards.


## Deploy the JavaHostel:

Once we've finished the code (or if you just cloned the repository), we can deploy and try it. First, we need to run the WildFly server, so open a console and run the `$WILDFLY_HOME/standalone.sh` script (`standalone.bat` on Windows, I guess). At some point the WildFly log that is displayed in the console should show something like this:

```console
11:26:38,917 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0025: WildFly Preview 25.0.1.Final (WildFly Core 17.0.3.Final) started in 6470ms - Started 313 of 553 services (339 services are lazy, passive or on-demand)
11:26:38,921 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0060: Http management interface listening on http://127.0.0.1:9990/management
11:26:38,922 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0051: Admin console listening on http://127.0.0.1:9990
```

Now, to deploy the application manually, you could run `mvn package` (under the `javahostel` project folder), so Maven builds the `target/javahostel.war` file. Then, copy that file to `$WILDFLY_HOME/standalone/deployments/` and watch the WidlFly log. If all goes well, you should see a `javahostel.war.deployed` file under `$WILDFLY_HOME/standalone/deployments/`. If you rename that file to `javahostel.war.undeploy`, WildFly will undeploy it. If you rename back to `javahostel.war.dodeploy`, it will deploy it again. Quite simple.

However, we can have Maven perform the deploy, as we configured it earlier. When you run `mvn install`, the WildFly Maven plug-in will use a management API to deploy your application (it will not show in `$WILDFLY_HOME/standalone/deployments/`). The same will happen if you run `mvn wildfly:deploy` directly. On the other hand, if you run `mvn clean` (or `mvn wildfly:undeploy` directly), WildFly will undeploy your application. You can also run `mvn wildfly:redeploy` to update your deployment in case you make changes to the source code and wants to try them.



## Future Work:

Here are some things that I would still like to do in order to improve this example:

- Update the code to use features from the more recent versions of Java. The original code was created with Java 7 or some version around that, but there are many new features in more recent versions that could make the code better;

- Configure Docker to deploy the example in an image that is already ready-to-go with Java, WildFly and MySQL, so all one would need to try the example is Git, Maven and Java.
