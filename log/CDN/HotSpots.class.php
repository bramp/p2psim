<?php

/*
Works out how which segments of the stream were viewed
*/
class HotSpots extends Parser {

	// Should the hotspot be based on threashold OR something else?
	const usethreashold = false;

	// Threashold of how popular a segment has to be to become "hot"
	const hotthreashold = 0.30;

	const usehotsize = false;

	// How large should the hotspots be (as a percentage of the media)
	const hotsize = 0.10;

		// If we have this many sessions
	const progSessions = 100;

	private $views = array();
	private $viewsessions = array();

	function __construct() {}

	public function log (&$record) {
		$file = $record['object'];

		if (!isset($this->views[$file])) {
			$this->views[$file] = array();
			$this->viewsessions[$file] = 0;
		}

		$this->viewsessions[$file]++;
		$views = & $this->views[$file];

		for ($i = (int)$record['startMediaTime']; $i < $record['endMediaTime']; $i++) {
			if (!isset( $views[$i] ))
				$views[$i] = 1;
			else
				$views[$i]++;
		}

		// Fill in the zeros
		$max = max(array_keys($views));
		for ($i = 0; $i < $max ; $i++) {
			if (!isset($views[$i]))
				$views[$i] = 0;
		}

		ksort ($views, SORT_NUMERIC);

		if ( $this->viewsessions[$file] % self::progSessions == 0 ) {
			$this->saveSessions('output/' . $this->viewsessions[$file] . '-', $file, $views);
		}

	}

	public function finish() {}
	public function toString() {}

	private function calculateHotspots($views, $threadshold) {
		$hotspots = array();
		$hotStart = -1;

		$end = max(array_keys($views));
		$max = (float)max($views);

		// Find which parts are above a threashold
		for ($i = 0; $i <= $end; $i++) {
			//echo $views[$i] . " " . ($views[$i] / $max) . " $max\n";
			if (($views[$i] / $max) > $threadshold) {
				if ($hotStart == -1) {
					$hotStart = $i;
				}
			} else {
				if ($hotStart != -1) {
					$hotspots[] = $hotStart . "\t" . $i;
					$hotStart = -1;
				}
			}
		}

		if ($hotStart != -1) {
			$hotspots[] = $hotStart . "\t" . $i;
		}

		return $hotspots;
	}

	private function saveSessions($fileprefix, $game, $views) {

			$fp = fopen ($fileprefix . $game . '_views', 'w');
			foreach ($views as $time => $count) {
				fwrite($fp, "$time\t$count\n");
			}
			fclose($fp);

			$hotspots = array();

			if (self::usethreashold) {
				$hotspots = $this->calculateHotspots( $views, self::hotthreashold );
			} else if (self::usehotsize) {
				$length = (float)max(array_keys($views)) * self::hotsize;

				$hotsize = 0;

				// In this case find a threashold that matchs ~ X percent
				for ( $threashold = 0.0; $threashold <= 1.0; $threashold += 0.05 ) {
					$oldhotspots = $hotspots;
					$oldhotsize = $hotsize;
					$hotspots = $this->calculateHotspots( $views, $threashold );

					if (count($hotspots) > 0) {
						$hotsize = 0;
						foreach ($hotspots as $hotspot) {
							list ($start, $end) = explode("\t", $hotspot, 2);
							$hotsize += ($end - $start);
						}

						if ($hotsize < $length) {

							if (abs($oldhotsize - $length) < abs($hotsize - $length)) {
								$hotspots = $oldhotspots;
								$hotsize = $oldhotsize;
								$threashold -= 0.05;
							}

							//echo "$threashold $hotsize < $length\n";
							break;
						}
					}
				}

			} else {

			}

			$fp = fopen ($fileprefix . $game . '_hotspots', 'w');
			foreach ($hotspots as $hotspot) {
				fwrite($fp, "$hotspot\n");
			}
			fclose($fp);
	}

	public function save($fileprefix) {
		foreach ($this->views as $game => $views) {
			$this->saveSessions($fileprefix, $game, $views);

			// This works out all the hot spots at the end for different theasholds
			for ($t = 0.0; $t <= 1.0; $t+= 0.01) {
				$hotspots = $this->calculateHotspots( $views, $t );

				$fp = fopen ($fileprefix . $game . '_hotspots_' . $t, 'w');
				foreach ($hotspots as $hotspot) {
					fwrite($fp, "$hotspot\n");
				}
				fclose($fp);
			}

		}
	}

}

?>