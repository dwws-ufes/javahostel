# JavaHostel @ Jakarta EE 9 

In this version, the Java Hostel example was implemented in Jakarta EE 9, using [Visual Studio Code](https://code.visualstudio.com/), [WildFly](https://www.wildfly.org/), [MySQL](https://dev.mysql.com/) and [Maven](https://maven.apache.org/). The following sections guide you through the construction of the code or you can just install/configure the necessary tools (see **Preparation**), clone the repository and run `mvn install`.



## Preparation:

To follow the tutorial below or to clone and run the example, you will need to:

1. Have a Java Development Kit (JDK) that is compatible with the other tools. I used [OpenJDK 17](https://openjdk.java.net/projects/jdk/17/);

2. Have a version of [WildFly](https://www.wildfly.org/downloads/) that is compatible with Jakarte EE 9. At the time of this writing I used _wildfly-preview-25.0.1.Final_;

3. Have [MySQL server](https://dev.mysql.com/downloads/mysql/) (I had version 8.0.23 installed) and its administration front-end, [MySQL Workbench](https://dev.mysql.com/downloads/workbench/) (I had version 8.0.20 installed);

4. Have [Maven](https://maven.apache.org/download.cgi) (I had version 3.8.3 installed);

5. Using MySQL Workbench, create a database schema for Java Hostel and a user with full privileges on it. I created the  `javahostel` schema, with encoding `utf8mb4` and a user `dwws` with password `dwws` with full privileges on `javahostel`. At the time of this writing, [the first part of JButler's tutorial](https://github.com/dwws-ufes/jbutler/wiki/Tutorial00) had more detailed instructions on this, if you need;

6. Download MySQL's Java driver [Connector/J](https://dev.mysql.com/downloads/connector/j/), unpack it and add its JAR file (e.g., `mysql-connector-java-8.0.27.jar`) as a module in WildFly. 

>In order to do this, create the directory structure `com/mysql/main` under `$WIDFLY_HOME/modules` (`$WILDFLY_HOME` being where you unpacked/installed WildFly), place the JAR file there, along with a `module.xml` file that references it using the following syntax (double check if the name of the JAR file matches):
>
>```xml
><?xml version="1.0" encoding="UTF-8"?>
><module xmlns="urn:jboss:module:1.1" name="com.mysql">
>    <resources>
>        <resource-root path="mysql-connector-java-8.0.27.jar"/>
>    </resources>
>    <dependencies>
>        <module name="javax.api"/>
>    </dependencies>
></module>
>```
>
>Then, open the file `$WILDFLY_HOME/standalone/configuration/standalone.xml` and add the driver configuration there, under `<subsystem xmlns="urn:jboss:domain:datasources:6.0"> <drivers>` (version `6.0` can vary, if you search for `com.h2database.h2` you should find the H2 driver configuration, just place the new driver configuration next to it):
>
>```xml
><driver name="mysql" module="com.mysql">
>    <driver-class>com.mysql.cj.jdbc.Driver</driver-class>
></driver>
>```

7. Configure a datasource in WildFly for JavaHostel, with JNDI name `java:jboss/datasources/javahostel`, using the MySQL driver to access the MySQL database schema you created.

>With the same `standalone.xml` still open and again under the `<subsystem xmlns="urn:jboss:domain:datasources:6.0">` configuration, add the following datasource configuration (you can look for the `java:jboss/datasources/ExampleDS` datasource configuration and place next to it, adjust schema/user names and password accordingly):
>
>```xml
><datasource jta="true" jndi-name="java:jboss/datasources/javahostel" pool-name="javahostelPool" enabled="true" use-java-context="true">
>    <connection-url>jdbc:mysql://localhost:3306/javahostel</connection-url>
>    <driver>mysql</driver>
>    <security>
>        <user-name>dwws</user-name>
>        <password>dwws</password>
>    </security>
></datasource>
>```

Steps 6 and 7 above could be automated by Maven and the other steps could be automated by using a Docker container specifically set up with this configuration, if you just want to deploy the example and not develop it yourself. Both are in my plans for future work, but as of now developing/running this example requires the above setup steps.



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

* `pom.xml`: the Project Object Model, a file that contains information about our project and that tells Maven how to behave when building our software;

* `src/main`: the main source code of our project, divided in three subfolders:

    - `java/`: where we put our Java classes, i.e., the `.java` files;

    - `resources/`: where we put any other types of files (ex.: `.xml`, `.properties`, etc.) that should go on the classpath along the compiled Java classes (i.e., the `.class` files);

    - `webapp/`: the root of our Web application, in which we should put Web pages, images, stylesheets, scripts and the `WEB-INF/` folder that a Java Web application is supposed to have;

* `src/test`: the source code for tests that can be run by Maven as part of the software build process. It usually mirrors the structure of `src/main` providing, e.g., unit tests for the classes in the main source code folder (as it was the case with the `App.java` and `AppTest.java` examples from the archetype we deleted).

That being said, we should now open the `javahostel` folder on VSCode and edit the `pom.xml` file in order to configure:

* The project should be packaged as a WAR (Web Archive) file (`<packaging>`);
* The URL of the project is https://github.com/dwws-ufes/javahostel (optional, `<url>`);
* We will use a Java 17 compiler and virtual machine (`<maven.compiler.source>` and `<maven.compiler.target>`);
* Maven should not complain if `web.xml` is missing (this file is optional for Jakarta EE, `<failOnMissingWebXml>`);
* Jakarta EE 9's Web API should be set as a dependency (`<dependency>`);
* The final name for the build should be `javahostel` (`<finalName>`).

And this is how the file would look (the contents of `<pluginManagement>` were removed from the code below and should be left unmodified for now, we will talk about it next):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

    <groupId>br.ufes.inf.labes</groupId>
    <artifactId>javahostel</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <name>javahostel</name>
    <url>https://github.com/dwws-ufes/javahostel</url>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <failOnMissingWebXml>false</failOnMissingWebXml>
    </properties>

    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.11</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>jakarta.platform</groupId>
            <artifactId>jakarta.jakartaee-web-api</artifactId>
            <version>9.0.0</version>
            <scope>provided</scope>
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
                <version>3.8.0</version>
            </plugin>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.22.1</version>
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
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://java.sun.com/jsf/facelets" 
      xmlns:h="http://java.sun.com/jsf/html">
<h:head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title><h:outputText value="JavaHostel :: " /><ui:insert name="title" /></title>
	<meta name="description" content="" />
	<meta name="keywords" content="" />
	<link href='http://fonts.googleapis.com/css?family=Roboto:400,100,300,700,500,900' rel='stylesheet' type='text/css' />
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
						<li><h:link value="Registration" outcome="/registration/index" /></li>
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
					<blockquote>&ldquo;In posuere eleifend odio. Quisque semper augue mattis wisi. Maecenas ligula. Pellentesque viverra vulputate enim. Aliquam erat volutpat.&rdquo;</blockquote>
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
						<span class="byline">Integer sit amet pede vel arcu aliquet pretium</span>
					</header>
					<ul class="contact">
						<li><a href="#" class="fa fa-twitter"><span>Twitter</span></a></li>
						<li class="active"><a href="#" class="fa fa-facebook"><span>Facebook</span></a></li>
						<li><a href="#" class="fa fa-dribbble"><span>Pinterest</span></a></li>
						<li><a href="#" class="fa fa-tumblr"><span>Google+</span></a></li>
					</ul>
				</section>
			</div>
		</div>
	<!-- /Footer -->

	<!-- Copyright -->
		<div id="copyright">
			<div class="container">
				Design: <a href="http://templated.co">TEMPLATED</a> Images: <a href="http://unsplash.com">Unsplash</a> (<a href="http://unsplash.com/cc0">CC0</a>)
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
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
	xmlns:ui="http://java.sun.com/jsf/facelets" 
	xmlns:f="http://java.sun.com/jsf/core" 
	xmlns:h="http://java.sun.com/jsf/html" 
	template="/WEB-INF/templates/decorator.xhtml"> 

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
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://xmlns.jcp.org/xml/ns/javaee"
	xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
	id="WebApp_ID" version="4.0">
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
</web-app>
```

7. Finally, if we want to test the application (see instructions in the "Deploy the JavaHostel" section next), we also need the CDI configuration file `src/main/webapp/WEB-INF/beans.xml` with the following contents:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/beans_2_0.xsd"
       version="2.0" bean-discovery-mode="annotated">
</beans>
```

**TODO: EXPLAIN THE CODE!**

Next, we configure the object/relational mapping with JPA and create the domain classes:

1. Under `src/main/resources/`, create a folder called `META-INF` and a file called `persistence.xml` inside it, with the following contents:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.1" xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
	<persistence-unit name="JavaHostel">
		<provider>org.hibernate.ejb.HibernatePersistence</provider>
		<jta-data-source>java:jboss/datasources/javahostel</jta-data-source>
		<properties>
			<property name="hibernate.hbm2ddl.auto" value="update"/>
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

**TODO: EXPLAIN THE CODE!**

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
    <p>Name: <h:inputText id="name" value="#{registrationController.guest.name}" /></p> 

    <p>Birthdate: <h:inputText id="birthDate" value="#{registrationController.guest.birthDate}"> 
    <f:convertDateTime pattern="dd/MM/yyyy" /> 
    </h:inputText></p> 

    <p>E-mail: <h:inputText id="email" value="#{registrationController.guest.email}" /></p> 

    <p>Password: <h:inputSecret id="password" value="#{registrationController.guest.password}" /></p> 

    <p><h:commandButton action="#{registrationController.register}" value="Register" /></p> 
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

<p>Dear <h:outputText value="#{registrationController.guest.name}" />, welcome to JavaHostel.</p> 
```

```xhtml
<section>
    <header>
        <h2>Registration</h2>
    </header>
</section>

<p>Dear <h:outputText value="#{registrationController.guest.name}" />, unfortunately underage people are not allowed to register as guests and, according to your birth date, you have only <h:outputText value="#{registrationController.age}" /> years.</p> 
```

**TODO: EXPLAIN THE CODE!**

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

* Update the code to use features from the more recent versions of Java. The original code was created with Java 7 or some version around that, but there are many new features in more recent versions that could make the code better;

* [Configure the WildFly Maven plug-in](https://docs.jboss.org/wildfly/plugins/maven/latest/examples/complex-example.html) to add the MySQL driver and create the datasource together with the deployment, so steps 6 and 7 of the preparation are not necessary anymore;

* Configure Docker to deploy the example in an image that is already ready-to-go with Java, WildFly and MySQL, so all one would need to try the example is Git, Maven and Java.