<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home Automation Web</title>

    <link href="${resource(dir: 'css', file: 'font-awesome.min.css')}" rel="stylesheet">

    <script src="${resource(dir: 'js', file: 'jquery-1.11.3.min.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'jquery-ui.min.js')}" type="text/javascript"></script>

    <link href="${resource(dir: 'css', file: 'jquery-ui.min.css')}" rel="stylesheet">
    <link href="${resource(dir: 'css', file: 'jquery-ui.theme.min.css')}" rel="stylesheet">

    <link href="${resource(dir: 'css', file: 'bootstrap.min.css')}" rel="stylesheet">
    <link href="${resource(dir: 'css', file: 'bootstrap-theme.min.css')}" rel="stylesheet">
    <script src="${resource(dir: 'js', file: 'bootstrap.min.js')}" type="text/javascript"></script>

    <link href="${resource(dir: 'css', file: 'theme.css')}" rel="stylesheet">

    <script src="${resource(dir: 'js', file: 'raphael-2.1.4.min.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js', file: 'justgage-1.1.0.min.js')}" type="text/javascript"></script>

    <asset:javascript src="spring-websocket" />
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
                <li class="active"><g:link uri="/">Home</g:link></li>
                <li><g:link controller="home" action="charts">Gráficos</g:link></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>

<div class="container">
    <div class="row">
        <div class="col-md-6">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Controles</h3>
                </div>
                <div class="panel-body">
                    <table class="table">
                        <thead>
                        <tr>
                            <th>Nome</th>
                            <th>Estado</th>
                            <th>Ações</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td>Luz sala</td>
                            <td>
                                <span id="lights_room" class="label"></span>
                            </td>
                            <td><button type="button" class="btn btn-sm btn-primary action" data-topic="lights/room/set" data-message="1">On</button> <button type="button" class="btn btn-sm btn-default action" data-topic="lights/room/set" data-message="0">Off</button></td>
                        </tr>
                        <tr>
                            <td>Luz quarto</td>
                            <td>
                                <span id="lights_bedroom" class="label"></span>
                            </td>
                            <td><button type="button" class="btn btn-sm btn-primary action" data-topic="lights/bedroom/set" data-message="1">On</button> <button type="button" class="btn btn-sm btn-default action" data-topic="lights/bedroom/set" data-message="0">Off</button></td>
                        </tr>
                        <tr>
                            <td>Luz cozinha</td>
                            <td>
                                <span id="lights_kitchen" class="label"></span>
                            </td>
                            <td><button type="button" class="btn btn-sm btn-primary action" data-topic="lights/kitchen/set" data-message="1">On</button> <button type="button" class="btn btn-sm btn-default action" data-topic="lights/kitchen/set" data-message="0">Off</button></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Tempo Real</h3>
                </div>
                <div class="panel-body">
                    <div id="gauge-temperature" class="200x160px"></div>
                    <div id="gauge-humidity" class="200x160px"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    var tempGauge;
    var humiGauge;

    $(function() {
        toggleStatusLabel('lights_room', ${application.lightsRoom == 1});
        toggleStatusLabel('lights_bedroom', ${application.lightsBedroom == 1});
        toggleStatusLabel('lights_kitchen', ${application.lightsKitchen == 1});

        var socket = new SockJS("${createLink(uri: '/stomp')}");
        var client = Stomp.over(socket);
        client.connect({}, function() {
            client.subscribe("/topic/switches/status", function(message) {
                var data = JSON.parse(message.body);
                toggleStatusLabel('lights_room', data.room == 1);
                toggleStatusLabel('lights_bedroom', data.bedroom == 1);
                toggleStatusLabel('lights_kitchen', data.kitchen == 1);
            });
            client.subscribe("/topic/sensors/status", function(message) {
                var data = JSON.parse(message.body);
                tempGauge.refresh(data.temperature);
                humiGauge.refresh(data.humidity);
            });
        });

        $('button.action').bind('click', publish);

        tempGauge = new JustGage({
            id: "gauge-temperature",
            value: ${temperature},
            min: -40,
            max: 80,
            title: "Temperatura ˚C"
        });

        humiGauge = new JustGage({
            id: "gauge-humidity",
            value: ${humidity},
            min: 0,
            max: 100,
            title: "Umidade %"
        });
    });

    function publish() {
        var topic = $(this).attr('data-topic');
        var message = $(this).attr('data-message');
        $.ajax({
            url: '${createLink(controller: 'home', action: "publish")}',
            dataType: 'json',
            type: 'POST',
            data: {
                topic: topic,
                message: message
            },
            error: function(data, error, status) {
                alert(data.payload);
            }
        });
    }

    function toggleStatusLabel(id, on) {
        var span = $('#'+id);
        span.removeClass('label-default');
        span.removeClass('label-warning');
        if (on == true) {
            span.addClass('label-warning');
            span.html('Ligada')
        } else {
            span.addClass('label-default');
            span.html('Desligada')
        }
    }
</script>
</body>
</html>
