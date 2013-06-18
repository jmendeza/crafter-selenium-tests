<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Hello World</title>
</head>
<body>
<#if person?? >
<h1>${person}</h1>
</#if>
<p>
   <form method="post" id="helloWorld" accept-charset="utf-8" action="/">
    <label for="personsName"><span>Your name is ?:</span></label>
    <input type="text" size="20" id="personsName" name="personsName" autofocus="true"/>
    <hr/>
    <input type="submit" name="submit" value="Send"/>
   </form>
</p>
</body>
</html>