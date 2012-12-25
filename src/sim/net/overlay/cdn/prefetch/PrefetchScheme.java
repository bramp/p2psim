/**
 *
 */
package sim.net.overlay.cdn.prefetch;


public enum PrefetchScheme {
	NONE, //=0 Do no prefetching
	BEFORESTART, //=1 Retrieves all the prefetch data before the simulation starts
	SEQUENCE, //=2 Request the bookmarks in order
	SEQUENCEAFTER, //=3 Request the bookmarks in order (but only after our current position)
	RANDOM, //=4 Request the bookmarks in a random order
	PREDICTIVE, //=5 Request the bookmarks based on predications
	POPULARITY, //=6 Request the bookmarks based on their popularity

	PREDICTIVE2, //=7 Request the bookmarks based on two levels of predications

	BEST, //=8 Request the bookmarks based on where the user will actually go next

	PARALLEL, //=9 Request all the bookmarks in parallel biased by which one is needed

	AHEAD, //=10 Prefetchs X seconds ahead

	BEFORESTARTALL, //=11 Retrieves all the video before the simulation starts

	AHEADTOBOOKMARKEND, //=12 Prefetchs X seconds ahead, but only to bookmark end
	AHEADANDPREDICT, //=13 Prefetchs X seconds ahead, and predicts what to get after the bookmark

	;

	public String toString() {
		return Integer.toString( ordinal() );
	}

	public Prefetcher constructPrefetcher() {
		switch (this) {
			case BEFORESTART:
				return new BeforeStartPrefetcher( false );
			case BEFORESTARTALL:
				return new BeforeStartPrefetcher( true );

			case SEQUENCE:
				return new SequencePrefetcher( false );
			case SEQUENCEAFTER:
				return new SequencePrefetcher( true );

			case RANDOM:
				return new RandomPrefetcher();

			case PREDICTIVE:
				return new PredictivePrefetcher ( 1 );
			case PREDICTIVE2:
				return new PredictivePrefetcher ( 2 );

			case BEST:
				return new BestPrefetcher();

			case AHEAD:
				return new AheadPrefetcher();

			case AHEADTOBOOKMARKEND:
				return new AheadToBookmarkEndPrefetcher();

			case AHEADANDPREDICT:
				return new AheadAndPredictPrefetcher( new PredictivePrefetcher() );

			case NONE:
				return null;

			default:
				throw new RuntimeException( "Unknown prefetcher " + this);
		}
	}
}