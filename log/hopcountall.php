<?php

	require('hopcount.inc.php');

	set_time_limit(0);

	$tabs = `find "C:/Projects/P2PSim/trunk/log" -mindepth 2 -maxdepth 4 -name "*.txt.gz"`;
	$tabs = explode("\n", trim($tabs));

	foreach($tabs as $log) {
		echo $log . "\n";

		hopcount($log);
	}

?>
