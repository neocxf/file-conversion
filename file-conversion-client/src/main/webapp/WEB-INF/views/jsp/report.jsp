
<html>
<head>
    <meta charset="UTF-8"/>
    <!--meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; maximum-scale=1.0;" /-->
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta http-equiv="X-UA-Compatible" content="IE=9"/>
    <title>dsOne</title>

    <link rel="shortcut icon" href="http://52.77.155.15/dsone/static/dsone/images/dsOne-favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="http://52.77.155.15/dsone/static/css/bootstrap/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="http://52.77.155.15/dsone/static/css/libs/font-awesome.css"/>
    <link rel="stylesheet" type="text/css" href="http://52.77.155.15/dsone/static/dsone/css/compiled_en/theme_styles.css"/>
    <link rel="stylesheet" type="text/css" href="http://52.77.155.15/dsone/static/dsone/css/compiled_en/service.css"/>
    <link rel="stylesheet" type="text/css" href="http://52.77.155.15/dsone/static/dsone/css/compiled_en/report.css"/>
    
        
        
        
        
        
    
</head>
<body class="print-body">
<div class="content-wd690">
    <div class="booking-reports-form">
        <div class="booking-reports print-reports">

            <div class="custom-reports-input custom-reports-mt25 custom-title">
                <h2>Occupancy trends report</h2>
            </div>

            <div class="custom-reports-checkbox custom-reports-mt25 custom-reports-bordertop" id="paramDiv">
                
                
                
                
                
                
                
                
                
                
                
                
                <div class="checkbox-block checkbox-fr" id="viewChkDiv" style="display: none;">
                    
                        <label class="checkbox">
                            <p>List view</p>
                            <input type="checkbox" id="listType" name="showStype" value="list" checked="" onclick="ShowHideView(this, 'occupancy');"/>
                            <i></i>
                        </label>

                        <label class="checkbox checkbox-ml20">
                            <p>Chart view</p>
                            <input type="checkbox" id="chartType" name="showStype" value="chat" checked="" onclick="ShowHideView(this, 'occupancy');"/>
                            <i></i>
                        </label>
                    
                </div>
                <div class="cancellation-trends-clear" id="compareOccupancyReport" style="display: none;">
                    <div class="cancellation-trends">
                        <div class="reports-checkbox-stay" id="occupancy-title1" name="printHeaderDiv"><p id="divP1"><i>hotel maxl</i></p><p id="divP2"><span>Today:</span><i>14 October 2016</i></p>
                        </div>
                        <div class="reports-form-table">
                            <div class="reports-progress-bar" id="occupancy-charView1" style="width: 300px; height: 300px; overflow:inherit;"></div><br/><br/>
                            <table width="316px" id="occupancy-table1"></table>
                        </div>
                    </div>
                    <div class="cancellation-trends reports-checkbox-ml20">
                        <div class="reports-checkbox-stay" id="occupancy-title2" name="printHeaderDiv"><p id="divP1"><i>hotel maxl</i></p><p id="divP2"><span>Today:</span><i>14 October 2016</i></p>
                        </div>
                        <div class="reports-form-table">
                            <div class="reports-progress-bar" id="occupancy-charView2" style="width: 300px; height: 300px; overflow:inherit;"></div><br/><br/>
                            <table width="315px" id="occupancy-table2"></table>
                        </div>
                    </div>
                </div>

                <div id="commonOccupancyReport" >
                    <div class="reports-checkbox-stay" id="s-occupancy-title" name="printHeaderDiv"><p id="divP1"><i>hotel maxl</i></p><p id="divP2"><span>Today:</span><i>14 October 2016</i></p><p class="mr-40"><span>Date of stay:</span><i>14 Oct 2016</i><span> to </span><i>27 Oct 2016</i></p></div>
                    <div class="reports-form-table">
                        <div class="reports-progress-bar" id="s-occupancy-charView" style="width: 500px; height: 400px; overflow:inherit;" data-highcharts-chart="0"><div class="highcharts-container" id="highcharts-0" style="position: relative; overflow: hidden; width: 500px; height: 400px; text-align: left; line-height: normal; z-index: 0; -webkit-tap-highlight-color: rgba(0, 0, 0, 0);"><svg version="1.1" style="display:block;font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Arial, Helvetica, sans-serif;font-size:12px;" xmlns="http://www.w3.org/2000/svg" width="500" height="400"><desc>Created with Highcharts 4.2.1</desc><defs><clipPath id="highcharts-1"><rect x="0" y="0" width="446" height="349"></rect></clipPath></defs><rect x="0" y="0" width="500" height="400" strokeWidth="0" fill="#FFFFFF" class=" highcharts-background"></rect><g class="highcharts-grid" zIndex="1"></g><g class="highcharts-grid" zIndex="1"><path fill="none" d="M 44 359.5 L 490 359.5" stroke="#D8D8D8" stroke-width="1" zIndex="1" opacity="1"></path><path fill="none" d="M 44 301.5 L 490 301.5" stroke="#D8D8D8" stroke-width="1" zIndex="1" opacity="1"></path><path fill="none" d="M 44 243.5 L 490 243.5" stroke="#D8D8D8" stroke-width="1" zIndex="1" opacity="1"></path><path fill="none" d="M 44 184.5 L 490 184.5" stroke="#D8D8D8" stroke-width="1" zIndex="1" opacity="1"></path><path fill="none" d="M 44 126.5 L 490 126.5" stroke="#D8D8D8" stroke-width="1" zIndex="1" opacity="1"></path><path fill="none" d="M 44 68.5 L 490 68.5" stroke="#D8D8D8" stroke-width="1" zIndex="1" opacity="1"></path><path fill="none" d="M 44 10.5 L 490 10.5" stroke="#D8D8D8" stroke-width="1" zIndex="1" opacity="1"></path></g><g class="highcharts-axis" zIndex="2"><path fill="none" d="M 75.5 359 L 75.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 107.5 359 L 107.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 139.5 359 L 139.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 170.5 359 L 170.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 202.5 359 L 202.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 234.5 359 L 234.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 266.5 359 L 266.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 298.5 359 L 298.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 330.5 359 L 330.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 362.5 359 L 362.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 393.5 359 L 393.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 425.5 359 L 425.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 457.5 359 L 457.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 490.5 359 L 490.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 43.5 359 L 43.5 369" stroke="white" stroke-width="1" opacity="1"></path><path fill="none" d="M 44 359.5 L 490 359.5" stroke="#C0D0E0" stroke-width="1" zIndex="7"></path></g><g class="highcharts-axis" zIndex="2"></g><g class="highcharts-series-group" zIndex="3"><g class="highcharts-series highcharts-series-0" zIndex="0.1" transform="translate(44,10) scale(1 1)" clip-path="url(#highcharts-1)"><path fill="none" d="M 15.928571428571429 349 L 47.785714285714285 349 L 79.64285714285714 349 L 111.5 349 L 143.35714285714286 349 L 175.2142857142857 349 L 207.07142857142856 349 L 238.92857142857142 349 L 270.7857142857143 58.16666666666663 L 302.64285714285717 349 L 334.5 349 L 366.3571428571429 58.16666666666663 L 398.2142857142857 349 L 430.0714285714286 349" stroke="#7cb5ec" stroke-width="2" zIndex="1" stroke-linejoin="round" stroke-linecap="round"></path><path fill="none" d="M 5.928571428571429 349 L 15.928571428571429 349 L 47.785714285714285 349 L 79.64285714285714 349 L 111.5 349 L 143.35714285714286 349 L 175.2142857142857 349 L 207.07142857142856 349 L 238.92857142857142 349 L 270.7857142857143 58.16666666666663 L 302.64285714285717 349 L 334.5 349 L 366.3571428571429 58.16666666666663 L 398.2142857142857 349 L 430.0714285714286 349 L 440.0714285714286 349" stroke-linejoin="round" visibility="visible" stroke="rgba(192,192,192,0.0001)" stroke-width="22" zIndex="2" class=" highcharts-tracker" style=""></path></g><g class="highcharts-markers highcharts-series-0 highcharts-tracker" zIndex="0.1" transform="translate(44,10) scale(1 1)" clip-path="url(#highcharts-2)" style=""><path fill="#7cb5ec" d="M 430 345 C 435.328 345 435.328 353 430 353 C 424.672 353 424.672 345 430 345 Z"></path><path fill="#7cb5ec" d="M 398 345 C 403.328 345 403.328 353 398 353 C 392.672 353 392.672 345 398 345 Z"></path><path fill="#7cb5ec" d="M 366 54.16666666666663 C 371.328 54.16666666666663 371.328 62.16666666666663 366 62.16666666666663 C 360.672 62.16666666666663 360.672 54.16666666666663 366 54.16666666666663 Z"></path><path fill="#7cb5ec" d="M 334 345 C 339.328 345 339.328 353 334 353 C 328.672 353 328.672 345 334 345 Z"></path><path fill="#7cb5ec" d="M 302 345 C 307.328 345 307.328 353 302 353 C 296.672 353 296.672 345 302 345 Z"></path><path fill="#7cb5ec" d="M 270 54.16666666666663 C 275.328 54.16666666666663 275.328 62.16666666666663 270 62.16666666666663 C 264.672 62.16666666666663 264.672 54.16666666666663 270 54.16666666666663 Z"></path><path fill="#7cb5ec" d="M 238 345 C 243.328 345 243.328 353 238 353 C 232.672 353 232.672 345 238 345 Z"></path><path fill="#7cb5ec" d="M 207 345 C 212.328 345 212.328 353 207 353 C 201.672 353 201.672 345 207 345 Z"></path><path fill="#7cb5ec" d="M 175 345 C 180.328 345 180.328 353 175 353 C 169.672 353 169.672 345 175 345 Z"></path><path fill="#7cb5ec" d="M 143 345 C 148.328 345 148.328 353 143 353 C 137.672 353 137.672 345 143 345 Z"></path><path fill="#7cb5ec" d="M 111 345 C 116.328 345 116.328 353 111 353 C 105.672 353 105.672 345 111 345 Z"></path><path fill="#7cb5ec" d="M 79 345 C 84.328 345 84.328 353 79 353 C 73.672 353 73.672 345 79 345 Z"></path><path fill="#7cb5ec" d="M 47 345 C 52.328 345 52.328 353 47 353 C 41.672 353 41.672 345 47 345 Z"></path><path fill="#7cb5ec" d="M 15 345 C 20.328 345 20.328 353 15 353 C 9.672 353 9.672 345 15 345 Z"></path></g></g><g class="highcharts-axis-labels highcharts-xaxis-labels" zIndex="7"><text x="59.92857142857143" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="378" opacity="1"><tspan>14 Oct 2016</tspan></text><text x="0" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="-9999"><tspan>15 Oct 2016</tspan></text><text x="0" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="-9999"><tspan>16 Oct 2016</tspan></text><text x="0" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="-9999"><tspan>17 Oct 2016</tspan></text><text x="0" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="-9999"><tspan>18 Oct 2016</tspan></text><text x="0" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="-9999"><tspan>19 Oct 2016</tspan></text><text x="0" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="-9999"><tspan>20 Oct 2016</tspan></text><text x="0" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="-9999"><tspan>21 Oct 2016</tspan></text><text x="0" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="-9999"><tspan>22 Oct 2016</tspan></text><text x="0" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="-9999"><tspan>23 Oct 2016</tspan></text><text x="0" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="-9999"><tspan>24 Oct 2016</tspan></text><text x="0" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="-9999"><tspan>25 Oct 2016</tspan></text><text x="0" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="-9999"><tspan>26 Oct 2016</tspan></text><text x="474.07142857142856" style="color:#606060;cursor:default;font-size:11px;fill:#606060;" text-anchor="middle" transform="translate(0,0)" y="378" opacity="1"><tspan>27 Oct 2016</tspan></text></g><g class="highcharts-axis-labels highcharts-yaxis-labels" zIndex="7"><text x="29" style="color:#606060;cursor:default;font-size:11px;fill:#606060;width:155px;text-overflow:clip;" text-anchor="end" transform="translate(0,0)" y="361" opacity="1">0</text><text x="29" style="color:#606060;cursor:default;font-size:11px;fill:#606060;width:155px;text-overflow:clip;" text-anchor="end" transform="translate(0,0)" y="303" opacity="1">0.2</text><text x="29" style="color:#606060;cursor:default;font-size:11px;fill:#606060;width:155px;text-overflow:clip;" text-anchor="end" transform="translate(0,0)" y="245" opacity="1">0.4</text><text x="29" style="color:#606060;cursor:default;font-size:11px;fill:#606060;width:155px;text-overflow:clip;" text-anchor="end" transform="translate(0,0)" y="186" opacity="1">0.6</text><text x="29" style="color:#606060;cursor:default;font-size:11px;fill:#606060;width:155px;text-overflow:clip;" text-anchor="end" transform="translate(0,0)" y="128" opacity="1">0.8</text><text x="29" style="color:#606060;cursor:default;font-size:11px;fill:#606060;width:155px;text-overflow:clip;" text-anchor="end" transform="translate(0,0)" y="70" opacity="1">1</text><text x="29" style="color:#606060;cursor:default;font-size:11px;fill:#606060;width:155px;text-overflow:clip;" text-anchor="end" transform="translate(0,0)" y="12" opacity="1">1.2</text></g><g class="highcharts-tooltip" zIndex="8" style="cursor:default;padding:0;pointer-events:none;white-space:nowrap;" transform="translate(0,-9999)"><path fill="none" d="M 3.5 0.5 L 13.5 0.5 C 16.5 0.5 16.5 0.5 16.5 3.5 L 16.5 13.5 C 16.5 16.5 16.5 16.5 13.5 16.5 L 3.5 16.5 C 0.5 16.5 0.5 16.5 0.5 13.5 L 0.5 3.5 C 0.5 0.5 0.5 0.5 3.5 0.5" isShadow="true" stroke="black" stroke-opacity="0.049999999999999996" stroke-width="5" transform="translate(1, 1)"></path><path fill="none" d="M 3.5 0.5 L 13.5 0.5 C 16.5 0.5 16.5 0.5 16.5 3.5 L 16.5 13.5 C 16.5 16.5 16.5 16.5 13.5 16.5 L 3.5 16.5 C 0.5 16.5 0.5 16.5 0.5 13.5 L 0.5 3.5 C 0.5 0.5 0.5 0.5 3.5 0.5" isShadow="true" stroke="black" stroke-opacity="0.09999999999999999" stroke-width="3" transform="translate(1, 1)"></path><path fill="none" d="M 3.5 0.5 L 13.5 0.5 C 16.5 0.5 16.5 0.5 16.5 3.5 L 16.5 13.5 C 16.5 16.5 16.5 16.5 13.5 16.5 L 3.5 16.5 C 0.5 16.5 0.5 16.5 0.5 13.5 L 0.5 3.5 C 0.5 0.5 0.5 0.5 3.5 0.5" isShadow="true" stroke="black" stroke-opacity="0.15" stroke-width="1" transform="translate(1, 1)"></path><path fill="rgba(249, 249, 249, .85)" d="M 3.5 0.5 L 13.5 0.5 C 16.5 0.5 16.5 0.5 16.5 3.5 L 16.5 13.5 C 16.5 16.5 16.5 16.5 13.5 16.5 L 3.5 16.5 C 0.5 16.5 0.5 16.5 0.5 13.5 L 0.5 3.5 C 0.5 0.5 0.5 0.5 3.5 0.5"></path><text x="8" zIndex="1" style="font-size:12px;color:#333333;fill:#333333;" y="20"></text></g></svg></div></div><br/><br/>
                        <table width="650px" id="s-occupancy-table"><thead class="form-table-lh roboto-slab"><tr><th class="basic-information-pl20" width="145px">Date</th><th width="191px">occupancy (%)</th></tr></thead><tbody><tr><td class="basic-information-pl20">14 Oct 2016</td><td class="basic-information-pr20">0%</td></tr><tr><td class="basic-information-pl20">15 Oct 2016</td><td class="basic-information-pr20">0%</td></tr><tr><td class="basic-information-pl20">16 Oct 2016</td><td class="basic-information-pr20">0%</td></tr><tr><td class="basic-information-pl20">17 Oct 2016</td><td class="basic-information-pr20">0%</td></tr><tr><td class="basic-information-pl20">18 Oct 2016</td><td class="basic-information-pr20">0%</td></tr><tr><td class="basic-information-pl20">19 Oct 2016</td><td class="basic-information-pr20">0%</td></tr><tr><td class="basic-information-pl20">20 Oct 2016</td><td class="basic-information-pr20">0%</td></tr><tr><td class="basic-information-pl20">21 Oct 2016</td><td class="basic-information-pr20">0%</td></tr><tr><td class="basic-information-pl20">22 Oct 2016</td><td class="basic-information-pr20">1%</td></tr><tr><td class="basic-information-pl20">23 Oct 2016</td><td class="basic-information-pr20">0%</td></tr><tr><td class="basic-information-pl20">24 Oct 2016</td><td class="basic-information-pr20">0%</td></tr><tr><td class="basic-information-pl20">25 Oct 2016</td><td class="basic-information-pr20">1%</td></tr><tr><td class="basic-information-pl20">26 Oct 2016</td><td class="basic-information-pr20">0%</td></tr><tr><td class="basic-information-pl20">27 Oct 2016</td><td class="basic-information-pr20">0%</td></tr><tr><td class="basic-information-pl20">AVG</td><td class="basic-information-pr20">1%</td></tr></tbody></table>
                    </div>
                </div>

            
            </div>

        </div>
    </div>
</div>
</body>
</html>