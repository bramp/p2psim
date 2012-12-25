<?php

/*
Works out what the hit miss ratio would be if a server was holding certain segments of the video
*/
class HitMissRatio extends Parser {

	private $hotspots;

	// The number of seconds we hit/miss
	private $hits = array();
	private $miss = array();

	private function loadHotspot($filename) {
		$f = file ($filename);
		$hotspots = array();

		foreach ($f as $line) {
			$line = trim($line);
			if (strlen($line) > 0) {
				list($start, $end) = explode("\t", $line);
				$hotspots[$start] = $end;
			}
		}

		return $hotspots;
	}

	// Pass an array of hotspot names to compare to
	function __construct($hotspots) {
		$this->hotspots = array();

		foreach ($hotspots as $hotspot) {
			$this->hotspots[$hotspot] = self::loadHotspot($hotspot);
			$this->hits[$hotspot] = 0;
			$this->miss[$hotspot] = 0;
		}
	}

	public function log (&$record) {
		$file = $record['object'];
		$start = $record['startMediaTime'];
		$end = $record['endMediaTime'];

		// Now look at all the hotspots
		foreach ($this->hotspots as $h => $hotspots) {

			foreach ($hotspots as $hotstart => $hotend) {

				$startswithin = false;
				$endswithin = false;

				// It starts within this hotspot
				if ($start > $hotstart && $start < $hotend) {
					$startswithin = true;
				}

				// It ends within this hotspot
				if ($end > $hotstart && $end < $hotend) {
					$endswithin = true;
				}

				if ($startswithin && $endswithin) {
					$this->hits[$h] += $end - $start;

				} else if ($startswithin && !$endswithin) {
					$this->hits[$h] += $hotend - $start;
					$this->miss[$h] += $end - $hotend;

				} else if ($endswithin && !$startswithin) {
					$this->hits[$h] += $end - $hotstart;
					$this->miss[$h] += $hotstart - $start;

				} else if (!$startswithin && !$endswithin) {
					if ($start < $hotstart && $end > $hotend) {
						$this->hits[$h] += $hotend - $hotstart;
						$this->miss[$h] += $hotstart - $start;
						$this->miss[$h] += $end - $hotend;
					} else {
						$this->miss[$h] += $end - $start;
					}
				}
			}
		}
	}

	public function finish() {}
	public function toString() {}

	public function save($fileprefix) {

		$fp = fopen ($fileprefix . '_hit_miss', 'w');

		foreach ($this->hotspots as $idx => $hotspots) {
			$hotspotsum = 0;
			foreach ($hotspots as $start => $end) {
				$hotspotsum += ($end - $start);
			}

			$ratio = $this->hits[$idx] / ($this->hits[$idx] + $this->miss[$idx]);
			fwrite($fp, $idx . "\t" . $hotspotsum . "\t" . $this->hits[$idx] . "\t" . $this->miss[$idx] . "\t" . $ratio . "\n");
		}

		fclose($fp);
	}

}

?>