package sim.net.topology.reader.nodeloader;

import sim.net.Host;
import sim.net.router.EdgeRouter;
import sim.net.router.InteriorRouter;
import sim.net.topology.reader.NodeLoader;

public class InetRouterLoader implements NodeLoader {

	public Host createHost(String type, int address) {
		//transits act as interior routers
		if (type.equals("N")) {
			//return new MobileRouter(address, SharedLink.BANDWIDTH_1024k, 10);
			return new EdgeRouter(address);
		} else if (type.equals("R")) {
			return new InteriorRouter(address);
		} else {
			throw new RuntimeException("Unknown Node Type " + type);
		}
	}

}
