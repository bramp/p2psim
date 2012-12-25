<?php

	for ($b = 12 ; $b < 17 ; $b++) {
		for ($seed = 0; $seed < 3 ; $seed++) {
					passthru("java -Xms300m -Xmx1400m -classpath ./bin sim.main.BigSimulator sim.workload.routing.RoutingTest btest $seed $b");
    }
	}

?>
