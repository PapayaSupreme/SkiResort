PREREQUISITES :<br>
                - Maven (Hibernate ORM, jakarta, zaxxer, PostgreSQL JDBC)<br>
                  =>in the pom.xml root file<br>
                - Java 23+<br>
                - Ran on Intellij but other works<br>
                - PostgreSQL DB<br><br>
SETUP :<br>
              1. Run db/init.sql (db config)<br>
              2. Setup secrets (db creds and link)<br>
              3. [OPTIONAL] setup SLF4J log providers for Hikari<br>
              4. Run test/App.java<br><br>

===== NOTE ===== <br>
All resort terrain instances are instantiated from the db on every run to allow for ease of use<br>
All Persons / Passes instances are only queried or inserted for greater performance<br><br>
For better Structure insights, see **structure.txt**

**===== CONTACT =====**<br>
**This project is made by me, for any enquiries :** pablo.ferreiraa10@gmail.com<br><br><br>
