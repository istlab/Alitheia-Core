<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="resources/styles.css" type="text/css" rel="stylesheet">
<title>SQO-OSS private member area</title>
</head>
<body margin-left="0" margin-top="0">
<div class="logo">
<img src="resources/logo.jpg" alt="SQO-OSS logo" align="middle" border="0"> - Source Quality Observatory for Open Source Software
</div>
<div class="projecttitle">Private member area (<?php print $_SERVER['PHP_AUTH_USER'] ?>)</div>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<td valign="top">
		<?php include('menu.php') ?>
	</td>
	<td valign="top" width="100%">
	<h2 class="title">Change password</h2>
	<div class="content">	
	<form action="change.php" method="post">
	<table class="content">
	<tr>
		<td align="right"><b>username:</b></td>
		<td><?php print $_SERVER['PHP_AUTH_USER'] ?></td>
	</tr>
	<tr>
		<td align="right"><b>old password:</b></td>
		<td><input type="password" name="old" /></td>
	</tr>
	<tr>
		<td align="right"><b>new password:</b></td>
		<td><input type="password" name="new_one" /></td>
	</tr>
	<tr>
		<td align="right"><b>retype password:</b></td>
		<td><input type="password" name="new_two"/></td>
	</tr>
	</table>	
	<input type="submit" value="Save"/> <input type="reset" value="Clear"/>
	</form>
	</div>
	</td>
</tr>
</table>
</body>
</html>