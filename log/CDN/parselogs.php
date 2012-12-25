<?php

	//if ($argc < 2) {
	//	exit( $argv[0] . " (log file)\n" );
	//}

	// File to parse some useful stats from
	//$logfile =  'C:\Documents and Settings\brampton\access_log';
	//$logfile =  $argv[1];
	//$fp = fopen($logfile, 'r');
	$fp = STDIN;

	set_time_limit(0);

	function __autoload($class_name) {
	   require_once $class_name . '.class.php';
	}

	if (!$fp) {
		exit('Could not open input file');
	}

	//$state = new State();

	$tmp = array();
	for ($i = 100; $i<=10600; $i+=100) {
		$tmp[] = 'output/' . $i . '-0_hotspots';
	}

	$tmp = array();
	for ($i = 0.0; $i<=1.0; $i+=0.01) {
		$tmp[] = 'output/0_hotspots_' . $i;
	}

	$parsers = array(
		//new ViewSessions(),
		//new HotSpots(),

		new HitMissRatio($tmp),
	);


	// Setup the parsers
	foreach ($parsers as &$parser) {

		if (!($parser instanceof ParserInterface))
			exit('All parsers must implement ParserInterface');

		$parser->init( );
	}

	while (!feof($fp)) {

		$line = trim(fgets($fp));

		if (strlen($line) == 0)
			continue;


		// Unpack the record
		//6286147: 0064: Request stopped From:00C4 at 6285147 for Media(21 length:9150304365) Start:4765250000 Pos:4765375000
		//$pattern = '/^([\S]+)\s-\s[^\[]+\s\[([^\]]+)\]\s\"(GET|HEAD|POST|PUT|DELETE|TRACE|OPTIONS|CONNECT|[^\"\s]+)\s?(.*)\"\s([\d]+)\s([\d-]+)(\s )?$/';
		$pattern = '/([\d]+): ([\dA-F]+): Request stopped From:([\dA-F]+) at ([\d]+) for Media\(([\d]+)[^\)]+\) Start:([\d]+) Pos:([\d]+)$/';
		if ( !preg_match ( $pattern, $line , $matchs) )
			continue;

		$record = array();
		$record['ip'] = $matchs[3];
		$record['server'] = $matchs[2];
		$record['time'] = (int)( $matchs[1] / 1000 );
		//$record['timeString'] = date ('F j, Y, g:i a', $record['time']);

		$record['object'] = $matchs[5];
		$record['startRealTime'] = (int)( $matchs[4] / 1000 );
		$record['endRealTime'] = (int)( $matchs[1] / 1000 );

		// TODO change these hardcoded values
		$byterate = (1000000 / 8);
		$record['startMediaTime'] = (int)( $matchs[6] / $byterate );
		$record['endMediaTime'] = (int)( $matchs[7] / $byterate );

		$record['startByte'] = (int)( $matchs[6] );
		$record['endByte'] = (int)( $matchs[7] );

		//echo "$line\n";
		//print_r($record);

		$state->records++;

		// Run this log line over each parser
		foreach ($parsers as &$parser) {
			$parser->log($record);
		}

	}

	// Output each parser's stats
	foreach ($parsers as &$parser) {

		$parser->finish();

		$parser->toString();
		$parser->save('output/');
	}


	fclose($fp);
?>
