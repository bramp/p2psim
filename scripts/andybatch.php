<?php

	$workloads = array( 'sim.workload.mobile.StaticPastryTest',
											'sim.workload.mobile.StaticStealthTest',

											'sim.workload.mobile.MovingPastryTest',
										  'sim.workload.mobile.MovingStealthTest',

										  //'sim.workload.mobile.HardMovingPastryTest',
										  'sim.workload.mobile.HardMovingStealthTest'
										  );

	$stealthratios = array(0.99);

	$totalPeers = array();
	for ($i = 3; $i < 23 ; $i++) {
		$totalPeers[] = floor(pow(2,$i/2.2075));
	}

	$name = '1pc-fixed-service';

	for ($seed = 0; $seed < 3 ; $seed++) {
		foreach($workloads as $workload) {
			foreach ($stealthratios as $stealthratio) {
				foreach ($totalPeers as $total) {

					$wifistealth = (int)($total * $stealthratio);
					$fixedstealth = 0;

					$wifiservice = 0;
					$fixedservice = $total - $wifistealth;

					passthru("nice -n 19 java -Xms300m -Xmx1400m -classpath ./bin sim.main.BigSimulator $workload $name $seed $wifistealth $fixedstealth $wifiservice $fixedservice");
				}
			}
		}
	}

	/**
	SECOND SET
	**/

	$stealthratios = array(0.99, 0.95);

	$totalPeers = array();
	for ($i = 3; $i < 23 ; $i++) {
		$totalPeers[] = floor(pow(2,$i/2.2075));
	}

	$name = '1pc-all-mobile';

	for ($seed = 0; $seed < 3 ; $seed++) {
		foreach($workloads as $workload) {
			foreach ($stealthratios as $stealthratio) {
				foreach ($totalPeers as $total) {

					$wifistealth = (int)($total * $stealthratio);
					$fixedstealth = 0;

					$wifiservice = $total - $wifistealth;
					$fixedservice = 0;

					passthru("nice -n 19 java -Xms300m -Xmx1400m -classpath ./bin sim.main.BigSimulator $workload $name $seed $wifistealth $fixedstealth $wifiservice $fixedservice");
				}
			}
		}
	}
?>