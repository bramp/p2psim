package sim.net.events;

import sim.events.Event;

public class JoinMulticastGroupEvent extends Event {
	int nodeAddress;
	int groupAddress;

	public JoinMulticastGroupEvent(int nodeAddress, int groupAddress) {
		this.nodeAddress = nodeAddress;
		this.groupAddress = groupAddress;
	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}

	@Override
	public void run() throws Exception {
		// check the groupAddress is valid
		// find the root
		// schedule periodic MulticastJoinPackets
	}
}
