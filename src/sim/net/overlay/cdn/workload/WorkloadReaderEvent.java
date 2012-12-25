package sim.net.overlay.cdn.workload;

import sim.events.Event;
import sim.events.Events;
import sim.events.RepeatableEvent;
import sim.main.Global;
import sim.net.HostSet;
import sim.net.overlay.cdn.Client;
import sim.net.overlay.cdn.Media;
import sim.net.overlay.cdn.Server;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

public final class WorkloadReaderEvent extends RepeatableEvent {

	protected WorkloadReader r;
	protected Action nextAction;
	protected Client[] clients;
	protected Server server;
	protected int count;

	public static WorkloadReaderEvent newEvent(WorkloadReader r) {

		assert r != null;

		WorkloadReaderEvent e = (WorkloadReaderEvent) Event.newEvent(WorkloadReaderEvent.class);

		e.r = r;
		e.nextAction = r.getNextAction();

		HostSet h = Global.hosts.getType(Client.class);

		e.clients = h.toArray( new Client[ h.size() ] );
		e.server = (Server) Global.hosts.getType(Server.class).first();

		e.count = 0;

		return e;
	}

	@Override
	public void run() throws Exception {
		if (nextAction == null)
			return;

		// Run the action
		assert (nextAction.realTime) == Events.getTime();

		Client c = clients[nextAction.user];
		Server s = server;
		Media m = Media.getMedia(nextAction.object);

		Trace.println(LogLevel.INFO, c + ": Running action " + ++count + "/" + r.getNumberOfActions() + " " + nextAction);

		switch (nextAction.type) {
			case seek:
				// Seek to this position
				SeekAction sa = (SeekAction)nextAction;
				long start = m.getByteOffset( sa.mediaTime );
				c.startRequest(s.getAddress(), m, start, -1, sa.why);
				break;

			case pause:
				// Pause the stream
				c.pauseRequest( s.getAddress() );
				break;

			case stop:
				// Stop the stream
				c.stopRequests( s.getAddress() );
				break;

			default:
				throw new RuntimeException("Unknown action");
		}

		// Now find the next action
		nextAction = r.getNextAction();

		// Reschedule if there is another action
		if (nextAction != null) {
			reschedule((nextAction.realTime) - Events.getTime());
		}
	}

	public long getEstimatedRunTime() {
		// TODO This only works if the WorkloadReaderEvent was started at time zero
		return (long) ( r.getEstimatedRunTime() - Events.getTime() );
	}

}
