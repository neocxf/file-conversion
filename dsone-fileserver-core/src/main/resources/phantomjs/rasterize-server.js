"use strict";
var port, server, service,
    address, output, size, pageWidth, pageHeight,
    system = require('system'),
    page = require('webpage').create();

page.onResourceError = function(resourceError) {
    page.reason = resourceError.errorString;
    page.reason_url = resourceError.url;
};

page.settings.resourceTimeout = 5000; // 5 seconds

page.onResourceTimeout = function(e) {
    console.log(e.errorCode);   // it'll probably be 408
    console.log(e.errorString); // it'll probably be 'Network timeout on resource'
    console.log(e.url);         // the url whose request timed out
    // phantom.exit(1);
};


if (system.args.length !== 2) {
    console.log('Usage: simpleserver.js <portnumber>');
    phantom.exit(1);
} else {
    port = system.args[1];
    server = require('webserver').create();

    service = server.listen(port, function (request, response) {

        console.log('Request at ' + new Date());
        console.log(JSON.stringify(request, null, 4));

        var params = JSON.parse(request.post);

        console.log(params.url);
        console.log(params.fileName);
        console.log(params.outputSize);
        console.log(params.zoom);


        address = params.url;
        output = params.fileName;
        page.viewportSize = { width: 600, height: 600 };
        if (params.fileName.substr(-4) === ".pdf") {
            size = params.outputSize.split('*');
            page.paperSize = size.length === 2 ? { width: size[0], height: size[1], margin: '0px' }
                : { format: params.outputSize, orientation: 'portrait', margin: '1cm' };
        } else if (params.outputSize.substr(-2) === "px") {
            size = params.outputSize.split('*');
            if (size.length === 2) {
                pageWidth = parseInt(size[0], 10);
                pageHeight = parseInt(size[1], 10);
                page.viewportSize = { width: pageWidth, height: pageHeight };
                page.clipRect = { top: 0, left: 0, width: pageWidth, height: pageHeight };
            } else {
                console.log("size:", params.outputSize);
                pageWidth = parseInt(params.outputSize, 10);
                pageHeight = parseInt(pageWidth * 3/4, 10); // it's as good an assumption as any
                console.log ("pageHeight:",pageHeight);
                page.viewportSize = { width: pageWidth, height: pageHeight };
            }
        }

        page.zoomFactor = params.zoom;

        // the original impl
        page.open(address, function (status) {
            console.log('Phantomjs server return status: {} ' + status);

            response.headers = {
                'Cache': 'no-cache',
                'Content-Type': 'text/html'
            };

            if (status !== 'success') {
                console.log('Unable to load the address!');
                // phantom.exit(1);
                console.log(
                    "Error opening url \"" + page.reason_url
                    + "\": " + page.reason
                );

                response.statusCode = 408;

                response.write('<html>');
                response.write('<head>');
                response.write('<title>Error Page</title>');
                response.write('</head>');
                response.write('<body>');
                response.write('<p>The page you are looking does not exist.</p>');
                response.write('</body>');
                response.write('</html>');

                response.close();
            }
            else {

                console.log("address: {} " + address);

                window.setTimeout(function () {
                    page.render(output);
                    // phantom.exit();

                    response.statusCode = 200;

                    response.write('<html>');
                    response.write('<head>');
                    response.write('<title>Hello, world!</title>');
                    response.write('</head>');
                    response.write('<body>');
                    response.write('<p>This is from PhantomJS web server.</p>');
                    response.write('<p>Request data:</p>');
                    response.write('<pre>');
                    response.write(JSON.stringify(request, null, 4));
                    response.write('</pre>');
                    response.write('</body>');
                    response.write('</html>');
                    response.close();


                }, 200);
            }
        });



    });

    if (service) {
        console.log('Web server running on port ' + port + ', status: READY');
    } else {
        console.log('Error: Could not create web server listening on port ' + port + ', status: FAIL');
        phantom.exit();
    }
}