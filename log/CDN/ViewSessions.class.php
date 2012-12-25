<?php

/*
Works out how which segments of the stream were viewed, and logs them as $start-$end, then works out session lengths
*/
class ViewSessions extends Parser {

	// The start and end time of each "viewing" session
	public $sessions = array();

	// The length of each viewing session
	public $sessionlength = array();


	public function log (&$record) {
		$file = $record['object'];

		$this->sessions[$file][] = $record['startMediaTime'] . '-' . $record['endMediaTime'];
		$this->sessionlength[$file][] = $record['endMediaTime'] - $record['startMediaTime'];

	}

	public function finish() {}

	public function toString() {}

	public function save($fileprefix) {

		// Make a list of session lengths
		foreach ($this->sessionlength as $game => $sessionlength) {

			$fp = fopen ($fileprefix . $game . '_views_sessions', 'w');

			foreach ($sessionlength as $length) {
				fwrite($fp, "$length\n");
			}

			fclose($fp);
		}

		// Make a list of actual session start and end times
		foreach ($this->sessions as $game => $sessions) {
			sort ($sessions, SORT_NUMERIC);

			$fp = fopen ($fileprefix . $game . '_views_start', 'w');

			foreach ($sessions as $time) {
				list ($start, $end) = explode('-', $time);
				fwrite($fp, "$start\t$end\t" . ($end - $start) . "\n");
			}

			fclose($fp);
		}
	}

}

?>