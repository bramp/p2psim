<?php

	$workloads = array( 'sim.workload.puredht.KeysTest');

	$peers20for1000 = array();
	for ($i = 3; $i < 23 ; $i++) {
		$peers20for1000[] = floor(pow(2,$i/2.2075));
	}

	$peers40for1000 = array();
	for ($i = 5; $i < 49 ; $i++) {
		$peers40for1000[] = floor(pow(2,$i/4.8160));
	}

	$peers20for2500 = array();
	for ($i = 2; $i < 22 ; $i++) {
		$peers20for2500[] = floor(pow(2,$i/1.8604));
	}

	$peers40for2500 = array();
	for ($i = 5; $i < 47 ; $i++) {
		$peers40for2500[] = floor(pow(2,$i/4.0752));
	}

	$peersarray = array(5, 10, 25, 50, 100, 150, 200, 250);

  $normals = array ( 5, 10, 25, 50, 75, 100 );
	$ratios = array (1.00);

	foreach($workloads as $workload) {
		$name = time();
		for ($seed = 0; $seed < 3 ; $seed++) {
			foreach ($peersarray as $peers) {
				//foreach ($normals as $normal) {
					//foreach ($ratios as $ratio) {
						passthru("java -Xms300m -Xmx1024m -classpath ./bin sim.main.Simulator $name $seed $workload $peers");
					//}
				//}
			}
		}
	}
?>