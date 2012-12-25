hold on
grid on
plot(averagegroup(r1.Host_Peer_Count,rep),averagegroup(r1.DHT_Stretch,rep),'b+-')
plot(averagegroup(r5.Host_Peer_Count,rep),averagegroup(r5.DHT_Stretch,rep),'bo-')
plot(averagegroup(r10.Host_Peer_Count,rep),averagegroup(r10.DHT_Stretch,rep),'b*-')
plot(averagegroup(r20.Host_Peer_Count,rep),averagegroup(r20.DHT_Stretch,rep),'bx-')
plot(averagegroup(r30.Host_Peer_Count,rep),averagegroup(r30.DHT_Stretch,rep),'bs-')
plot(averagegroup(r50.Host_Peer_Count,rep),averagegroup(r50.DHT_Stretch,rep),'bd-')
xlabel('DHT Network Size')
ylabel('DHT Stretch')
title({strcat('[',workload,']');'DHT Stretch with Increasing DHT Network Size';'and Percentage of Normal to Stealthy Peers'})
legend('1%','5%','10%','20%','30%','50%','Location','Best')
hold off