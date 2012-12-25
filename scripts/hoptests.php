<?php

	set_time_limit(0);

	/*
	$name = 'hoptests';
	$workload = 'sim.workload.puredht.KeysTest';

	for ($peers = 2 ; $peers <= 1100; $peers += 2) {
		for ($seed = 0; $seed < 6 ; $seed++) {
			passthru("nice -n 19 java -Xms300m -Xmx1200m -classpath ./bin sim.main.Simulator $workload $name $seed $peers");
		}
	}
	*/

$sizes = array(5,10,25,50,100,200,300,400,500,600,700,800,900,1000);

// percentage of stealth nodes
$percentages = array( 0 );

$failTypes = array('None');

$churnTimes= array(360000);

$keyss = array(1000);

$ks = array(1);

$getss = array(3);

$getTimes = array(360000);

for ($seed = 0; $seed < 6 ; $seed++) {
	foreach ($churnTimes as $churnTime) {
		foreach ($sizes as $size) {
			foreach($getTimes as $getTime) {
				foreach($keyss as $keys) {
					foreach($getss as $gets) {
						foreach($ks as $k) {
							foreach($failTypes as $failType) {
								foreach($percentages as $percentage) {
									$stealth = (int)($size * $percentage);
									$service = $size - $stealth;
									// $percentage is passed in for logging purposes only
									// don't bother to run stealth churn / service churn for Pastry sims
									if (!(($percentage == 0 && ($failType == 'Stealth' || $failType == 'Service')) || (($churnTime > $churnTimes[0]) && $failType == 'None'))) {
										passthru("nice -n 19 java -Xms300m -Xmx500m -classpath ./bin sim.main.Simulator sim.workload.sigcomm.KeysTest Valid-$failType $seed $failType $service $stealth $churnTime $keys $k $gets $getTime $percentage");
									}
								}
							}
						}
					}
				}
			}
		}
	}
}

?>

?>
