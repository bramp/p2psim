<?php

//$sizes = array(5,10,25,50,100,200,300,400,500,600,700,800,900,1000, 1100, 1200, 1300, 1400, 1500, 1600);
//$sizes = array(150,160, 170, 180, 190, 200, 210, 220, 230, 240, 250, 260, 270, 280, 290, 300, 330, 360, 390, 410, 440, 470, 500, 550, 600, 700, 750, 800);
$sizes = array(1800, 2000, 2250, 2500, 2750, 3000);

// percentage of stealth nodes
$percentages = array(0);

for ($seed = 0; $seed < 5 ; $seed++) {
        foreach ($sizes as $size) {
                foreach($percentages as $percentage) {
                        $stealth = (int)($size * $percentage);
                        $service = $size - $stealth;

                        // $percentage is passed in for logging purposes only
                        passthru("nice -n 19 java -Xms300m -Xmx500m -classpath ./bin sim.main.Simulator sim.workload.puredht.ConstMsgGlobalTest idrisvalidate $seed $service 10000 $percentage");
                }
        }
}

?>
