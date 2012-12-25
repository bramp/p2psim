package sim.net.topology.reader.nodeloader;

import sim.net.Host;
import sim.net.router.EdgeRouter;
import sim.net.router.InteriorRouter;
import sim.net.topology.reader.NodeLoader;

public class TSRouterLoader implements NodeLoader {

	public Host createHost(String type, int address) {
		//transits act as interior routers
		if (type.equals("T")) {
			return new InteriorRouter(address);
		}
		// stubs act as edge routers for peers
		else if (type.equals("S")) {
			return new EdgeRouter(address);
			//return new MobileRouter(address, SharedLink.BANDWIDTH_1024k, 10);
		}
		else {
			throw new RuntimeException("Unknown Node Type " + type);
		}
	}

}
