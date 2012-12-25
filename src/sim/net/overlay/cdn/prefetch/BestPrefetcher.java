package sim.net.overlay.cdn.prefetch;

import sim.main.Global;
import sim.net.overlay.cdn.Hotspot;
import sim.net.overlay.cdn.Request;
import sim.net.overlay.cdn.workload.Action;
import sim.net.overlay.cdn.workload.SeekAction;
import sim.net.overlay.cdn.workload.WorkloadReader;
import sim.net.overlay.cdn.workload.WorldCupFromLogs;
import sim.net.overlay.cdn.workload.Action.ActionType;
import sim.net.router.Router;

/**
 * Request the bookmarks based on where the user will actually go next
 * @author Andrew Brampton
 *
 */
public class BestPrefetcher extends Prefetcher {

	@Override
	public void newRequest( final Request r, final String why ) {

		// Find out what action we are on, and then find out which bookmarks
		// the user will view
		clearPrefetchRange();

		if ( r != null )
			setMedia( r.getMedia() );		

		WorkloadReader reader = WorldCupFromLogs.r;

		// Hack to figure out what client ID we are
		int clientID = client.getAddress() - Global.hosts.getType(Router.class).size() - 1;

		for (int i = reader.actionsIn(); i < reader.getNumberOfActions(); i++) {
			Action a = reader.getAction(i);
			if (a.user == clientID && a.type == ActionType.seek ) {
				SeekAction sa = (SeekAction)a;

				// If this is a shortcut, do something
				if ( sa.why != null && sa.why.startsWith("shortcut ") ) {
					String bookmark = sa.why.substring(9);
					Hotspot h = media.getHotspot(bookmark);

					addPrefetchRange(h);
				}
			}
		}
	}

}
