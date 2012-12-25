<?php
	set_time_limit(0);

	$tabs = `find "./Stealth Join Test" -mindepth 3 -maxdepth 4 -name "*.join.tab"`;
	$tabs = explode("\n", trim($tabs));

	echo "Node\tRatio\tJoinMessage\tJoinFinishedMessage\tPairsMessage\tPairs\tTotalJoinMessage\tTotalJoinFinishedMessage\tTotalPairsMessage\tTotalPairs\n";

	foreach($tabs as $log) {
		//echo $log . "\n";

		$file = file($log);

		preg_match ("/output-n([\d]+)-([\d\.]+)-([\d]+)/", $log, $regs);

		$nodes = $regs[1];
		$ratio = $regs[2];
		$seed =  $regs[3];

		if ( isset($file[1]) ) {
			echo $nodes . "\t" . $ratio . "\t" . $file[1];
		}

	}




?>
