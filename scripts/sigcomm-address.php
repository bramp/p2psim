<?php
passthru("java -Xms300m -Xmx1400m -classpath ./bin sim.main.Simulator sim.workload.sigcomm.EncapsulationTest Encap 0 200 10000 576460752303423488");
passthru("java -Xms300m -Xmx1400m -classpath ./bin sim.main.Simulator sim.workload.sigcomm.RegistrationTest Reg 0 200 10000 576460752303423488");
?>