<?php

$sizes = array(500);

// percentage of stealth nodes
$percentages = array(0.95);

$failTypes = array("Service");

$churnTimes= array(15000,30000,45000,60000,75000,90000,105000,120000,150000,180000,210000,240000,270000,300000,330000,360000,420000,480000,540000,600000);
//$churnTimes= array(500000,400000,300000,200000,100000,50000);

$keyss = array(1000);

$ks = array(1);

$getss = array(10);

$getTimes = array(100000);

// none, piggybacking, polling, rejoining
$recoveryTypes = array(3);


for ($seed = 0; $seed < 6 ; $seed++) {
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
											passthru("nice -n 19 java -Xms300m -Xmx1200m -classpath ./bin sim.main.Simulator sim.workload.sigcomm.KeysTest Keys-$failType $seed $failType $service $stealth $churnTime $keys $k $gets $getTime $percentage $recoveryType");
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