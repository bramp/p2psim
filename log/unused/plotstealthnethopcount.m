hold on
grid on
plot(averagegroup(r1.Host_Peer_Count,rep),averagegroup(r1.Net_Hops_Avg,rep),'b+-')
plot(averagegroup(r5.Host_Peer_Count,rep),averagegroup(r5.Net_Hops_Avg,rep),'bo-')
plot(averagegroup(r10.Host_Peer_Count,rep),averagegroup(r10.Net_Hops_Avg,rep),'b*-')
plot(averagegroup(r20.Host_Peer_Count,rep),averagegroup(r20.Net_Hops_Avg,rep),'bx-')
plot(averagegroup(r30.Host_Peer_Count,rep),averagegroup(r30.Net_Hops_Avg,rep),'bs-')
plot(averagegroup(r50.Host_Peer_Count,rep),averagegroup(r50.Net_Hops_Avg,rep),'bd-')
xlabel('DHT Network Size')
ylabel('Underlying Network Hop Count')
title({strcat('[',workload,']');'Average Underlying Network Hop Count with Increasing DHT Network Size';'and Percentage of Normal to Stealthy Peers'})
legend('1%','5%','10%','20%','30%','50%','Location','Best')
hold off