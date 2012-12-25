<?php

	require('joincount.inc.php');

	set_time_limit(0);

	if ($_SERVER["argc"] < 2)
		exit("\nphp joincount.php {logfile}\n");

	$log = $_SERVER["argv"][1];

	joincount($log);


?>
