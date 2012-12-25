<?php

	$startseed = 3;
	$endseed = 9;

	//$gatewayRatios = array(0, 1);
	$gatewayRatios = array(1);
	//$services = array(10, 100, 500);
	//$stealths = array(10, 25, 50, 75, 100, 150, 250, 500, 750, 1000, 1500, 2000);
	//$services = array(2000, 1000, 10, 100, 500);
	//$stealths = array(8000, 10, 25, 50, 75, 100, 150, 250, 500, 750, 1000, 1500, 2000);
	$services = array(1000);
	$stealths = array(10, 25, 50, 75, 100, 150, 250, 500, 750, 1000, 1500, 2000, 2500);

	$type = array ( 0, 1 ); // Per message, and Per Hop

	$truefalse = array('true', 'false');

	$cmds = array();
	$i = 195;

	//////////////////////////////////////////////////////////////////

	$workload = 'sim.workload.authentication.StealthAuthTest';

	for ($seed = $startseed; $seed < $endseed; $seed++) {
		foreach ($gatewayRatios as $gatewayRatio) {
			foreach ($services as $service) {
				foreach ($stealths as $stealth) {
					foreach ($type as $perhop) {
						foreach ($truefalse as $chain) {

							// Must have atleast one!
							$gateway = floor($service * $gatewayRatio);
							$gateway = max(1, $gateway);

							$cmds[] = "java -Xms150m -Xmx1000m -classpath ./bin sim.main.Simulator $i $workload auth $seed $gateway $service $stealth $perhop $chain $gatewayRatio";
							$i++;
						}
					}
				}
			}
		}
	}

	//$cmds = array();

	///////////////////////////////////////////////////////////////////

	$workload = 'sim.workload.authentication.StealthNoAuthTest';

	for ($seed = $startseed; $seed < $endseed; $seed++) {
		foreach ($services as $service) {
			foreach ($stealths as $stealth) {
				$cmds[] = "java -Xms150m -Xmx1000m -classpath ./bin sim.main.Simulator $i $workload auth $seed $service $stealth";
				$i++;
			}
		}
	}

	require('dispatch.inc.php');

?>
