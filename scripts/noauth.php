<?php

	$workload = 'sim.workload.authentication.StealthNoAuthTest';

	//$gatewayRatios = array(0, 0.01, 0.05, 0.10, 0.25, 0.5, 0.75, 1);
	$services = array(10, 25, 50, 75, 100);
	$stealths = array(10, 25, 50, 75, 100, 150, 250, 500, 750, 1000);

	for ($seed = 0; $seed < 3 ; $seed++) {
		//foreach ($gatewayRatios as $gatewayRatio) {
			foreach ($services as $service) {
				foreach ($stealths as $stealth) {

					//$gateway = floor($service * $gatewayRatio);
					// Must have atleast one!
					//$gateway = max(1, $gateway);

					/*
					// Can't have more gateways than service
					$gateway = min($service, $gateway);

					// Must have atleast one gateway/service/stealth node
					$gateway = max(1, $gateway);
					$service = max(1, $service);
					$stealth = max(1, $stealth);
					*/

					passthru("nice -n 19 java -Xms300m -Xmx1500m -classpath ./bin sim.main.Simulator $workload auth $seed $service $stealth");
				}
			}
		//}
	}

?>