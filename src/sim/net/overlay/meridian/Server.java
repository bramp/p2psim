/**
 *
 */
package sim.net.overlay.meridian;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.Packet;
import sim.net.RoutingException;
import sim.net.links.Link;
import sim.net.overlay.meridian.Rings.HostPair;

/**
 *
 * A Merdian server that handles request for service
 *
 * @author Andrew Brampton
 * @author Idris A. Rai
 *
 */
public class Server extends Node {

	int  limit;
    int  requestCount;

	/**
	 * @param address
	 */

	public Server(int address, int limit, int requests) {
		super(address);
		this.limit = limit;
		this.requestCount = requests;
	}

	public Server(int address, int limit) {
		super(address);
		this.limit = limit;
	}

	public Server(int address) {
		super(address);
		this.limit = 4;
	}

    Map<Integer, Double> scoreTable = new TreeMap<Integer, Double>();

    Map<Integer,Integer> limitTable = new TreeMap<Integer, Integer>();

    Map<Integer, List<Integer>> clientTable = new TreeMap<Integer, List<Integer>>();
    Map<Integer, List<Integer>> offloadedClients = new TreeMap<Integer, List<Integer>>();

    List<Integer> clientsList = new ArrayList<Integer>();

    Map<Object, Integer> offloadedRequests = new TreeMap<Object, Integer>();

    public Map<Integer, Integer> serverRequests = new TreeMap<Integer, Integer>();
    public ArrayList<Integer>  actingServers = new ArrayList<Integer>();

    public Set<Integer>  overloadedServers = new HashSet<Integer>();

