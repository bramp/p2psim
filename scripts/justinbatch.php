<?php
	$workloads = array( 'sim.workload.swarm.mobile.MovingPastryTest',
											'sim.workload.swarm.mobile.MovingSwarmTest',
										  'sim.workload.swarm.mobile.StaticPastryTest',
										  'sim.workload.swarm.mobile.StaticSwarmTest'
										  );

	$ratios = array(0.50, 0.75, 0.90, 0.99);

	$totalPeers = array();
	for ($i = 3; $i < 23 ; $i++) {
		$totalPeers[] = floor(pow(2,$i/2.2075));
	}

	$name = 'justin';

	for ($seed = 0; $seed < 3 ; $seed++) {
		foreach($totalPeers as $total) {
				foreach ($workloads as $workload) {
					foreach ($ratios as $ratio) {
						$low = (int)($total * $ratio);
						$high = $total - $low;

						passthru("nice -n 19 java -Xms300m -Xmx1400m -classpath ./bin sim.main.BigSimulator $workload $name $seed $high $low $ratio");
					}
				}
		}
	}