<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <link rel="stylesheet" href="normalize.css">
    <link rel="stylesheet" href="pliker.css">
    <title>Pliker - Login</title>
</head>
<body>
    <p id="login_pliker_header">
        Pliker
    </p>
    <div id="form_container">
        <form action="/login" method="post" id="form">
            <#if flashmsg??>
                <div id="flash_group">
                    ${flashmsg}
                </div>
            </#if>
            <div id="token_group">
                <input type="password" class="inputbox" name="token" id="token" placeholder="Access Token" size="30">
            </div>
            <div id="submit_group">
                <input type="submit" value="Log in" id="login_button">
            </div>
        </form>
    </div>
</body>
</html>