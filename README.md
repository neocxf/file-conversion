# [dsone-pdf-converter](https://git.derbysoft.tm/DHP/dsone-fileserver)

provide html-pdf conversion service

## prerequisite

- maven installed
- docker installed (optional, needed if you run [Docker way](#jar)
- docker-compose installed (same as above)

## How to use

There are two way to use this project. One way is build the project as an war file adn deploy to tomcat container. The second way is deploying in docker container.

### [Traditional way (war deployed)](id:war)
   Make sure that the phantomjs package is installed, and `/usr/local/phantomjs --version` works
```bash
$ git clone https://git.derbysoft.tm/dhp/dsone-fileserver
$ cd fileserver
$ mvn clean install
// ${tomcat} means the actual installation location. ${project.version} means the version of the current build.
// before you run, replace it with acutal information
$ cp dsone-fileserver-web/target/dsone-fileserver-web-${project.version}.war ${tomcat}/webapps
$ service tomcat restart
```    

### [Docker way (jar deployed)](id:jar)
```bash
$ git clone https://git.derbysoft.tm/dhp/dsone-fileserver
$ cd fileserver/docker
$ ./run.sh
```
Finally, open `http://localhost:8091/converter/html/pdf?url=http://www.cnblogs.com` in your browser. You can see that
the website's index page converted as a pdf document.

Of course, you can also make a POST request to `http://localhost:8091/public/transformer/` endpoint, of which the Content-Type 
is `application/json;charset=utf-8`. The body of the POST request is :
```json
{
  "content": "<html>whatever a valid html document source</html>",
  "type": "pdf",
  "fileName": "_sbd23s"
}
```
Also notice that the `type` and `fileName` is optional, though they may have an influence on the performance.
