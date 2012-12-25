<?php

set_time_limit(0);

if ($_SERVER['argc'] < 2)
	exit("\nphp " . $_SERVER["argv"][0] . " {path to tab files}\n");

$path = $_SERVER['argv'][1];

function mean($array) {
	return array_sum($array) / count($array);
}

$stats = array();

// Directory to scan for files
$d = dir($path);

while (false !== ($entry = $d->read())) {


	//output-1-1-0.txt.gz.tab
	$regex = "/output-([\d]+)-([\d]+)-([\d]+).txt.gz.tab/";
	if ( preg_match ($regex, $entry, $regs) ) {

		$nodes = $regs[1] + $regs[2];

		$data = array();

		// Now open the file
		$file = file($d->path . "\\" . $entry);

		// Pop off theheader
		$headers = array_shift ($file);
		$headers = explode("\t", $headers);

		foreach ($headers as &$header) {
			$header = trim($header);
			$data[ $header ] = array();
		}

		foreach ($file as $line) {
			$line = explode("\t", $line);
			foreach ($line as $i => $element) {
				$element = trim($element);

				if (strlen($element) > 0)
					$data[ $headers[$i] ] [] = $element;
			}
		}

		// We have parsed this file, now store the stats
		$stats[] = array ($nodes, mean($data['nodehandles']), mean($data['linkcount_uni']), max($data['nodehandles']), max($data['linkcount_uni']));
	}

}

$d->close();

function cmp($a, $b) {
   return $a[0] - $b[0];
}

//sort by the first col
usort ($stats, "cmp");

// now print all the results
foreach ($stats as $stat) {
	foreach ($stat as $x) {
		echo $x . "\t";
	}
	echo "\n";
}


?>