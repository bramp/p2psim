<?php

$sizes = array(500);

// percentage of stealth nodes
$percentages = array(0.95,0);

$failTypes = array("Both","Service");

$churnTimes= array(5000,10000,15000,20000,25000,30000,45000,60000,90000,120000,180000,240000,300000,360000,420000,480000,540000,600000);

$keyss = array(1000);

$ks = array(1,3);

$getss = array(5);

$getTimes = array(360000);

$recoveryType = 1;

foreach($failTypes as $failType) {
	foreach($ks as $k) {
		for ($seed = 0; $seed < 5 ; $seed++) {
			foreach($percentages as $percentage) {
				foreach ($churnTimes as $churnTime) {
					foreach ($sizes as $size) {
						foreach($getTimes as $getTime) {
							foreach($keyss as $keys) {
								foreach($getss as $gets) {
									$stealth = (int)($size * $percentage);
									$service = $size - $stealth;
									// $percentage is passed in for logging purposes only
									// don't bother to run stealth churn / service churn for Pastry sims
									if (!(($percentage == 0 && ($failType == "Stealth" || $failType == "Service")) || (($churnTime > $churnTimes[0]) && $failType == "None"))) {
										passthru("nice -n 19 java -Xms300m -Xmx1400m -classpath ./bin sim.main.Simulator sim.workload.sigcomm.KeysTest Churn-$failType $seed $failType $service $stealth $churnTime $keys $k $gets $getTime $percentage $recoveryType");
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
