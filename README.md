This is the source code for the National Land Survey of Finland's
[File service of open data](https://tiedostopalvelu.maanmittauslaitos.fi/tp/kartta?lang=en)
(Avoimien aineistojen tiedostopalvelu).

The following services are implemented:

* (Map interface for open data)
* INSPIRE predefined dataset download service (ATOM) implementation
* Update service for spatial datasets (ATOM), with api key registration
* Admin interface for dataset metadata, files and user permissions
* Statistics for open data downloads

The application has been developed by the National Land Survey of Finland
and released as open source in September 2014.

Note, that the [Oskari Platform](http://www.oskari.org) based map interface (frontend code) is not currently
publicly available.

# License

Dual licensed under MIT/EUPL.

# Requirements

* PostgreSQL 9.x database server
* Java 7 (Java 8 is currently not supported)
* Servlet 2.5/JSP 2.1 compatible servlet container (tested with Apache Tomcat 6.0/7.0)
* JDBC4/4.1 compatible database connection pool (DBCP1.3/Tomcat 6 is too old)
* A proxy server with authentication capabilities (Apache/Nginx)

# Quick start

Clone the repository and build with Apache Maven:

```
git clone https://github.com/nlsfi/file-download-service.git
cd file-download-service
mvn clean install
```

Copy webapp from fileservice-war/target/tp.war to a servlet container.

Create the database:

```
createdb tiepaldb
psql -d tiepaldb -f resources/sql/create-tables.sql
```

Create a JNDI database connection pool with the name ```java:comp/env/jdbc/tiepaldb```

Configure a proxy server for Tomcat (Apache / Nginx), see the example
configuration files for Apache HTTPD in resources/examples/httpd.

Access the admin UI at http://server/tp/hallinta after starting the services.
