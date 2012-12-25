<?php

 	$names = array('Stealth',  'Service', 'Both');

 	$totalPeers = array(1000);

	// 1% to 5% service nodes
	$ratios = array(0.01,0.05);

	$churnrates  = array(1000,8375,15750,23125,30500,37875 ,45250,52625,60000);

	foreach($churnrates as $churnrate) {
		foreach($ratios as $ratio) {
			foreach($names as $name) {
				foreach($totalPeers as $total) {
					for ($seed = 0; $seed < 3 ; $seed++) {
						passthru("nice -n 19 java -Xms300m -Xmx1400m -classpath ./bin sim.main.BigSimulator sim.workload.stealth.KeysChurnTypeTest $name $seed $name $total $ratio $churnrate");
    				}
  				}
  			}
		}
	}

?>
