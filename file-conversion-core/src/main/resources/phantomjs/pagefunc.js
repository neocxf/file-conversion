var page = require('webpage').create();

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

page.open(
    "http://www.nosuchdomain/",
    function (status) {
        if ( status !== 'success' ) {
            console.log(
                "Error opening url \"" + page.reason_url
                + "\": " + page.reason
            );
            phantom.exit( 1 );
        } else {
            console.log( "Successful page open!" );
            phantom.exit( 0 );
        }
    }
);