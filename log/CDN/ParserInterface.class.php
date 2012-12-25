<?php

interface ParserInterface {

	public function init();
	public function finish();

	public function log (&$record);
	public function toString();

	// Output array of numbers to disk
	public function save($fileprefix);
}

?>