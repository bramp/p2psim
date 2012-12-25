<?php

$sizes = array(10,25,50,75,100,150,250,500,750,1000);

// percentage of stealth nodes
$percentages = array(0.95);

$failTypes = array("None","Stealth","Service","Both");

$churnTimes= array(360000);

$keyss = array(1000);

$ks = array(1,3);

$getss = array(3);

$getTimes = array(360000);

// none, piggybacking, polling, rejoining
$recoveryTypes = array(0,1,2,3);

for ($seed = 0; $seed < 3 ; $seed++) {
	foreach ($churnTimes as $churnTime) {
		foreach ($sizes as $size) {
			foreach($getTimes as $getTime) {
				foreach($keyss as $keys) {
					foreach($getss as $gets) {
						foreach($ks as $k) {
							foreach($failTypes as $failType) {
								foreach($recoveryTypes as $recoveryType) {
									foreach($percentages as $percentage) {
										$stealth = (int)($size * $percentage);
										$service = $size - $stealth;
										// $percentage is passed in for logging purposes only
										// don't bother to run stealth churn / service churn for Pastry sims
										if (!(($percentage == 0 && ($failType == "Stealth" || $failType == "Service")) || (($churnTime > $churnTimes[0]) && $failType == "None"))) {
											passthru("nice -n 19 java -Xms300m -Xmx1400m -classpath ./bin sim.main.Simulator sim.workload.sigcomm.KeysTest Keys-$failType $seed $failType $service $stealth $churnTime $keys $k $gets $getTime $percentage $recoveryType");
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
}

?>