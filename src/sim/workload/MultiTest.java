package sim.workload;

import sim.collections.IntVector;
import sim.events.Events;
import sim.main.Global;
import sim.net.Host;
import sim.net.SimpleHost;
import sim.net.events.SendEvent;
import sim.net.links.NormalLink;
import sim.net.multicast.MulticastJoinPacket;
import sim.net.multicast.MulticastPacket;
import sim.net.router.Router;

public class MultiTest {
	public MultiTest(String[] arglist) throws Exception {
		IntVector v = new IntVector();
		v.add(1);
		v.add(2);
		v.add(3);
		v.remove(0);
		System.out.println(v);
		System.exit(0);

		int lastAddress = Global.hosts.last().getAddress();

		Host a = new SimpleHost(lastAddress+1);
		Host b = new SimpleHost(lastAddress+2);
		Host c = new SimpleHost(lastAddress+3);

		new NormalLink(a,Global.hosts.get(0));
		new NormalLink(b,Global.hosts.get(100));
		new NormalLink(c,Global.hosts.get(3));

		Router.createRoutingTables();

		int groupAddress = Global.groups.addGroup(a.getAddress());

		MulticastJoinPacket p = MulticastJoinPacket.newPacket(b.getAddress(),groupAddress);
		MulticastJoinPacket p2 = MulticastJoinPacket.newPacket(c.getAddress(),groupAddress);

		MulticastPacket p3 = MulticastPacket.newPacket(b.getAddress(),groupAddress);

		Events.add(SendEvent.newEvent(b,p),0);
		Events.add(SendEvent.newEvent(c,p2),1000);

		Events.add(SendEvent.newEvent(b,p3),10000);
	}
}
