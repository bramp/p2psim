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

	function joincount($log) {
		global $noderecvs, $nodefwds, $nodesents, $nodeputs;

		$type = substr($log, -3);

		$logout = $log . '.join.tab';

		$filesize = 0;

		// Bail if the output file already exists
		//if (file_exists($logout)) {
		//	echo "skipping $logout\n";
		//	return;
		//}

		$fpout = fopen($logout, 'w');
		if ($fpout === false) {
			exit('Couldn\'t open ' . $logout . ' for writing');
		}

		$fp = open($type, $log, 'r');
		if ($fp === false) {
			exit('Couldn\'t open ' . $log . ' for reading');
		}

		$errors = 0;

		$joinmessages = 0;
		$joinfinishmessages = 0;
		$pairsmessages = 0;
		$pairs = 0;

		$totaljoinmessages = 0;
		$totaljoinfinishmessages = 0;
		$totalpairsmessages = 0;
		$totalpairs = 0;

		while (!eof($type, $fp)) {
			$line = gets ($type, $fp);
			$filesize += strlen($line);

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

				  } else if ($type == 'fwd') {

						if ($messagetype == 'JoinMessage')
							$totaljoinmessages++;

						if ($messagetype == 'JoinFinishedMessage')
							$totaljoinfinishmessages++;

						if ($messagetype == 'PairsMessage')
							$totalpairsmessages++;

						if ($messagetype == 'JoinFinishedMessage' || $messagetype == 'PairsMessage') {
							preg_match ("/([\d]+) pairs/", $line, $regs);
							$totalpairs += $regs[1];
						}

				  } else if ($type == 'sent') {

						if ($messagetype == 'JoinMessage') {
							$joinmessages++;
							$totaljoinmessages++;
						}

						if ($messagetype == 'JoinFinishedMessage') {
							$joinfinishmessages++;
							$totaljoinfinishmessages++;
						}

						if ($messagetype == 'PairsMessage') {
							$pairsmessages++;
							$totalpairsmessages++;
						}

						if ($messagetype == 'JoinFinishedMessage' || $messagetype == 'PairsMessage') {
							preg_match ("/([\d]+) pairs/", $line, $regs);
							$pairs += $regs[1];
							$totalpairs += $regs[1];
						}

				  }

			} else if ( strpos ($line, "ERROR") !== false ) {
					$errors++;
			} else {
				// This will print out any lines that didn't match
				//print_r($line);
			}

		}

		//print_r($resentValues);

		/*
		echo "Recv Hops\n";
		ksort ($hops);
		print_r($hops);

		echo "Recv Delays\n";
		ksort ($delays);
		print_r($delays);
		*/
		/*
		echo "Recv per node\n";
		ksort ($noderecvs);
		print_r($noderecvs);

		echo "Fwds per node\n";
		ksort ($nodefwds);
		print_r($nodefwds);
		*/

		//print_r($linksCount);
		//print_r($linksDups);

		//echo 'Sent: ' . $sent . ' Fwd: ' . $fwd . ' Recv: ' . $recv . ' Resents: ' . $resents . ' Put: ' . $put;

		// Print out file data, (ie the long lists of numbers)
		$cols = array(/* 'header' => $values */
									'JoinMessage' => array($joinmessages),
									'JoinFinishedMessage' => array($joinfinishmessages),
									'PairsMessage' => array($pairsmessages),
									'Pairs' => array($pairs),
									'TotalJoinMessage' => array($totaljoinmessages),
									'TotalJoinFinishedMessage' => array($totaljoinfinishmessages),
									'TotalPairsMessage' => array($totalpairsmessages),
									'TotalPairs' => array($totalpairs),
									);

		// Sort the cols so that the longest arrays are printed first
		//usort ( $cols, 'cmp' );

		$line = '';
		// Print the headers
		foreach ($cols as $header => $col) {
			$line .= $header . "\t";
		}
		fwrite( $fpout,  trim($line) . "\n" );

		$max = 0; // Find the longest col
		foreach ($cols as $col) {
			if (count($col) > $max)
				$max = count($col);
		}

		// Print the values (in cols)
		for ($i = 0; $i < $max; $i++) {
			$line = '';

			foreach ($cols as $col) {
				if ($i < count($col)) {
					$line .= $col[$i] . "\t";
				} else {
					$line .= " \t";
				}
			}
			fwrite( $fpout, rtrim($line) . "\n" );
		}

		//echo 'Written ' . $logout . "\n";
		fclose($fpout);

	}

?>