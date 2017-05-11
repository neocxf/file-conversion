"use strict";
var port, server, service,
    address, output, size, pageWidth, pageHeight,
    system = require('system'),
    page = require('webpage').create();

page.onResourceError = function(resourceError) {
    page.reason = resourceError.errorString;
    page.reason_url = resourceError.url;
};

page.onConsoleMessage = function (msg) { // enable the sandbox console
    console.log(msg);
};

// rather than provide the rough 5000 millseconds, we can use some else like monitoring specific .class attribute change
// through https://github.com/ryanmorr/ready
page.settings.resourceTimeout = 5000; // 5 seconds

page.onResourceTimeout = function(e) {
    console.log(e.errorCode);   // it'll probably be 408
    console.log(e.errorString); // it'll probably be 'Network timeout on resource'
    console.log(e.url);         // the url whose request timed out
    // phantom.exit(1);
};

if (system.args.length !== 2) {
    console.log('Usage: phantomjs rasterize-server.js <portnumber>');
    phantom.exit(1);
} else {
    port = system.args[1];
    server = require('webserver').create();

    service = server.listen(port, function (request, response) {

        console.log('Request at ' + new Date());
        console.log(JSON.stringify(request, null, 4));

        var params = JSON.parse(request.post);

        address = params.url;
        output = params.fileName;

        page.viewportSize = { width: 1024, height: 768 };

        if (params.fileName.substr(-4) === ".pdf") {
            size = params.outputSize.split('*');
            page.paperSize = size.length === 2 ? { width: size[0], height: size[1], margin: '1cm', border: '1cm' }
                : { format: params.outputSize, orientation: 'portrait', margin: '1cm' };

        } else if (params.outputSize.substr(-2) === "px") {
            // if the output file type is jpeg or png, just ignore the output size setting, show the whole image

            // size = params.outputSize.split('*');
            // if (size.length === 2) {
            //     pageWidth = parseInt(size[0], 10);
            //     pageHeight = parseInt(size[1], 10);
            //     page.viewportSize = { width: pageWidth, height: pageHeight };
            //     page.clipRect = { top: 0, left: 0, width: pageWidth, height: pageHeight };
            // } else {
            //     console.log("size:", params.outputSize);
            //     pageWidth = parseInt(params.outputSize, 10);
            //     pageHeight = parseInt(pageWidth * 3/4, 10); // it's as good an assumption as any
            //     console.log ("pageHeight:",pageHeight);
            //     page.viewportSize = { width: pageWidth, height: pageHeight };
            // }
        }

        page.zoomFactor = params.zoomFactor;

        // the original impl
        page.open(address, function (status) {
            console.log('Phantomjs server return status: {} ' + status);

            page.evaluate(function (zoom) {
                document.querySelector('body').style.zoom = zoom;
            }, page.zoomFactor);

            response.headers = {
                'Cache': 'no-cache',
                'Content-Type': 'application/json'
            };

            var entity = {};

            if (status !== 'success') {
                console.log('Unable to load the address!');
                // phantom.exit(1);
                console.log(
                    "Error opening url \"" + page.reason_url
                    + "\": " + page.reason
                );

                response.statusCode = 404;

                entity.statusCode = 404;
                entity.msg = "The page you are looking does not exist.";

                response.write(JSON.stringify(entity, null, 4));

                response.close();

            }
            else {

                console.log("address: {} " + address);

                window.setTimeout(function () {
                    var renderSuccess = page.render(output);
                    console.log('render success: ? ' + renderSuccess);
                    renderSuccess = true;
                    if (renderSuccess) {
                        entity.statusCode = 200;
                        entity.msg = " the page with url: " + params.url + " has been successfully rendered";
                        entity.url = params.url;
                        entity.fileName = params.fileName;

                        // normal way of writing json to the end peer
                        response.statusCode = 200;
                        response.write(JSON.stringify(entity, null, 4));
                        response.close();

                    } else {
                        entity.statusCode = 404;
                        entity.msg = " the page with url: " + params.url + " failed rendered";
                        entity.url = params.url;
                        entity.fileName = params.fileName;

                        response.statusCode = 202;
                        response.write(JSON.stringify(entity, null, 4));
                        response.close();
                    }

                }, params.resolveTime);
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