<?php

	$workload = 'sim.net.overlay.cdn.workload.WorldCupFromLogs';

	$name = time();
	$seed = 0;
	$servers = 1;

	$actions = array ( 'eurovision.actions', 'mufc-mila.actions' );
//	$actions = array ( 'eurovision.actions' );
//	$actions = array ( 'mufc-mila.actions' );

	//$prefetchSchemes = array ( 7, 1, 0, 5, 3, 2, 4, 6 );
	//$prefetchSchemes = array ( 8, 7, 1, 5, 3, 2, 4, 6 );
//	$prefetchSchemes = array ( 0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11 );
	
	$prefetchSchemes = array ( 3, 5, 10, 11, 12, 13 );	

	//$prefetchSizes = array ( 10, 20, 30, 45, 60, 90, 120, 150, 180, 210, 250, 300, 350, 400 );
	$prefetchSizes = array ( 10, 45, 60, 90, 150, 180, 210, 250, 350, 400 );

	$i = 0;
	$cmds = array();

	/*

	foreach ($actions as $action) {
		foreach ($prefetchSchemes as $prefetchScheme) {
			foreach ($prefetchSizes as $prefetchSize) {
				$cmds[] = "java -Xms300m -Xmx1000m -classpath ./bin sim.main.CDNSimulator $i $workload $name $seed $servers $action ./ $prefetchScheme 1 $prefetchSize";
				$i++;
			}
		}
	}

	*/

	$prefetchSizes = array ( 0.10, 0.25, 0.45, 0.60, 0.75, 0.80, 0.90, /* 0.9125, 0.925, 0.9375, 0.95, 0.975,*/ 1.0 );
	//$prefetchSizes = array ( 0.95 );
	//$prefetchRates = array ( 0.10, 0.25, 0.50, 0.75, 1, 1.25, 1.5, 2 );
	$prefetchRates = array ( 1 );

	foreach ($actions as $action) {
		foreach ($prefetchSchemes as $prefetchScheme) {
			foreach ($prefetchSizes as $prefetchSize) {
				foreach ($prefetchRates as $prefetchRate) {
					$cmds[] = "java -Xms300m -Xmx1000m -classpath ./bin sim.main.CDNSimulator $i $workload $name $seed $servers $action ./ $prefetchScheme $prefetchRate 60 $prefetchSize";
					$i++;
				}
			}
		}
	}


	require('dispatch.inc.php');

?>