	/* (non-Javadoc)
	 * @see sim.net.overlay.meridian.Node#recv(sim.net.links.Link, sim.net.Packet)
	 */
	@Override
	public void recv(Link link, Packet p) {
		super.recv(link, p);

      	int bestServer = 0;
      	//Map serverScore;
		//Map serverClients;

		if(actingServers.size() != Global.hosts.getType(Server.class).size()){
		    if(!actingServers.contains(this.address)){
	            actingServers.add(this.address);
		    }
		} else{
			System.out.println("All servers are ACTING");
		}
		if (p instanceof RequestPacket && Default.useRandom == true) {
			requestCount++;

			serverRequests.put(this.address, requestCount);
			System.out.println(this.address+"  "+requestCount+"  "+ serverRequests);
			clientsList.add(p.from);


			if (this.requestCount == this.limit){

				if(!overloadedServers.contains(this.address))
					overloadedServers.add(this.address);

			}else if (this.requestCount - this.limit >= 1){

				Iterator<Integer> it = actingServers.iterator();
				int server =0;
				boolean flag = false;
				int acting = 0;

				while(it.hasNext()){
					acting = it.next();
					if(!overloaded( acting)){
						System.out.println("its hereeeeee");
					 flag = true;
					 server = acting;
					}
				}
				if (flag == false){
					server = getServer();
					System.out.println("not hereeeeee");
				}

                if(!actingServers.contains(server))
                	actingServers.add(server);

				System.out.println("aaaacting " +actingServers);

				Server s = (Server) Global.hosts.get(server);
				int available = s.limit - s.requestCount;

				System.out.println(this.address+ " " +"Random Server " +server +"  "+overloaded(server)+"  "+ available);

				if(available != 0){
					try {

						int count = 0;
						if (serverRequests.containsKey(server))
							count= serverRequests.get(server);

						--requestCount;

						serverRequests.put(server,count+1);
						serverRequests.put(this.address, requestCount);

						send (RedirectPacket.newPacket(this.address,p.from, server));
					} catch (RoutingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
					System.out.println(this.address+"  "+available +" "+ server+ "  "+overloaded(server)+"  "+serverRequests.get(server) );

				clientsList =  clientsList.subList(0, clientsList.size()-1);


			}

		} else
		if (p instanceof RequestPacket && Default.useRandom == false) {
			//serverScore = sTable((RequestPacket)  p);
	 		//serverClients = cTable((RequestPacket) p);
			requestCount++;
			serverRequests.put(this.address, requestCount);

			if (this.requestCount > this.limit){

				overloadedServers.add(this.address);

                bestServer = getBest(p);
                if(limitTable.containsKey(bestServer)){

                	List<Integer> clients = getClients(bestServer,p);

                    int lmt = limitTable.get(bestServer);
                    Server s = (Server) Global.hosts.get(bestServer);

                    int available = s.limit - s.requestCount;

                    List<Integer> ct = null ;

                    if(clients != null){
                    	ct = removeDublicates(clients);
                    }
                    /*
                    // get closest clients from bestServer up to ring i-1 exclusive
                    if(ct!=null){
                    	int r = 2;
                    	ct = getClosest(ct, bestServer, r);
                    }*/

                    if (ct  != null && ct.size() <= available){
                        clientsList.addAll(ct);
                    	redirect(ct, bestServer,lmt);
                    }
                    else if (ct != null && ct.size() > available){
                    	ArrayList<Integer> subList = new ArrayList<Integer>();

                    	Iterator<Integer> i = ct.iterator();
                    	int count=0;
                    	while (i.hasNext() && count < available ){
                    		subList.add(i.next());
                    		count++;
                    	}

                        clientsList.addAll(subList);
                        redirect(subList, bestServer,lmt);

                    }
                } else

                if(!actingServers.contains(bestServer)){
                    try {
    					send (RequestLimitPacket.newPacket(this.address, bestServer));
    				} catch (RoutingException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
                }
 	       }

		} else if(p instanceof RequestLimitPacket ){
			try {
				send (RequestLimitReplyPacket.newPacket(this.address, p.from, this.limit, this.limit-this.requestCount));
			} catch (RoutingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (p instanceof RequestLimitReplyPacket && Default.useRandom == false){
			RequestLimitReplyPacket rp = (RequestLimitReplyPacket) p;
			int lmt = (int) rp.limit;
			if(!limitTable.containsKey(rp.from)){
				limitTable.put(rp.from, lmt);
			}

			List<Integer> cs = getClients(p.from, rp);
	        int available = (int) rp.availCapacity;
	        if(cs != null)
	        	cs = removeDublicates(cs);
	        /*
	        if(cs!=null){
	        	int r = 2;
	        	cs = getClosest(cs, bestServer, r);
	        }*/


	        if(cs != null && cs.size() <= available ){
	        		clientsList.addAll(cs);
	        		redirect(cs, rp.from,lmt);
	        }
	        else if (cs != null && cs.size() > available) {
	        	ArrayList<Integer> subList = new ArrayList<Integer>();
	        	Iterator<Integer> i = cs.iterator();
            	int count=0;
            	while (i.hasNext() && count < available ){
            		subList.add(i.next());
            		count++;
            	}

	        	clientsList.addAll(subList);
	        	redirect(subList, rp.from,lmt);
	        }
		}else if (p instanceof RequestLimitReplyPacket && Default.useRandom == true){

			RequestLimitReplyPacket rp = (RequestLimitReplyPacket) p;

			Iterator<Integer> it = clientsList.iterator();
			int ad = it.next();

			try {
				send (RedirectPacket.newPacket(this.address, ad, rp.from));
			} catch (RoutingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println(clientsList);
			clientsList.remove(ad);
			System.out.println(clientsList);
		}
	}

	/*
	private ArrayList<Integer> getClosest(ArrayList<Integer> c, int best, int k){
		ArrayList<Integer> closestList = new ArrayList<Integer>();

		Server s = (Server) Global.hosts.get(best);

		for(int j: c){
			Client client = (Client) Global.hosts.get(j);
			Rings r = client.rings;

            r.getHostRing(best);

			if ( r.getHostRing(best) <k){

				System.out.println( r.getHostRing(best)+"  "+ r +" "+best);

	         closestList.add(j);
			}
		}
		System.out.println("Original List  " +c);
		System.out.println("Closest List"+ closestList);
		return closestList;
	}

	*/
	private Integer getServer(){

		int address = 0;

/*		Iterator<Integer> i1 = actingServers.iterator();

		while(i1.hasNext()){
			address = i1.next();
			if (actingServers.contains(address) && !overloaded(address))
				address = i1.next();
			else
				address = 0;
		}
		*/


//		if (address == 0){
			HostSet s = Global.hosts.getType(Server.class);
			Iterator<Host> i = s.iterator();

			Host h = i.next();
			address = h.getAddress();

		  // System.out.println(actingServers);

			while(i.hasNext()){
				if(!actingServers.contains(address) && !overloaded(address)){
					System.out.println("aaaacting 2 " +actingServers);
					   System.out.println("now  " +address);
					return address;
				}
				h = i.next();
				address = h.getAddress();
			}
//		}
		return address;

	}

	private List<Integer> removeDublicates(List<Integer> clients) {
		// TODO Auto-generated method stub

		ArrayList<Integer> cl = new ArrayList<Integer>();
		Iterator<Integer> i = clients.iterator();

		int y;
		while(i.hasNext()){
			y = i.next();
			if(!clientsList.contains(y)){
//			if(!offloadedClients.containsValue(y)){
				cl.add(y);
			}
		}
		return cl;
	}

	public int  getBest(Packet p ){
		  int  best = 0;

		  Map<Integer, Double> score = sTable((RequestPacket) p);

		  Iterator<Integer> jj = score.keySet().iterator();
		  List<Integer> overloadedList = new ArrayList<Integer>(); // a list of overloaded servers


		  while(jj.hasNext()){
			  	int address = (Integer) jj.next();
				if (serverRequests.get(address) != null ) {
					if(overloaded(address)){
						overloadedList.add(address);
					}
				}
		  }

	     System.out.println("Overloaded = "+overloadedList);
	     System.out.println("acting = "+ actingServers + " svrRqsts"+ serverRequests);

		 Iterator<Integer> ii = scoreTable.keySet().iterator() ;
		 Iterator<Integer> ik = scoreTable.keySet().iterator() ;

		 // best server is
		 // 1.  non overloaded acting server with clients in the clientsTable
		 // 2.  non overloaded new server with clientsTable

		 int o;
		 double max = 0 ;

		 while (ik.hasNext()){
			 o = ik.next();
			 if(!overloadedList.contains(o)&& actingServers.contains(o) && !removeDublicates(clientTable.get(o)).isEmpty()){
				 List<Integer> cnts = removeDublicates(clientTable.get(o));
				 if (cnts != null )
			 		best = o;
			 }
		 }

		 if(best == 0){
			 while (ii.hasNext()){
				 o = (Integer) ii.next();
				 if (!overloadedList.contains(o) && !removeDublicates(clientTable.get(o)).isEmpty()){
					 if (scoreTable.get(o)> max ){
						 best = o;
						 max = scoreTable.get(o);
					 }
				 }
			 }
		 }
		  if(best == 0)
			  best = this.address;

	      return best;
	   }

	private void redirect(List<Integer> clients, int best, int limit){

		// send request to the new server (re-direct)
		// also remove all instances of clients ID from clients tables -- to do --
		// then if clients move scores of other servers changes !!! -- mmmm!

		// do as it is --  some clients will be ping-ponged to new servers
		// "enhancement" will be an option to find the best server for all
		// offloaded clients

	//	System.out.println("CL = "+clients);

        // remove clients' dublicates

		System.out.println("Server = "+this.address+"  tbl = "+clientTable);
		System.out.println("Server = "+this.address+"  tbl = "+scoreTable);

        //Server s = (Server) Global.hosts.get(best);

        if(!actingServers.contains(best)){
            actingServers.add(best);
        }
        try {
        	if (clients != null){
        		Iterator<Integer> i = clients.iterator();
        		while ( i.hasNext() ) {
        			int in = i.next();
        			send (RedirectPacket.newPacket(this.address, in, best));
        		}
        	} else if (overloadedServers.size() == Global.hosts.size() ) {
        		System.out.println("ALL servers are overloaded!!!!");
        	}
		} catch (RoutingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// table and requests updates

    	tablesUpdates(best,clients);
	}


	private void tablesUpdates(int server, List<Integer> clients) {
		// TODO Auto-generated method stub

		/*	useful for updating scores -- to do --
		 *
		 * Client c = (Client) Global.hosts.get(in);
			Rings rng = c.rings;
			int r = c.getRing(best, radii);
			double s = getScore(rng, r);
//			System.out.println(rng.rings.length +"  "+ r + "score = "+s);
			System.out.println(rng+"  "+ (Server) Global.hosts.get(best)+"'s ring = "+r);
			*/


		int k = clients.size();
		int sz = 0;

		if (offloadedRequests.containsKey(server)){
			sz  = offloadedRequests.get(server);
		}

	    offloadedRequests.put(server, sz+clients.size());

        List<Integer> ls = offloadedClients.get(server);

        if(ls!=null){
        	Iterator<Integer> ii = clients.iterator() ;
        	while(ii.hasNext()){
        		int cl = ii.next();
        		if(!ls.contains(cl)){
        			ls.add(cl);
        		}
        	}
        	offloadedClients.put(server, ls);
        } else{
        	ls = clients;
        	offloadedClients.put(server, ls);
        }

    	System.out.println("O-CL = "+offloadedClients);
    	System.out.println("O-RQ = "+offloadedRequests);

    	List<Integer> l1 = clientTable.get(server);
		List<Integer> l2 = offloadedClients.get(server);
    	Iterator<Integer> i2 = l2.iterator();

		List<Integer> lst = new ArrayList<Integer>();
		int a;
		while(i2.hasNext()){
			a = i2.next();
			if(l1.contains(a)){
				lst.add(a);
			}
		}

		Iterator<Integer> ij = lst.iterator();
		while(ij.hasNext()){
			l1.remove(ij.next());
		}

		clientTable.put(server, l1);

		int m = 0;
		if(serverRequests.containsKey(server)){
			 m =  serverRequests.get(server);
		}

		// k can have dublicates -- eliminate duplicates -- to do

		requestCount = requestCount - k;
		if(overloadedServers.contains(this.address)){
			if(requestCount < this.limit)
			//	System.out.println(overloadedServers);
			//	System.out.println(this.address);
				overloadedServers.remove(this.address) ;
		}

		serverRequests.put(this.address, requestCount);
		serverRequests.put(server, m+k);

		System.out.println("LimitTable = "+limitTable);
	    System.out.println("2  acting = "+ actingServers + " svrRqsts"+ serverRequests);
	}


	private List<Integer> getClients(int bestServer, Packet p) {

		// get clients corresponding to the best server

		Map<Integer, List<Integer>> clients = new TreeMap<Integer, List<Integer>>();

		if(p instanceof RequestPacket){
			clients = cTable((RequestPacket) p);
		} else if(p instanceof RequestLimitPacket) {
			clients = cTable((RequestLimitPacket) p);
		}

		return clients.get(bestServer);
	}

	public Map<Integer, Double> sTable(Packet p){
		RequestPacket rp = (RequestPacket) p;
		Rings rng =  rp.rings;

		for (int i = 0; i < rng.rings.length; i++) {
			Iterator<HostPair> ii = rng.rings[i].iterator();

			while(ii.hasNext()){
				int address = ii.next().host;

				if ( address != this.getAddress() ) {
				    if( !scoreTable.containsKey(address) ){ // look at self to avoid ofloading to itself
		//			    if(!scoreTable.containsKey(obj)){
					    	scoreTable.put(address, (double)(rng.rings.length)/(i+1));

				    } else {
						double score = scoreTable.get(address);
						score = score+  getScore(rng, i);
						scoreTable.put(address, score);
				    }
				}
			}
		}
		return scoreTable;
	}

	// get a score for a server on ring i

	private double getScore(Rings r , int i){
		return (r.rings.length)/(i+1);
	}

	private Map<Integer, List<Integer>> cTable(Packet p){

		RequestPacket rp = (RequestPacket) p;
		Rings rng =  rp.rings;

		for (int i = 0; i < rng.rings.length; i++) {
			Iterator<HostPair> ii = rng.rings[i].iterator();

			while(ii.hasNext()){
				Integer address = ii.next().host;
				if ( address != this.address) {
				    if (clientTable.containsKey(address) ){
					    List<Integer> l = clientTable.get(address);

					    if(!l.contains(p.from)){
						   l.add(p.from);
					       clientTable.put(address, l);
					    }
				    } else {
					    List<Integer> lt = new ArrayList<Integer>();
					    if(!lt.contains(p.from)){
					       lt.add(p.from);
				    	   clientTable.put(address, lt);
					    }
				    }
				}
			}
		}
		return clientTable;
	  }


    private boolean overloaded(int address) {
	// TODO Auto-generated method stub

    Server h = (Server) Global.hosts.get(address);

    if((serverRequests.get(address)) == null)
    	return false;
    else if((serverRequests.get(address)) >= h.limit){
		return true;
		}else{
		return false;
	 	}
    }

}

