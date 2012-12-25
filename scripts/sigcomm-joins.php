<?php

// 0 to 1000 nodes, 20 steps
//$sizes = array();
//for ($i = 3; $i < 23 ; $i++) {
//	$sizes[] = floor(pow(2,$i/2.2075));
//}

$sizes = array();
for ($i = 3; $i < 17 ; $i++) {
	$sizes[] = floor(pow(2,$i/2.2075));
}

// percentage of stealth nodes
$percentages = array(0,0.5,0.80,0.95);

// mean inter-join time
$jointimes = array(60000);

for ($seed = 90; $seed < 250; $seed++) {
	foreach ($jointimes as $jointime) {
		foreach($sizes as $size) {
			foreach($percentages as $percentage) {
				$stealth = (int)($size * $percentage);
				$service = $size - $stealth;
				// $percentage is passed in for logging purposes only
				passthru("nice -n 19 java -Xms300m -Xmx1000m -classpath ./bin sim.main.Simulator sim.workload.sigcomm.JoinsTest Joins $seed $service $stealth $jointime $percentage");
			}
		}
	}
}

?>