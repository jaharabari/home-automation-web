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
                                <th class="col-xs-6">Nome</th>
                                <th class="col-xs-2">Estado</th>
                                <th class="col-xs-4">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Luz varanda sala</td>
                                <td>
                                    <span id="lights_room_balcony" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/lights_room_balcony/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/lights_room_balcony/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Luz sala</td>
                                <td>
                                    <span id="lights_room" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/lights_room/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/lights_room/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Luz sala-cozinha</td>
                                <td>
                                    <span id="lights_room_kitchen" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/lights_room_kitchen/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/lights_room_kitchen/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Luz cozinha</td>
                                <td>
                                    <span id="lights_kitchen" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/lights_kitchen/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/lights_kitchen/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Luz banheiro</td>
                                <td>
                                    <span id="lights_bathroom" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/lights_bathroom/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/lights_bathroom/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Luz banheiro-espelho</td>
                                <td>
                                    <span id="lights_bathroom_mirror" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/lights_bathroom_mirror/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/lights_bathroom_mirror/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Luz entrada</td>
                                <td>
                                    <span id="lights_entry_balcony" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/lights_entry_balcony/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/lights_entry_balcony/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Luz quarto</td>
                                <td>
                                    <span id="lights_bedroom" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/lights_bedroom/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/lights_bedroom/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Luz varanda quarto</td>
                                <td>
                                    <span id="lights_bedroom_balcony" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/lights_bedroom_balcony/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/lights_bedroom_balcony/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Luz quarto superior</td>
                                <td>
                                    <span id="lights_upper_bedroom" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/lights_upper_bedroom/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/lights_upper_bedroom/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Luz área de serviço</td>
                                <td>
                                    <span id="lights_service_area" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/lights_service_area/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/lights_service_area/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Luz telhado verde</td>
                                <td>
                                    <span id="lights_green_roof" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/lights_green_roof/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/lights_green_roof/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Tomada esquerda cama</td>
                                <td>
                                    <span id="sockets_bedroom_left" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/sockets_bedroom_left/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/sockets_bedroom_left/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Tomada direita cama</td>
                                <td>
                                    <span id="sockets_bedroom_right" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/sockets_bedroom_right/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/sockets_bedroom_right/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Sensores</h3>
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
        toggleStatusLabel('lights_room_balcony', ${application.lights_room_balcony == 1});
        toggleStatusLabel('lights_room', ${application.lights_room == 1});
        toggleStatusLabel('lights_room_kitchen', ${application.lights_room_kitchen == 1});
        toggleStatusLabel('lights_kitchen', ${application.lights_kitchen == 1});
        toggleStatusLabel('lights_bathroom', ${application.lights_bathroom == 1});
        toggleStatusLabel('lights_bathroom_mirror', ${application.lights_bathroom_mirror == 1});
        toggleStatusLabel('lights_entry_balcony', ${application.lights_entry_balcony == 1});
        toggleStatusLabel('lights_bedroom', ${application.lights_bedroom == 1});
        toggleStatusLabel('lights_bedroom_balcony', ${application.lights_bedroom_balcony == 1});
        toggleStatusLabel('lights_upper_bedroom', ${application.lights_upper_bedroom == 1});
        toggleStatusLabel('lights_service_area', ${application.lights_service_area == 1});
        toggleStatusLabel('lights_green_roof', ${application.lights_green_roof == 1});
        toggleStatusLabel('sockets_bedroom_left', ${application.sockets_bedroom_left == 1});
        toggleStatusLabel('sockets_bedroom_right', ${application.sockets_bedroom_right == 1});

        var socket = new SockJS("${createLink(uri: '/stomp')}");
        var client = Stomp.over(socket);
        client.connect({}, function() {
            client.subscribe("/topic/switches/status", function(message) {
                var data = JSON.parse(message.body);
                toggleStatusLabel('lights_room_balcony', data.lights_room_balcony == 1);
                toggleStatusLabel('lights_room', data.lights_room == 1);
                toggleStatusLabel('lights_room_kitchen', data.lights_room_kitchen == 1);
                toggleStatusLabel('lights_kitchen', data.lights_kitchen == 1);
                toggleStatusLabel('lights_bathroom', data.lights_bathroom == 1);
                toggleStatusLabel('lights_bathroom_mirror', data.lights_bathroom_mirror == 1);
                toggleStatusLabel('lights_entry_balcony', data.lights_entry_balcony == 1);
                toggleStatusLabel('lights_bedroom', data.lights_bedroom == 1);
                toggleStatusLabel('lights_bedroom_balcony', data.lights_bedroom_balcony == 1);
                toggleStatusLabel('lights_upper_bedroom', data.lights_upper_bedroom == 1);
                toggleStatusLabel('lights_service_area', data.lights_service_area == 1);
                toggleStatusLabel('lights_green_roof', data.lights_green_roof == 1);
                toggleStatusLabel('sockets_bedroom_left', data.sockets_bedroom_left == 1);
                toggleStatusLabel('sockets_bedroom_right', data.sockets_bedroom_right == 1);
            });
            client.subscribe("/topic/sensors/temperature", function(message) {
                console.log(message.body)
                tempGauge.refresh(message.body);
            });
            client.subscribe("/topic/sensors/humidity", function(message) {
                console.log(message.body)
                humiGauge.refresh(message.body);
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
            span.html('on')
        } else {
            span.addClass('label-default');
            span.html('off')
        }
    }
</script>
</body>
</html>
