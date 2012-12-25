<?php

$file = 'P:\old_logs\Justin 6\justin-sim.workload.swarm.mobile.StaticSwarmTest\Global.tab';

$row = 1;
$handle = fopen($file, 'r');

$headers = fgetcsv($handle, 10000000, "\t");
$data = array();

foreach ($headers as $header) {
	$data[$header] = array();
}

$fields = array('RTBestChoice', 'RTDiff', 'RTDiffAbs');
$results = array();
$count = 0;

// Loop each line
while (($line = fgetcsv($handle, 10000000, "\t")) !== FALSE) {

	$temp = array();

	foreach ($fields as $field) {
		$temp[$field] = array();
	}

  // Loop each col
  foreach ($line as $idx=>$cell) {
		$data[$headers[$idx]][] = $cell;

		foreach ($fields as $field) {

			if (preg_match ("/$field\(([0-9]+)\)_Avg/", $headers[$idx], $regs)) {

				//if ($cell != 0) {
					//$temp['RTBestChoice'][time] = value
					$temp[$field][$regs[1]] = $cell;
				//}

			}
  	}
  }

	// Remove the times that contain blank data
	$times = array_keys ($temp[$fields[0]]);
	foreach ($times as $time) {
		$valid = false;
		foreach ($fields as $field) {
			if ($temp[$field][$time] != 0) {
				$valid = true;
				break;
			}
		}

		if (!$valid) {
			foreach ($fields as $field) {
				unset($temp[$field][$time]);
			}
		}
	}

	// Sort temp
	foreach ($fields as $field) {
		ksort ( $temp[$field] , SORT_NUMERIC);
	}

	$results[] = $temp;
  $count++;
}

//print_r($data);
fclose($handle);

//print_r($results);

$line = 0;

foreach($results as $result) {
	$line++;
	echo "Line: $line\n";

	// Print out the times
	echo "times = [";
	foreach ($result[$fields[0]] as $idx => $numbers)
		echo "$idx ";
	echo "]\n";

	foreach($fields as $field) {
		// Print out the values
		echo "$field = [";
		foreach ($result[$field] as $numbers)
			echo "$numbers ";
		echo "]\n";
	}

	echo "\n";
}


?>