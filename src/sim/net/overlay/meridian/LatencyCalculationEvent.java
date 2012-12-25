/**
 *
 */
package sim.net.overlay.meridian;

import sim.events.Event;
import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import static sim.stats.StatsObject.SEPARATOR;

/**
 * @author Andrew Brampton
 *
 */
public class LatencyCalculationEvent extends Event {

	public LatencyCalculationEvent() {}

	/* (non-Javadoc)
	 * @see sim.events.Event#getEstimatedRunTime()
	 */
	@Override
	public long getEstimatedRunTime() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see sim.events.Event#run()
	 */
	@Override
	public void run() throws Exception {
		HostSet clients = Global.hosts.getType(Client.class);
		HostSet servers = Global.hosts.getType(Server.class);

		for ( Host h : clients ) {
			Client c = (Client)h;

			int rtt = c.getUnicastDelay( c.newServer ) * 2;

			Global.stats.logAverage("Median" + SEPARATOR + "Client" + SEPARATOR + "Latency" , rtt);
			Global.stats.logAverage("Median" + SEPARATOR + "Server" + c.newServer + SEPARATOR + "Latency" , rtt);

			boolean closest = true;
			int clstDistance = 0;
			int penaltyDistance = 0;
			int sumDistances = 0;
		//	System.out.print(c + " (");

			for ( Host h2: servers ) {
				Server s = (Server)h2;

				int d = c.getUnicastDelay( s.getAddress())*2;
				sumDistances = sumDistances + d;
				if ( d < rtt ){
					closest = false;
					clstDistance =  d;
				}

		/*
				if ( c.newServer == s.getAddress() )
					System.out.print("[");

				System.out.print(s + " " + c.getUnicastDelay( s.getAddress() ));

				if ( c.newServer == s.getAddress() )
					System.out.print("]");

				System.out.print(" ,"); */

			}

			//double avgDistance = sumDistances/servers.size();

			penaltyDistance = rtt-clstDistance;

	 		Global.stats.logAverage("Median" + SEPARATOR + "averageClosest" , Math.min(rtt, clstDistance));
			Global.stats.logAverage("Median" + SEPARATOR + "penaltyDistance" , penaltyDistance);
			Global.stats.logAverage("Median" + SEPARATOR + "Closest" , closest ? 1 : 0 );
		}

	}

}
