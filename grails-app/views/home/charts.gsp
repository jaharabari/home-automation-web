<%@ page import="org.joda.time.DateTime" contentType="text/html;charset=UTF-8" %>
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

    <script src="${resource(dir: 'js', file: 'moment.js')}" type="text/javascript"></script>

    <script src="${resource(dir: 'js', file: 'highcharts.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js/modules', file: 'data.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js/modules', file: 'exporting.js')}" type="text/javascript"></script>

    <script>
        $(function() {
            Highcharts.setOptions({
                lang: {
                    months: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agsto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
                    weekdays: ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado']
                }
            });

            $("#startdate").datepicker({
                dateFormat: 'dd/mm/yy',
                changeMonth: true,
                changeYear: true,
                numberOfMonths: 1,
                onClose: function(selectedDate) {
                    $("#enddate").datepicker("option", "minDate", selectedDate);
                }
            });
            $("#enddate").datepicker({
                dateFormat: 'dd/mm/yy',
                changeMonth: true,
                changeYear: true,
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

<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Filtro</h3>
        </div>
        <div class="panel-body">
            <form action="/" id="searchForm">
                <div class="form-group">
                    <label class="control-label" for="startdate">Início</label>
                    <div class="row">
                        <div class="col-xs-6">
                            <input type="text" id="startdate" name="startdate" class="form-control" value="${new DateTime().withTimeAtStartOfDay().toDate().format('dd/MM/yyyy')}"/>
                        </div>
                        <div class="col-xs-3">
                            <select name="starthour" class="form-control">
                                <option value="00" selected>0</option>
                                <option value="01">1</option>
                                <option value="02">2</option>
                                <option value="03">3</option>
                                <option value="04">4</option>
                                <option value="05">5</option>
                                <option value="06">6</option>
                                <option value="07">7</option>
                                <option value="08">8</option>
                                <option value="09">9</option>
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
                        </div>
                        <div class="col-xs-3">
                            <select name="startminute" class="form-control">
                                <option value="00" selected>00</option>
                                <option value="15">15</option>
                                <option value="30">30</option>
                                <option value="45">45</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label" for="enddate">Fim</label>
                    <div class="row">
                        <div class="col-xs-6">
                            <input type="text" id="enddate" name="enddate" class="form-control" value="${new DateTime().withTimeAtStartOfDay().plusDays(1).toDate().format('dd/MM/yyyy')}"/>
                        </div>
                        <div class="col-xs-3">
                            <select name="endhour" class="form-control">
                                <option value="00" selected>0</option>
                                <option value="01">1</option>
                                <option value="02">2</option>
                                <option value="03">3</option>
                                <option value="04">4</option>
                                <option value="05">5</option>
                                <option value="06">6</option>
                                <option value="07">7</option>
                                <option value="08">8</option>
                                <option value="09">9</option>
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
                        </div>
                        <div class="col-xs-3">
                            <select name="endminute" class="form-control">
                                <option value="00" selected>00</option>
                                <option value="15">15</option>
                                <option value="30">30</option>
                                <option value="45">45</option>
                            </select>
                        </div>
                    </div>
                </div>
                <input type="submit" value="Buscar" class="btn btn-primary">
            </form>
        </div>
    </div>

    <div id="temperatureChart" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
    <div id="humidityChart" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

    <div class="row">
        <div class="col-xs-12 col-md-6">
            <table class="table table-bordered" id="temperatureTable">
                <thead>
                    <tr>
                        <th>Data</th>
                        <th>˚C</th>
                    </tr>
                </thead>
                <tbody id="temperatureTbody"></tbody>
            </table>
        </div>
        <div class="col-xs-12 col-md-6">
            <table class="table table-bordered" id="humidityTable">
                <thead>
                <tr>
                    <th>Data</th>
                    <th>%</th>
                </tr>
                </thead>
                <tbody id="humidityTbody"></tbody>
            </table>
        </div>
    </div>

    <script>
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
                    endmintxt = $form.find( "select[name='endminute']" ).val();

            var startdt = startdtxt + ' ' + starthrtxt + ':' + startmintxt;
            var enddt = enddtxt + ' ' + endhrtxt + ':' + endmintxt;

            $.ajax({
                url: '${createLink(controller: 'home', action: "chartData")}',
                cache: false,
                contentType: "application/json; charset=utf-8",
                type: 'GET',
                data: {
                    start: startdt,
                    end: enddt
                },
                success: function(data) {
                    if (data.error != 0) {
                        console.log(data.payload);
                    } else {
                        console.log(data.payload);

                        var tempStr = '';
                        $.each(data.payload.temperatures, function( index, value ) {
                            tempStr += '<tr>';
                            tempStr += '<td>' + value.time + '</td>';
                            tempStr += '<td>' + value.value + '</td>';
                            tempStr += '</tr>';
                        });
                        var tempTbody = $('#temperatureTbody');
                        tempTbody.empty();
                        tempTbody.append(tempStr);

                        var humidStr = '';
                        $.each(data.payload.humidities, function( index, value ) {
                            humidStr += '<tr>';
                            humidStr += '<td>' + value.time + '</td>';
                            humidStr += '<td>' + value.value + '</td>';
                            humidStr += '</tr>';
                        });
                        var humidTbody = $('#humidityTbody');
                        humidTbody.empty();
                        humidTbody.append(humidStr);

                        $('#temperatureChart').highcharts({
                            title: { text: 'Temperatura' },
                            xAxis: { type: 'datetime' },
                            yAxis: {
                                title: { text: '°C' }
                            },
                            data: {
                                table: 'temperatureTable',
                                parseDate: parseDate
                            }
                        });

                        $('#humidityChart').highcharts({
                            title: { text: 'Umidade' },
                            xAxis: { type: 'datetime' },
                            yAxis: {
                                title: { text: '%' }
                            },
                            data: {
                                table: 'humidityTable',
                                parseDate: parseDate
                            }
                        });
                    }
                }, error: function(data, error, status) {
                    console.log('Ocorreu um problema ao tentar processar a requisição.');
                }
            });
        });

        function parseDate(str) {
            return moment(str, "DD/MM/YYYY HH:mm:ss").toDate().getTime();
        }
    </script>
</div>
</body>
</html>
