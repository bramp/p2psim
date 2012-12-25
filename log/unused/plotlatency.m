hold on
grid on

plot(averagegroup(r10.Host_Peer_Count,rep),averagegroup(r10.DHT_E2ELatency_Avg,rep),'bo-')
plot(averagegroup(r20.Host_Peer_Count,rep),averagegroup(r20.DHT_E2ELatency_Avg,rep),'b*-')
plot(averagegroup(r30.Host_Peer_Count,rep),averagegroup(r30.DHT_E2ELatency_Avg,rep),'bx-')
plot(averagegroup(r40.Host_Peer_Count,rep),averagegroup(r40.DHT_E2ELatency_Avg,rep),'bs-')
plot(averagegroup(r50.Host_Peer_Count,rep),averagegroup(r50.DHT_E2ELatency_Avg,rep),'bd-')
plot(averagegroup(r60.Host_Peer_Count,rep),averagegroup(r60.DHT_E2ELatency_Avg,rep),'bo--')
plot(averagegroup(r70.Host_Peer_Count,rep),averagegroup(r70.DHT_E2ELatency_Avg,rep),'b*--')
plot(averagegroup(r80.Host_Peer_Count,rep),averagegroup(r80.DHT_E2ELatency_Avg,rep),'bx--')
plot(averagegroup(r90.Host_Peer_Count,rep),averagegroup(r90.DHT_E2ELatency_Avg,rep),'bs--')
plot(averagegroup(r100.Host_Peer_Count,rep),averagegroup(r100.DHT_E2ELatency_Avg,rep),'bd--')

xlabel('DHT Network Size')
ylabel('DHT End to End Latency (ms)')
title({strcat('[',workload,']');'Average DHT End to End Latency with Increasing DHT Network Size';'and Percentage of Failed Nodes'})
legend('10%','20%','30%','40%','50%','60%','70%','80%','90%','Location','Best')
hold off