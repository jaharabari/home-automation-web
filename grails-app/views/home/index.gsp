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
                    <td id="lights_room">${application.lightsRoom == 1 ? 'Ligada' : 'Desligada'}</td>
                    <td><button type="button" class="btn btn-sm btn-success action" data-topic="lights/room/set" data-message="1">On</button> <button type="button" class="btn btn-sm btn-danger action" data-topic="lights/room/set" data-message="0">Off</button></td>
                </tr>
                <tr>
                    <td>Luz quarto</td>
                    <td id="lights_bedroom">${application.lightsBedroom == 1 ? 'Ligada' : 'Desligada'}</td>
                    <td><button type="button" class="btn btn-sm btn-success action" data-topic="lights/bedroom/set" data-message="1">On</button> <button type="button" class="btn btn-sm btn-danger action" data-topic="lights/bedroom/set" data-message="0">Off</button></td>
                </tr>
                <tr>
                    <td>Luz cozinha</td>
                    <td id="lights_kitchen">${application.lightsKitchen == 1 ? 'Ligada' : 'Desligada'}</td>
                    <td><button type="button" class="btn btn-sm btn-success action" data-topic="lights/kitchen/set" data-message="1">On</button> <button type="button" class="btn btn-sm btn-danger action" data-topic="lights/kitchen/set" data-message="0">Off</button></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    $(function() {
        var socket = new SockJS("${createLink(uri: '/stomp')}");
        var client = Stomp.over(socket);
        client.connect({}, function() {
            client.subscribe("/topic/switches/status", function(message) {
                var data = JSON.parse(message.body);
                if (data.room == 1) {
                    $('#lights_room').html('Ligada');
                } else {
                    $('#lights_room').html('Desligada');
                }

                if (data.bedroom == 1) {
                    $('#lights_bedroom').html('Ligada');
                } else {
                    $('#lights_bedroom').html('Desligada');
                }

                if (data.kitchen == 1) {
                    $('#lights_kitchen').html('Ligada');
                } else {
                    $('#lights_kitchen').html('Desligada');
                }
            });
        });
        $('button.action').bind('click', publish);
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
            success: function(data) {
                if (data.error != 0) {

                }
            }, error: function(data, error, status) {

            }
        });
    }
</script>
</body>
</html>
