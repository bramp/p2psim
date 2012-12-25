<?php
	$tabs = `find -mindepth 3 -maxdepth 3 -name "*.tab"`;
	$tabs = explode("\n", trim($tabs));

	rsort ($tabs);

	foreach($tabs as $tab) {
		$t = explode('/', $tab);

		// Remove the timestamp
		$t[2] = substr( $t[2], strpos($t[2], '.') + 1 );

		$name = $t[1] . '.' . $t[2] . '.tab';

		if (file_exists($name)) {
			unlink ($name);
		}

		echo $name . "\n";
		`ln "$tab" "$name"`;

	}
?>