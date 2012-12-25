hold on
grid on

plot(averagegroup(r10.Host_Peer_Count,rep),averagegroup(r10.Net_E2ELatency_Max,rep),'bo-')
plot(averagegroup(r20.Host_Peer_Count,rep),averagegroup(r20.Net_E2ELatency_Max,rep),'b*-')
plot(averagegroup(r30.Host_Peer_Count,rep),averagegroup(r30.Net_E2ELatency_Max,rep),'bx-')
plot(averagegroup(r40.Host_Peer_Count,rep),averagegroup(r40.Net_E2ELatency_Max,rep),'bs-')
plot(averagegroup(r50.Host_Peer_Count,rep),averagegroup(r50.Net_E2ELatency_Max,rep),'bd-')
plot(averagegroup(r60.Host_Peer_Count,rep),averagegroup(r60.Net_E2ELatency_Max,rep),'bo--')
plot(averagegroup(r70.Host_Peer_Count,rep),averagegroup(r70.Net_E2ELatency_Max,rep),'b*--')
plot(averagegroup(r80.Host_Peer_Count,rep),averagegroup(r80.Net_E2ELatency_Max,rep),'bx--')
plot(averagegroup(r90.Host_Peer_Count,rep),averagegroup(r90.Net_E2ELatency_Max,rep),'bs--')
plot(averagegroup(r100.Host_Peer_Count,rep),averagegroup(r100.Net_E2ELatency_Max,rep),'bd--')

xlabel('DHT Network Size')
ylabel('Underlying Network End to End Latency (ms)')
title({strcat('[',workload,']');'Maximum Underlying Network End to End Latency with Increasing DHT Network Size';'and Percentage of Failed Nodes'})
legend('10%','20%','30%','40%','50%','60%','70%','80%','90%','Location','Best')
hold off