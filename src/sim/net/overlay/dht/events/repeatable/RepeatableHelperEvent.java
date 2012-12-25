package sim.net.overlay.dht.events.repeatable;

import java.util.Iterator;
import sim.events.RepeatableDistributionEvent;
import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;

public abstract class RepeatableHelperEvent extends RepeatableDistributionEvent {
	protected HostSet hosts = Global.hosts.getType(DHTInterface.class);

	/**
	 * @return a HostSet of failed peers
	 */
	public HostSet getFailedPeers() {
		Iterator<Host> i = hosts.iterator();
		HostSet failed = new HostSet();

		while(i.hasNext()) {
			Host h = i.next();

			if (h.hasFailed()) {
				failed.add(h);
			}
		}

		return failed;
	}

	/**
	 * @return a HostSet of non failed peers
	 */
	public HostSet getAlivePeers() {
		Iterator<Host> i = hosts.iterator();
		HostSet alive = new HostSet();

		while(i.hasNext()) {
			Host h = i.next();
			if (!h.hasFailed()) {
				alive.add(h);
			}
		}

		return alive;
	}


	/**
	 * if Global.auth_on is set then this returns only the joined peers that are
	 * able to authenicate joining peers.
	 * @return a list of peers you can join
	 */
	public HostSet getAuthGateways() {
		Iterator<Host> i = getJoinedPeers().iterator();
		HostSet joinable = new HostSet();

		while(i.hasNext()) {
			Host h = i.next();
			DHTInterface p = (DHTInterface)h;

			// Find peers that are not failed, and have joined
			// and if Global.auth_on is set check they are authed and can sign others
			if (!Global.auth_on || (p.getAuth() != null && p.getAuth().canSignOthers()) )
				joinable.add(h);
		}

		return joinable;
	}

	/**
	 * @return a HostSet of non failed, joined peers
	 */
	public HostSet getJoinedPeers(Iterator<Host> i) {
		HostSet joined = new HostSet();

		while(i.hasNext()) {
			Host h = i.next();
			DHTInterface p = (DHTInterface)h;

			// Find peers that are not failed, and have joined
			if (!h.hasFailed() && p.hasJoined()) {
				joined.add(h);
			}
		}

		return joined;
	}

	public HostSet getJoinedPeers() {
		return getJoinedPeers(hosts.iterator());
	}

	/**
	 * @return a HostSet of non failed, non joined peers
	 */
	public HostSet getNotJoinedPeers() {
		Iterator<Host> i = hosts.iterator();
		HostSet notjoined = new HostSet();

		while(i.hasNext()) {
			Host h = i.next();
			DHTInterface p = (DHTInterface) h;

			if (!h.hasFailed() && !p.hasJoined()) {
				notjoined.add(h);
			}
		}

		return notjoined;
	}
}
