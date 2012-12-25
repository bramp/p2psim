<?php

	$workloads = array( 'sim.workload.mobile.StaticPastryTest',
											'sim.workload.mobile.StaticStealthTest',

											'sim.workload.mobile.MovingPastryTest',
										  'sim.workload.mobile.MovingStealthTest',

										  'sim.workload.mobile.HardMovingPastryTest',
										  'sim.workload.mobile.HardMovingStealthTest'
										  );

	//$normalPeers = array(5, 10, 25, 50, 75, 100, 150, 250);
	//$stealthPeers = array( /*10, 25, 50, 75, 100, 150, 250, 500, 750, 1000,*/ 1500, 2000, 2500 /*, 5000, 10000, 20000 */);

	$totalPeers = array();
	for ($i = 3; $i < 23 ; $i++) {
		$totalPeers[] = floor(pow(2,$i/2.2075));
	}

	//$ratios = array(0.95, 0.90, 0.75, 0.50, 0.25, 0.10, 0.05);
	$ratios = array(0.90, 0.75, 0.25, 0.10);

	$name = time();

	foreach($workloads as $workload) {
		for ($seed = 0; $seed < 3 ; $seed++) {
			foreach ($ratios as $ratio) {
					foreach ($totalPeers as $total) {
						$wifi = (int)($ratio * $total);
						$wired = $total - $wifi;

						passthru("nice -n 19 java -Xms300m -Xmx1500m -classpath ./bin sim.main.BigSimulator $workload $seed $wifi $wired");
					}
				}
		}
	}
?>