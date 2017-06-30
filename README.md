# [dsone-pdf-converter](https://git.derbysoft.tm/DHP/dsone-fileserver)

provide html-pdf, html-png, html-jpeg conversion service 

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
  "fileName": "_sbd23s",
  "resolveTime": "200"
}
```
Also notice that the `type`, `fileName` and `resolveTime` is optional, `type` have three possible types: pdf/jpeg/png;
`resolveTime` has a default time of 200 (notice that this is the **suggestion** time, other than ajax dynamic generation of html page, 
 you should not configure this parameter).

## Integrate with existed application
  
  Refer the dsone-fileserver-client for reference. It contains a small use case for integrating.

- import dsone-fileserver-config module in your main pom.xml
```xml
    <dependency>
        <groupId>com.derbysoft.dhp</groupId>
        <artifactId>dsone-fileserver-config</artifactId>
        <version>1.1.0</version>
    </dependency>
```

- enable pdf-converter annotation
```java
    @Configuration
    @EnablePdfConverter
    public class PdfConverterClientConfig {
    
    }  
```
- make the actual conversion request to remote conversion service
```java
    @Controller
    public class HomeController {
    
        @Autowired
        private HttpClientAdapter httpClientAdapter;
        
        // generate a pdf file based on the html
        @RequestMapping(value = "/output-report", method = RequestMethod.GET)
        public void outputReport(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            // get the actual generated html source file 
            String content = "<html>whatever a valid html document source</html>";
            List<NameValuePair> reqParams = new ArrayList<>();
            reqParams.add(new BasicNameValuePair("fileName", String.valueOf(System.currentTimeMillis())));
            reqParams.add(new BasicNameValuePair("content", content));
    
            httpClientAdapter.handlePostRequest(remoteEnvArgs.getPdfTransformerAddr(), reqParams, new AbstractResponseHandler<Object>() {
                @Override
                public Object handleEntity(HttpEntity entity) throws IOException {
                    entity.writeTo(resp.getOutputStream());
                    return null;
                }
            });
        }
    }
```

## Debug help

  Backendly, all the transformation requests are proxied to the embeded Phantomjs server. So you can use several commands to test the transformation

### Parameters meaning
* url

html's absolute path

* fileName

pdf, jpg or png's name

* outputSize

transform file's size. Available size is : A4, A3, A5, 1920px*1024px, 1024px*768px, 1366px*768px, 1368px*1024px

* resolveTime

phantomjs's max resolve time. Currently used for Ajax request page rendering

* zoom

zoom factor, which is taken from phantomjs's website. Currently not employeed

### How to debug
- start the phantomjs server
```bash
    phantomjs rasterize-server.js 8080   
```

- test the transformation
```bash
    curl -X POST -H "Content-Type: application/json" -d '{"url":"./_report_20170510.html","fileName":"a.pdf","outputSize":"A4","zoomFactor":"0.52","resolveTime":200}' localhost:8080
```
   Notice that, you should provide the absolute transforming path of html file as the ***url*** parameter