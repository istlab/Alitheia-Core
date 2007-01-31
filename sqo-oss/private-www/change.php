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
	<div class="content">
	<?php
		/*	
		// check if passwords have zero length
		if( strlen($_POST['old']) != 0 && strlen($_POST['new_one']) != 0 && strlen($_POST['new_two']) != 0 ) {
			// check if new passwords dont match
			if(strlen($_POST['new_one']) < 8) {
				$message = "Password is too short (minimum 8 characters)";
			} else if( $_POST['new_one'] == $_POST['new_two'] ) {
				// update passwords
				shell_exec("/usr/apache2/bin/htpasswd -bm /var/svn/common/conf/svnauth ".$_SERVER['PHP_AUTH_USER']." ".$_POST['new_one']);
				$message = "Password was updated succesfully";
			} else {
				$message = 'New passwords dont match!';
			}
		} else {
			$message = 'Invalid passwords! Password change canceled.';
		}*/
		$link = mysql_connect('127.0.0.1','root','petroula');
		if(!$link) {
			$message = "Cannot connect to the database!";
		} else {  
			if(mysql_select_db('horde',$link)) {
				$query = "select * from horde_users where user_uid = '".$_SERVER['PHP_AUTH_USER']."' and user_pass = '".$_POST['old']."'";
				$result = mysql_query($query,$link);
				if(!$result) {
					$message = "User does not exist!";
				} else {
					//$row = mysql_fetch_array();
				}				
			} else { $message = "Cannot connect to the database!"; }			
		}
		
	?>
	<h2><?php echo $message ?></h2>
	</div>
	</td>
</tr>
</table>
</body>
</html>