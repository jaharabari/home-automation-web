<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home Automation Web - Gráficos</title>

    <link href="${resource(dir: 'css', file: 'font-awesome.min.css')}" rel="stylesheet">

    <script src="${resource(dir: 'js', file: 'jquery-1.11.3.min.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'jquery-ui.min.js')}" type="text/javascript"></script>

    <link href="${resource(dir: 'css', file: 'jquery-ui.min.css')}" rel="stylesheet">
    <link href="${resource(dir: 'css', file: 'jquery-ui.theme.min.css')}" rel="stylesheet">

    <link href="${resource(dir: 'css', file: 'bootstrap.min.css')}" rel="stylesheet">
    <link href="${resource(dir: 'css', file: 'bootstrap-theme.min.css')}" rel="stylesheet">
    <script src="${resource(dir: 'js', file: 'bootstrap.min.js')}" type="text/javascript"></script>

    <link href="${resource(dir: 'css', file: 'theme.css')}" rel="stylesheet">

    <script src="${resource(dir: 'js', file: 'chart.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'moment.js')}" type="text/javascript"></script>

    <asset:javascript src="spring-websocket" />

    <script>
        $(function() {
            var socket = new SockJS("${createLink(uri: '/stomp')}");
            var client = Stomp.over(socket);
            client.connect({}, function() {
                client.subscribe("/topic/sensors/status", function(message) {
                    console.log("STATUS: " + message.body);
                });
            });

            $("#startdate").datepicker({
                defaultDate: "-1d",
                changeMonth: true,
                numberOfMonths: 1,
                onClose: function(selectedDate) {
                    $("#enddate").datepicker("option", "minDate", selectedDate);
                }
            });
            $("#enddate").datepicker({
                defaultDate: "+1d",
                changeMonth: true,
                numberOfMonths: 1,
                onClose: function(selectedDate) {
                    $("#startdate").datepicker("option", "maxDate", selectedDate);
                }
            });
        });
    </script>
</head>
<body>

<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Abrir navegação</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Automação</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li><g:link uri="/">Home</g:link></li>
                <li class="active"><g:link controller="home" action="charts">Gráficos</g:link></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>

