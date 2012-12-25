<?php

	function cmd($line) {
		echo $line . "\n";
		passthru ($line);
	}

	$files = `find -mindepth 3 -name "*.txt.gz"`;
	$files = explode("\n", trim($files));

	//rsort ($files);

	foreach($files as $file) {
		$t = explode('/', $file);

		echo $file . "\n\n";

		$newfile = str_replace ( '.gz', '', $file);

		//cmd ('/bin/gzip -cd "' . $file . '" > "' . $newfile . '"');
		cmd ('/bin/gzip -d "' . $file . '"');
		cmd ('/usr/bin/bzip2 --best "' . $newfile . '"');
	}
?>