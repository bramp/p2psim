<?php

	require('joincount.inc.php');

	set_time_limit(0);

	$tabs = `find "./Stealth Join Test" -mindepth 3 -maxdepth 4 -name "*.txt.gz"`;
	$tabs = explode("\n", trim($tabs));

	foreach($tabs as $log) {
		echo $log . "\n";

		joincount($log);
	}




?>
