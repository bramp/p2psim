<?php

	$startseed = 3;
	$endseed = 9;

	///////////////////////////////////////////////////////////////////

	$workload = 'sim.workload.authentication.StealthNoAuthTest';

	//$services = array(10, 25, 50, 75, 100);
	//$stealths = array(10, 25, 50, 75, 100, 150, 250, 500, 750, 1000);
	$services = array(10, 100);
	$stealths = array(500);

	for ($seed = $startseed; $seed < $endseed; $seed++) {
		foreach ($services as $service) {
			foreach ($stealths as $stealth) {
				//passthru("nice -n 19 java -Xms300m -Xmx1500m -classpath ./bin sim.main.Simulator $workload auth $seed $service $stealth");
				passthru("nice -n 19 java -Xms300m -Xmx1000m -classpath ./bin sim.main.Simulator $workload auth $seed $service $stealth");
			}
		}
	}

	//////////////////////////////////////////////////////////////////

	$workload = 'sim.workload.authentication.StealthAuthTest';

	//$gatewayRatios = array(0, 0.01, 0.05, 0.10, 0.25, 0.5, 0.75, 1);
	//$services = array(10, 25, 50, 75, 100);
	//$stealths = array(10, 25, 50, 75, 100, 150, 250, 500, 750, 1000);

	$gatewayRatios = array(0, 0.01, 0.05, 0.10, 0.25, 0.5, 0.75, 1);
	$services = array(10, 100);
	$stealths = array(500);
	$truefalse = array('false');

	for ($seed = $startseed; $seed < $endseed; $seed++) {
		foreach ($gatewayRatios as $gatewayRatio) {
			foreach ($services as $service) {
				foreach ($stealths as $stealth) {
					foreach ($truefalse as $perhop) {
						foreach ($truefalse as $chain) {

							// Must have atleast one!
							$gateway = floor($service * $gatewayRatio);
							$gateway = max(1, $gateway);

							passthru("nice -n 19 java -Xms300m -Xmx1000m -classpath ./bin sim.main.Simulator $workload auth $seed $gateway $service $stealth $perhop $chain $gatewayRatio");
						}
					}
				}
			}
		}
	}

?>