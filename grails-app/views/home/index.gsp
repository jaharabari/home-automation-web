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
                                <td>Sala</td>
                                <td>
                                    <span id="room" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/room/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/room/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Cozinha</td>
                                <td>
                                    <span id="kitchen" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/kitchen/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/kitchen/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Banheiro</td>
                                <td>
                                    <span id="bathroom" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/bathroom/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/bathroom/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Quarto</td>
                                <td>
                                    <span id="bedroom" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/bedroom/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/bedroom/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Quarto superior</td>
                                <td>
                                    <span id="upper" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/upper/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/upper/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Balcão da cozinha</td>
                                <td>
                                    <span id="counter" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/counter/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/counter/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Corredor</td>
                                <td>
                                    <span id="corridor" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/corridor/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/corridor/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Varanda da sala</td>
                                <td>
                                    <span id="room_porch" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/room_porch/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/room_porch/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Varanda do quarto</td>
                                <td>
                                    <span id="bedroom_porch" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/bedroom_porch/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/bedroom_porch/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Entrada</td>
                                <td>
                                    <span id="entry" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/entry/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/entry/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Lavanderia</td>
                                <td>
                                    <span id="laundry" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/laundry/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/laundry/set" data-message="0">Off</button>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>Área de lazer</td>
                                <td>
                                    <span id="recreation" class="label"></span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <button type="button" class="btn btn-sm btn-primary action" data-topic="relays/recreation/set" data-message="1">On</button>
                                        <button type="button" class="btn btn-sm btn-default action" data-topic="relays/recreation/set" data-message="0">Off</button>
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
        toggleStatusLabel('room_porch', ${application.room_porch == 1});
        toggleStatusLabel('room', ${application.room == 1});
        toggleStatusLabel('counter', ${application.counter == 1});
        toggleStatusLabel('kitchen', ${application.kitchen == 1});
        toggleStatusLabel('bathroom', ${application.bathroom == 1});
        toggleStatusLabel('corridor', ${application.corridor == 1});
        toggleStatusLabel('entry', ${application.entry == 1});
        toggleStatusLabel('bedroom', ${application.bedroom == 1});
        toggleStatusLabel('bedroom_porch', ${application.bedroom_porch == 1});
        toggleStatusLabel('laundry', ${application.laundry == 1});
        toggleStatusLabel('upper', ${application.upper == 1});
        toggleStatusLabel('recreation', ${application.recreation == 1});

        var socket = new SockJS("${createLink(uri: '/stomp')}");
        var client = Stomp.over(socket);
        client.connect({}, function() {
            client.subscribe("/topic/switches/status", function(message) {
                var data = JSON.parse(message.body);
                toggleStatusLabel('room_porch', data.room_porch == 1);
                toggleStatusLabel('room', data.room == 1);
                toggleStatusLabel('counter', data.counter == 1);
                toggleStatusLabel('kitchen', data.kitchen == 1);
                toggleStatusLabel('bathroom', data.bathroom == 1);
                toggleStatusLabel('corridor', data.corridor == 1);
                toggleStatusLabel('entry', data.entry == 1);
                toggleStatusLabel('bedroom', data.bedroom == 1);
                toggleStatusLabel('bedroom_porch', data.bedroom_porch == 1);
                toggleStatusLabel('laundry', data.laundry == 1);
                toggleStatusLabel('upper', data.upper == 1);
                toggleStatusLabel('recreation', data.recreation == 1);
                console.log(data);
            });
            client.subscribe("/topic/sensors/temperature", function(message) {
                tempGauge.refresh(message.body);
                console.log(message.body);
            });
            client.subscribe("/topic/sensors/humidity", function(message) {
                humiGauge.refresh(message.body);
                console.log(message.body);
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
