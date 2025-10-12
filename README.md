PREREQUISITES : Maven (Hibernate ORM, jakarta, zaxxer, PostgreSQL JDBC)
                =>in the pom.xml root file
                Java 23+
                Ran on Intellij but other works
                PostgreSQL DB
SETUP :
              1. Run db/init.sql (db config)
              2. Setup secrets (db creds and link)
              3. [OPTIONAL] setup SLF4J log providers for Hikari
              4. Run test/App.java

===== NOTE =====
All resort terrain instances are instantiated from the db on every run to allow for ease of use
All Persons / Passes instances are only queried or inserted for greater performance
This project is made by me, for any enquiries : pablo.ferreiraa10@gmail.com
