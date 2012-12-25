<pre>
<?php

	set_time_limit(0);

	$log = '../bin/1000-loc.txt';

	$hops = array();
	$delays = array();
	$totaldelay = 0;
	$errors = 0;

	$fp = fopen($log, 'r');
	$line = '';

	if ($fp === false) {
		exit('bad file name');
	}

	echo "Total\tJoined OPlane\tIn OPlane\t% relays\tAverage Delay\tAverage Hops\n";

	while (!feof($fp)) {
		$lastLine = $line;
		$line = fgets ($fp);

		if ( preg_match ("/\bTotal Peers\b.* ([\d]+),.* ([\d]+), .* ([\d]+)/", $line, $regs) ) {
		    $total = $regs[1];
		    $joinOPlane = $regs[2];
		    $inOPlane = $regs[3];

				if ( preg_match ("/\brecv OPlaneJoinMessage\b[\D]*([\d]+)[\D]*([\d]+)ms/", $lastLine, $regs) ) {
					$hop = $regs[1];
		    	$delay = $regs[2];

		    	//Now work it all out
		    	$hops[] = $hop;
		    	$delays[] = $delay;

		    	$averageDelay = array_sum($delays) / count($delays);
		    	$averageHops = array_sum($hops) / count($hops);

		    	echo $total . "\t" . $joinOPlane . "\t" . $inOPlane . "\t" . (1 - $joinOPlane / $inOPlane) . "\t" . $averageDelay . "\t" . $averageHops . "\n";
		    } else {
		    	echo "ERROR\n";
		    }

		} else if ( strpos ($line, "ERROR") !== false ) {
				$errors++;
		}

	}

	echo 'Errors: ' . $errors;

	fclose($fp);


?></pre>