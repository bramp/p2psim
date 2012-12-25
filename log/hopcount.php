<?php

	require('hopcount.inc.php');

	set_time_limit(0);

	if ($_SERVER["argc"] < 2)
		exit("\nphp hopcount.php {logfile}\n");

	$log = $_SERVER["argv"][1];

	hopcount($log);


?>
