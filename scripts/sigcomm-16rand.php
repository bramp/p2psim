<?php

	set_time_limit(0);

$sizes = array();
for ($i=5; $i < 49; $i++) {
	$sizes[] = floor(pow(2,$i/4.8160));
}

$sizes = array_values(array_unique ( $sizes ));

	$name = '16-';
	$workload = 'sim.workload.stealth.KeysTest2';

	foreach ($sizes as $peers) {
		for ($seed = 3; $seed < 9; $seed++) {

			$stealth = (int)($peers * 0.95);
			$service = $peers - $stealth;

			$cmd = "nice -n 19 java -Xms300m -Xmx1200m -classpath ./bin sim.main.Simulator $workload $name $seed $service $stealth";
			//echo "$cmd\n";
			passthru($cmd);
		}
	}

?>