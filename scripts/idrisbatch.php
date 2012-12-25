<?php

	$workloads = array(
//											'sim.workload.idris.KeysChurnTest',
//										  'sim.workload.idris.ProxyChurnTest',
											'sim.workload.idris.KeysTest',
//											'sim.workload.idris.ProxyTest',
										);

	$stealthratios = array(0.99, 0.95, 0.90, 0.80, 0.70, 0.60, 0.50, 0.40, 0.30, 0.20, 0.10);
	$networksize = array (1000);
	$name = time();

	$cmds = array();
	$i = 0;

	foreach ($stealthratios as $stealthratio) {
		foreach($workloads as $workload) {
			foreach ($networksize as $nodes) {
				for ($seed = 0; $seed < 100 ; $seed++) {

					$stealth = (int)($stealthratio * $nodes);
					$service = $nodes - $stealth;

					$cmds[] = "java -Xms300m -Xmx1000m -classpath ./bin sim.main.Simulator $i $workload $name $seed $service $stealth $stealthratio";
					$i++;
				}
			}
		}
	}

	// This file reads the cmds and kick starts the process
	require('dispatch.inc.php');
?>

