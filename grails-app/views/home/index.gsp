<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home Automation Web</title>

    <link href='http://fonts.googleapis.com/css?family=Roboto+Slab:400,700,300,100' rel='stylesheet' type='text/css'>

    <link href="${resource(dir: 'css', file: 'font-awesome.min.css')}" rel="stylesheet">

    <script src="${resource(dir: 'js', file: 'jquery-1.11.3.min.js')}"></script>
    <script src="${resource(dir: 'js', file: 'jquery-ui.min.js')}"></script>

    <link href="${resource(dir: 'css', file: 'jquery-ui.min.css')}" rel="stylesheet">
    <link href="${resource(dir: 'css', file: 'jquery-ui.theme.min.css')}" rel="stylesheet">

    <link href="${resource(dir: 'css', file: 'bootstrap.min.css')}" rel="stylesheet">
    <link href="${resource(dir: 'css', file: 'bootstrap-theme.min.css')}" rel="stylesheet">
    <script src="${resource(dir: 'js', file: 'bootstrap.min.js')}"></script>
</head>
<body>
<div class="navbar">
    <div class="navbar-inner">
        <a class="brand" href="#">Automação Residencial</a>
    </div>
</div>

<g:link controller="home" action="charts">Gráficos</g:link>
</body>
</html>