<div id="main" class="container">
    <form action="/" id="searchForm">
        <table>
            <tr>
                <p>
                <td>Início:</td><td> <input type="text" id="startdate" name="startdate" ></td>
                <td>
                    <select name="starthour">
                        <option value="0" selected>0</option>
                        <option value="1">1</option>
                        <option value="2">2</option>
                        <option value="3">3</option>
                        <option value="4">4</option>
                        <option value="5">5</option>
                        <option value="6">6</option>
                        <option value="7">7</option>
                        <option value="8">8</option>
                        <option value="9">9</option>
                        <option value="10">10</option>
                        <option value="11">11</option>
                        <option value="12">12</option>
                        <option value="13">13</option>
                        <option value="14">14</option>
                        <option value="15">15</option>
                        <option value="16">16</option>
                        <option value="17">17</option>
                        <option value="18">18</option>
                        <option value="19">19</option>
                        <option value="20">20</option>
                        <option value="21">21</option>
                        <option value="22">22</option>
                        <option value="23">23</option>
                    </select>
                </td>
                <td>
                    <select name="startminute">
                        <option value="0" selected>00</option>
                        <option value="15">15</option>
                        <option value="30">30</option>
                        <option value="45">45</option>
                    </select>
                </td>
            </p>
            </tr>
            <tr>
                <p>
                <td>Fim: </td><td><input type="text" id="enddate" name="enddate"></td>
                <td>
                    <select name="endhour">
                        <option value="0" selected>0</option>
                        <option value="1">1</option>
                        <option value="2">2</option>
                        <option value="3">3</option>
                        <option value="4">4</option>
                        <option value="5">5</option>
                        <option value="6">6</option>
                        <option value="7">7</option>
                        <option value="8">8</option>
                        <option value="9">9</option>
                        <option value="10">10</option>
                        <option value="11">11</option>
                        <option value="12">12</option>
                        <option value="13">13</option>
                        <option value="14">14</option>
                        <option value="15">15</option>
                        <option value="16">16</option>
                        <option value="17">17</option>
                        <option value="18">18</option>
                        <option value="19">19</option>
                        <option value="20">20</option>
                        <option value="21">21</option>
                        <option value="22">22</option>
                        <option value="23">23</option>
                    </select>
                </td>
                <td>
                    <select name="endminute">
                        <option value="0" selected>00</option>
                        <option value="15">15</option>
                        <option value="30">30</option>
                        <option value="45">45</option>
                    </select>
                </td>
            </p>
            </tr>
            <tr>
                <td></td>
                <td><input type="radio" name="displayData" value="Umidade" checked>Umidade</td>
                <td><input type="radio" name="displayData" value="Temperatura">Temperatura</td>
            </tr>
            <tr>
                <td><input type="submit" value="Buscar"></td>
            </tr>
        </table>
    </form>
    <canvas id="myChart" width="700" height="500"></canvas>
    <div id="result"></div>
    <script type="text/javascript">
        function draw(labelsArray, dataArray) {
            var ctx = $("#myChart").get(0).getContext("2d");
            // set canvas dimensions
            ctx.canvas.width = 700;
            ctx.canvas.height = 500;

            var data = {
                labels : labelsArray,
                datasets : [ {
                    fillColor : "rgba(151,187,205,0.5)",
                    strokeColor : "rgba(151,187,205,1)",
                    pointColor : "rgba(151,187,205,1)",
                    pointStrokeColor : "#fff",
                    data : dataArray
                } ]
            };

            var myNewChart = new Chart(ctx).Line(data);
        }
    </script>
    <script>
        var switchValue = "Umidade";
        $.ajax({
            error: function(xhr, status, error) {
                var err = eval("(" + xhr.responseText + ")");
                alert(err.Message);
            }
        });
        $( "#searchForm" ).submit(function( event ) {
            // Stop form from submitting normally
            event.preventDefault();

            // Get some values from elements on the page:
            var $form = $( this ),
                    startdtxt = $form.find( "input[name='startdate']" ).val(),
                    starthrtxt = $form.find( "select[name='starthour']" ).val(),
                    startmintxt = $form.find( "select[name='startminute']" ).val(),
                    enddtxt = $form.find( "input[name='enddate']" ).val(),
                    endhrtxt = $form.find( "select[name='endhour']" ).val(),
                    endmintxt = $form.find( "select[name='endminute']" ).val(),
                    displayValue = $form.find( "input[name='displayData']:checked" ).val();
            switchValue = displayValue;

            var startdt = Date.parse(startdtxt) + (parseInt(starthrtxt) * 60 * 60 * 1000) + (parseInt(startmintxt) * 60 * 1000);
            var enddt = Date.parse(enddtxt) + (parseInt(endhrtxt) * 60 * 60 * 1000) + (parseInt(endmintxt) *  60 * 1000);

            $.ajax({
                url: '${createLink(controller: 'home', action: "chartData")}',
                cache: false,
                contentType: "application/json; charset=utf-8",
                type: 'GET',
                data: {
                    start: startdt,
                    end: enddt,
                    switchValue: switchValue
                },
                success: function(data) {
                    if (data.error != 0) {
                        console.log(data.payload);
                    } else {
                        var length = data.payload.length;
                        if (length != 0) {
                            var maximum = 25;
                            var increment = (length <= maximum) ? 1 : Math.round(length/maximum);
                            var totalsize = (length <= maximum) ? length : maximum;
                            var timeArray = new Array(totalsize);
                            var dataArray = new Array(totalsize);
                            for (var i = 0; i < totalsize; i++) {
                                var current = data.payload[i*increment];
                                timeArray[i] = moment(current.time).format('MM-DD-YY HH:mm');
                                dataArray[i] = current.value;
                            }
                            console.log(dataArray);
                            draw(timeArray, dataArray);
                        } else {
                            console.log("no data");
                        }
                    }
                }, error: function(data, error, status) {
                    console.log('Ocorreu um problema ao tentar processar a requisição.');
                }
            });
        });
    </script>
</div>
</body>
</html>
