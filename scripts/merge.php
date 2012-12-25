<?php

	// Merges a bunch of global.tab files together
	set_time_limit(0);
	ini_set('memory_limit', '-1');

	if ( $argc < 2 ) {
		exit( "php merge.php <files>\n" );
	}


	//$tabs = `find "P:\~StaticStealthTest" -name "Global.tab"`;
	//$tabs = explode("\n", trim($tabs));

	$tabs = array();
	for ($i = 1; $i < count($argv); $i++)
		$tabs = array_merge ( $tabs,  glob ( $argv[$i] ) );

	$stderr = fopen('php://stderr', 'w');

	//echo $topline . "\n";

	if ( count($tabs) == 0 ) {
		exit( 'please specify atleast one file' );
	}

	$results = array();
	$lines = 0;

	foreach($tabs as $log) {
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
						$a = array();

						$zeros = $lines;

						// We might need to zero pad it
						while ($zeros > 0) {
							$zeros--;
							$a[] = '0';
						}

						$results[ $header ] = $a;
					}

					$results[ $header ][] = trim($item);
				}

				$lines++;
			}

			// Check that all items have zero padded on the end
			foreach ($results as $header => $result) {
				$zeros = $lines - count($result);
				while ( $zeros > 0 ) {
					$results[ $header ][] = '0';
					$zeros--;
				}
			}

		}

	}	//foreach($tabs as $log)

	// Now sort the results if there is a order coloumn
	if ( isset( $results['Sim_Order'] ) ) {
		// Do a nasty eval hack!
		$evalstr = 'array_multisort(';
		$evalstr .= '$results[\'Sim_Order\'], SORT_ASC, SORT_NUMERIC, ';

		foreach ($results as $header => $result) {
			$evalstr .= '$results[\'' . $header . '\'], ';
		}

		$evalstr = substr( $evalstr, 0, strlen($evalstr) - 2 );
		$evalstr .= ');';

		eval ( $evalstr );
	}


	// Sort by the header rows
	//var_dump ( $results );
	ksort( $results , SORT_STRING );

	$output = '';
	foreach ($results as $header => $result) {
		$output .= $header . "\t";

		if (count($result) != $lines) {
			fwrite($stderr, "Not enough results " . count($result) . " != " . $lines . "\n");
		}
	}

	echo trim($output) . "\n";

	for ($i = 0; $i < $lines; $i++) {
		$output = '';

		foreach ($results as $header => $result) {
			$output .= $result[$i] . "\t";
		}

		echo trim($output) . "\n";
	}

?>
