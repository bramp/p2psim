<?php
	set_time_limit(0);

	$tabs = `find "P:\~StaticStealthTest" -name "Global.tab"`;
	$tabs = explode("\n", trim($tabs));

	$stderr = fopen('php://stderr', 'w');

	//echo $topline . "\n";

	$results = array();
	$lines = 0;

	foreach($tabs as $log) {
		//echo $log . "\n";

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

	}	//foreach($tabs as $log)

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

		foreach ($results as $result) {
			$output .= $result[$i] . "\t";
		}

		echo trim($output) . "\n";
	}

?>
