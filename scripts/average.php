<?php

	// Averages the results in a tab file
	set_time_limit(0);
	ini_set('memory_limit', '-1');

	if ( $argc < 2 ) {
		exit( "php merge.php <file>\n" );
	}

	$log = $argv[1];

	$stderr = fopen('php://stderr', 'w');

	$results = array();
	$lines = 0;

	//echo $log . "\n";
	fwrite($stderr, "$log\n");

	$file = file($log);

	if (isset($file[0])) {
		$headers = explode("\t", trim($file[0]) );
		unset($file[0]);

		foreach ($file as $line) {
			$line = explode("\t", $line);
			for ($i = 0; $i < count($line); $i++) {
				$item = $line[$i];
				$header = $headers[$i];

				// This is a new field
				if (!isset($results[ $header ])) {
					$results[ $header ] = array();

					$zeros = $lines;

					// We might need to zero pad it
					while ($zeros > 0) {
						$zeros--;
						$results[ $header ][] = '0';
					}

				}

				$results[ $header ][] = trim($item);
			}

			$lines++;
		}

		// Check that all items are zero padded
		foreach ($results as &$result) {
			while (count($result) < $lines)
				$result[] = '0';
		}
	}

	// HACK delete some un needed rows
	foreach ($results as $header => $result) {
		if ( substr( $header, 0, 8 ) == 'DHT_Good' || substr( $header, 0, 7 ) == 'DHT_Bad' )
			unset ( $results [ $header ] );
	}

	// Sort by the header rows
	ksort( $results , SORT_STRING );

	$newresults = array();
	$newlines = 0;

	// Fine all the headers that we are going to group by
	$headers = array_keys ( $results );
	$groupby = array();
	foreach ($headers as $header) {
		if ( substr($header, 0, 9) == 'Sim_Param' )
			$groupby[] = $header;
		else if ( $header == 'Sim_Name' || $header == 'Sim_Workload' )
			$groupby[] = $header;

		$newresults[ $header ] = array();
	}

	if ( count( $groupby ) == 0 ) {
		exit ( 'Groupby must include atleast one field' );
	}

	// Now go through each row finding groups
	$hasmatched = array();

	for ($i = 0; $i < $lines; $i++) {
		$output = '';
		$matchs = array($i);

		if ( in_array ( $i, $hasmatched ) )
			continue;

		for ($ii = $i + 1; $ii < $lines; $ii++) {

			if ( in_array ( $ii, $hasmatched ) )
				continue;

			$match = true;

			foreach ($groupby as $group) {
				if ( $results[ $group ][$i] != $results[ $group ][$ii] ) {
					$match = false;
					break;
				}
			}

			if ( $match ) {
				$matchs[] = $ii;
				$hasmatched[] = $ii;
			}
		}

		// now average all the matchs
		foreach ($results as $header => $result) {

			$total = 0;
			foreach ($matchs as $match) {
				$total += $result[ $match ];
			}

			$newresults [ $header ] [] = $total / count ( $matchs );
		}

		$newlines++;
	}

	$output = '';
	foreach ($newresults as $header => $result) {
		$output .= $header . "\t";

		if (count($result) != $newlines) {
			exit("Not enough results " . count($result) . " != " . $newlines . "\n");
		}

	}

	echo trim($output) . "\n";

	for ($i = 0; $i < $newlines; $i++) {
		$output = '';

		foreach ($newresults as $header => $result) {
			$output .= $result[$i] . "\t";
		}

		echo trim($output) . "\n";
	}

?>
