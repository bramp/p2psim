<?php

		function cmp($a, $b) {
			return count($a) - count($b);
		}

		function nonzero($var) {
			return($var > 0);
		}

		function addID($id) {
			global $noderecvs, $nodefwds, $nodesents, $nodeputs, $nodehandle;

			// Check if we have seen this ID before
	    if (!isset($noderecvs[$id])) {
	    	$noderecvs[$id] = 0; // If not set it to 0
	    	$nodefwds[$id] = 0;
	    	$nodesents[$id] = 0;
	    	$nodeputs[$id] = 0;
	    	$nodehandle[$id] = 0;
	    }
		}

		function messageFinished(&$messageid) {
			global $packetlog, $linksDups;

			//Stop tracking this message
			$path = &$packetlog[$messageid];

			//Check for dups
			$dups = array();
			$len = count($path) - 1;

			for ($i = 0; $i < $len; $i++) {

				if (strcmp($path[$i], $path[$i + 1]) >= 0) {
					$idx = $path[$i] . '-' . $path[$i + 1];
				} else {
					$idx = $path[$i + 1] . '-' . $path[$i];
				}

				if (!isset($dups[$idx]))
					$dups[$idx] = 0;
				else
					$dups[$idx]++;
			}

			foreach ($dups as $idx => $dup) {
				if (!isset($linksDups[$idx]))
					$linksDups[$idx] = 0;

				$linksDups[$idx] += $dup;
			}

			//echo count($packetlog) . "\n"; //"- " . min( array_keys ($packetlog) ) . "\n";
			unset($packetlog[$messageid]);
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

		$nodehandle = array();
		$nodesents = array(); // Number of packets each node sends
		$nodefwds = array(); // Number of packet each node fwds
		$noderecvs = array(); // Number of packets each node recv
		$nodeputs = array(); // Number of times a node receives a put
		$packetlog = array(); // Tracks the message while its moving

	function hopcount($log) {
		global $noderecvs, $nodefwds, $nodesents, $nodeputs, $nodehandle, $packetlog;

		$type = substr($log, -3);

		$nodehandle = array();
		$nodesents = array();	$nodefwds = array();
		$noderecvs = array(); $nodeputs = array();
		$packetlog = array();

		$logout = $log . '.tab';

		//$hops = array(); // The count of each hops (histogram binning)
		//$delays = array(); // The count of each delays (histogram binning)

		$hopValues = array(); // The number of hops on a packet when recv'ed
		$delayValues = array(); // The delay on a packet when recv'ed
		$resentValues = array(); // The number of resends of a packet when recv'ed

		$hosts = array();
		$linksCount = array();
		$linksBW = array();
		$linksDups = array();

		$totaldelay = 0;
		$errors = 0;
		$sent = 0;
		$resent = 0;
		$recv = 0;
		$fwd = 0;
		$put = 0;

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
			    $totaldelay += $delay;

					addID($id);

			    if ($type == 'recv') {

			   		$recv++;

				    $noderecvs[$id]++;

				    //if (!isset($hops[$hop])) {
				    //	$hops[$hop] = 0;
				    //}
				    //$hops[$hop]++;
				    $hopValues[] = $hop;

						// Store the delay
				    //$delay = round($delay, -2);
						//if (!isset($delays[$delay])) {
				    //	$delays[$delay] = 0;
				    //}
				    //$delays[$delay]++;
			      $delayValues[] = $delay;

			      // Store the resent value
			      $resentValues[] = $resent;

			      if ($messagetype == 'PutMessage') {
			      	$nodeputs[$id]++;
			      	$put++;
	          }

	         	$nodehandle[$id]++;

						messageFinished($messageid);

				  } else if ($type == 'fwd') {
				    $nodefwds[$id]++;
				   	$fwd++;

				   	$nodehandle[$id]++;

				  } else if ($type == 'sent') {
				  	$nodesents[$id]++;
				  	$sent++;

				  	// Track this packet's movements
				  	$packetlog[$messageid] = array();
				  }

			} else if ( preg_match ("/: ([0-9A-F ]+)\b: ([aliv|fail]+)/", $line, $regs) ) {
					//print_r($regs);

					$id = trim($regs[1]);
			    $type = $regs[2]; //aliv or fail

			    addID($id);

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

		    if ( isset($hosts[$packetid]) ) {
					$oldhost = $hosts[$packetid];

					$idx = $addy . '>' . $oldhost;

					// Record a count on this link
					if ( !isset( $linksCount[$idx] )) {
						$linksCount[$idx] = 0;
						$linksBW[$idx] = 0;
					}

					$linksCount[$idx]++;
					$linksBW[$idx]+=$size;
		    }

		    // Insert the packetid here
		    $hosts[$packetid] = $addy;

		    // Track the path of this message (if we are recording it)
		    if (isset($packetlog[$dataid])) {
  				$packetlog[$dataid][] = $addy;
		    }

			} else if ( strpos ($line, "ERROR") !== false ) {
					$errors++;
			} else {
				// This will print out any lines that didn't match
				//print_r($line);
			}

		}

		// Count only the values greater than zero
		$resents = count ( array_filter ($resentValues, 'nonzero') );

		//echo 'Sent: ' . $sent . ' Fwd: ' . $fwd . ' Recv: ' . $recv . ' Resents: ' . $resents . ' Put: ' . $put;

		//$sum = 0;
		//$total = 0;
		//foreach($hops as $hop=>$count) {
		//	$sum += $hop * $count;
		//	$total += $count;
		//}

		//echo "\n\n";

		//if ($total == 0)
		//	$total = -1;

		//echo 'Total Messages: ' . $total . ' Average Hops: ' . ($sum / $total) . ' Average Delay: ' . ($totaldelay / $total) . "\n";
		//echo 'Lost Messages: ' . $errors . ' (' . round(($errors / $total * 100), 1) . '%)' . "\n";
		//echo 'Log size: ' . $filesize . " bytes\n";
		close($type, $fp);

		// Remove the ID as keys from node{recvs,fwds}
		$noderecvs = array_values($noderecvs);
		$nodefwds = array_values($nodefwds);
		$nodesents = array_values($nodesents);
		$nodeputs = array_values($nodeputs);

		$nodehandle = array_values($nodehandle);

		$linksCount1 = array();
		$linksBW1 = array();

		// Treats both link directions as one whole link
		foreach ($linksCount as $key => $link) {

			$keys = split('>', $key);

			// Swap the index around if needed
			if (strcmp($keys[0], $keys[1]) >= 0) {
				$idx = $keys[0] . '-' . $keys[1];
			} else {
				$idx = $keys[1] . '-' . $keys[0];
			}

			if ( !isset($linksDups[$idx]) )
				$linksDups[$idx] = 0;

			// Record a count on this link
			if ( !isset( $linksCount1[$idx] )) {
				$linksCount1[$idx] = 0;
				$linksBW1[$idx] = 0;
			}

			$linksCount1[$idx]+=$linksCount[$key];
			$linksBW1[$idx]+=$linksBW[$key];
	  }
		$linksCount1 = array_values($linksCount1);
		$linksBW1 = array_values($linksBW1);

		// Treats links as 2 different links (1 in each direction)
		$linksCount2 = array_values($linksCount);
		$linksBW2 = array_values($linksBW);

		$linksDups = array_values($linksDups);

		// Print out file data, (ie the long lists of numbers)
		$cols = array(/* 'header' => $values */
									// Done per node
									'delays' => $delayValues,       // Number of messages a node sents
									'nodesents' => $nodesents,      // Number of messages a node sents
									'nodefwds' => $nodefwds,        // Number of messages a node fwds
									'noderecvs' => $noderecvs,      // Number of messages a node recvs
									'nodeputs' => $nodeputs,        // Number of messages a node puts
									'nodehandles' => $nodehandle,   // Number of message a node recvs and fwds

									// Done per message
									'hops' => $hopValues,           // Number of hops a message takes
									'resents' => $resentValues,     // Number of times a message is resent

									// Done per link
									'linkcount_uni' => $linksCount1,// The total number of packets a link sees in one direction
									'linkbw_uni' => $linksBW1,      // The total bandwidth used on a link in one direction
									'linkcount_bi' => $linksCount2, // The total number of packets a link sees
									'linkbw_bi' => $linksBW2,       // The total bandwidth used on a link
									'link_dup' => $linksDups,       // Number of duplicate messages a link sees
									);

		// Sort the cols so that the longest arrays are printed first
		//usort ( $cols, 'cmp' );

		$line = '';
		// Print the headers
		$max = 0; // Find the longest col
		$headers = array();
		foreach ($cols as $header => &$col) {
			$line .= $header . "\t";

			if (count($col) > $max)
				$max = count($col);

			$headers[$header] = count($col);
		}

		fwrite( $fpout,  trim($line) . "\n" );

		// Print the values (in cols)
		for ($i = 0; $i < $max; $i++) {
			$line = '';

			foreach ($headers as $header => $count) {
				if ($i < $count) {
					$line .= $cols[$header][$i] . "\t";
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