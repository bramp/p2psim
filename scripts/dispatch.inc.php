<?php

	if (!isset( $MAXPROCS ) )
		$MAXPROCS = 8; // How many processes should we run at once?

	if (!isset( $MAXCORES ) )
		$MAXCORES = $MAXPROCS; // How many cores are there?

	// ------------ No need to alter below here ----------------
	$master = array(); // Array of each process's handle
	$output = array(); // Array of each process's output
	$banner = array(); // Array of each process's banner (ie txt describing it)
	$cores = array(); // Array of which core each process is on
	$freecores = array(); // Which cores are free
	$count = 0;

	for ($i = 0; $i < $MAXCORES; $i++)
		$freecores[] = $i;

	function runNextProcess() {
		global $cmds, $master, $output, $banner, $count, $freecores, $cores;

		$cmd = array_shift($cmds);

		if (is_null( $cmd ))
			return false;

		// Pick a free core to use
		$core = array_shift ($freecores);

		// Always run nice infront of the application (and pipe stderr to stdout)
		//$cmd = 'nice -n 19 ' . $cmd . ' 2>&1';
		$cmd = 'taskset -c ' . $core . ' nice -n 19 ' . $cmd . ' 2>&1';

		// Start the next process
		$p = popen ( $cmd, 'r' );

		if ( $p === false ) {
			exit ( 'There was a error starting the application $cmd');
		}

		$master[ $p ] = $p;
		$output[ $p ] = "$cmd\n\n";
		$banner[ $p ] = "\n----- $count/" . (count($cmds) + $count) . " -----\n\n";
		$cores [ $p ] = $core;

		$count++;

		return $p;
	}

	// Setup the first few procs
	for ($i = 0; $i < $MAXPROCS; $i++) {
		if ( runNextProcess () === false)
			break;
	}

	while ( count ( $master ) > 0 ) {

		$read = $master;
		$write  = NULL;
		$except = NULL;

		$s = stream_select ( $read, $write, $except, 10 );

		foreach ($read as $p) {
			$ret = fread($p, 1024);

			// If we read nothing, then we might have reached the end of file
			if ($ret === false || strlen($ret) == 0) {

				// Now output everything this process has outputted
				echo $output [ $p ];
				echo $banner[ $p ];

				// This core is now free, add it back
				$freecores[] = $cores[ $p ];

				pclose( $p );

				unset( $master[ $p ] );
				unset( $output [ $p ] );
				unset( $banner [ $p ] );
				unset( $cores [ $p ] );

				// Now decide to add a new process
				runNextProcess();

			} else {
				$output [ $p ] .= $ret;
			}

		}

	}

?>
