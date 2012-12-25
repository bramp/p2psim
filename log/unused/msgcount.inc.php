<?php

		function cmp($a, $b) {
			return count($a) - count($b);
		}

		function nonzero($var) {
			return($var > 0);
		}

	function open($type, $filename, $mode ) {
		if ($type == '.gz') {
			return gzopen($filename, $mode);
		} else if ($type == 'bz2') {
			return bzopen($filename, $mode);
		} else {
			return fopen($filename, $mode);
		}
	}

	function eof($type, $fp) {
		if ($type == '.gz') {
			return gzeof($fp);
		} else { // else if ($type == 'bz2') {
			return feof($fp);
		}
	}

	function gets($type, $fp) {
		if ($type == '.gz') {
			return gzgets($fp);
		} else {  //else if ($type == 'bz2') {
			return fgets($fp);
		}
	}

	function close($type, $fp) {
		if ($type == '.gz') {
			return gzgets($fp);
		} else if ($type == 'bz2') {
			return bzclose($fp);
		} else {
			return fclose($fp);
		}
	}

	function msgcount($log) {

		$type = substr($log, -3);

		$fp = open($type, $log, 'r');
		if ($fp === false) {
			exit('Couldn\'t open ' . $log . ' for reading');
		}

		$msgcounts = array();

		while (!eof($type, $fp)) {
			$line = gets ($type, $fp);

				// Match Messages
			if ( preg_match ("/: ([0-9A-F ]+)\b: ([recv|fwd|sent]+)\b[\s]*([A-Za-z]+)\(([\d]+)\b.*([\d]+) hops.* ([\d]+) ms.* ([\d]+) resent/", $line, $regs) ) {
			    //print_r($regs);

			    $id = trim($regs[1]);
			    $type = $regs[2];
			    $messagetype = $regs[3];
					$messageid = $regs[4];
			    $hop = $regs[5];
			    $delay = $regs[6];
			    $resent = $regs[7];

			    if ($type == 'recv') {

			    	if (!isset($msgcounts[$messagetype]))
			    		$msgcounts[$messagetype] = 0;

						$msgcounts[$messagetype]++;

				  } else if ($type == 'fwd') {


				  } else if ($type == 'sent') {

				  }

			} else if ( preg_match ("/: ([0-9A-F ]+)\b: ([aliv|fail]+)/", $line, $regs) ) {
					//print_r($regs);

					$id = trim($regs[1]);
			    $type = $regs[2]; //aliv or fail

			    // Match Packets
			} else if ( preg_match ("/: ([0-9A-F]+)\b: ([A-Za-z]+)\(([\d]+)\b.* ([0-9A-F]+)>([0-9A-F]+) size:([\d]+).*data:([A-Za-z]+)\(([\d]+)\b/", $line, $regs) ) {
				//print_r($regs);

		    $addy = $regs[1];
		    $packettype = $regs[2];
		    $packetid = $regs[3];
				$from = $regs[4];
		    $to = $regs[5];
		    $size = $regs[6];
		    $datatype = $regs[7];
		    $dataid = $regs[8];


			} else if ( strpos ($line, "ERROR") !== false ) {
					$errors++;
			} else {
				// This will print out any lines that didn't match
				//print_r($line);
			}

		}

		print_r($msgcounts);


	}

?>