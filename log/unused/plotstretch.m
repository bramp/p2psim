hold on
grid on

plot(averagegroup(r10.Host_Peer_Count,rep),averagegroup(r10.DHT_Stretch,rep),'bo-')
plot(averagegroup(r20.Host_Peer_Count,rep),averagegroup(r20.DHT_Stretch,rep),'b*-')
plot(averagegroup(r30.Host_Peer_Count,rep),averagegroup(r30.DHT_Stretch,rep),'bx-')
plot(averagegroup(r40.Host_Peer_Count,rep),averagegroup(r40.DHT_Stretch,rep),'bs-')
plot(averagegroup(r50.Host_Peer_Count,rep),averagegroup(r50.DHT_Stretch,rep),'bd-')
plot(averagegroup(r60.Host_Peer_Count,rep),averagegroup(r60.DHT_Stretch,rep),'bo--')
plot(averagegroup(r70.Host_Peer_Count,rep),averagegroup(r70.DHT_Stretch,rep),'b*--')
plot(averagegroup(r80.Host_Peer_Count,rep),averagegroup(r80.DHT_Stretch,rep),'bx--')
plot(averagegroup(r90.Host_Peer_Count,rep),averagegroup(r90.DHT_Stretch,rep),'bs--')
plot(averagegroup(r100.Host_Peer_Count,rep),averagegroup(r100.DHT_Stretch,rep),'bd--')

xlabel('DHT Network Size')
ylabel('DHT Stretch')
title({strcat('[',workload,']');'DHT Stretch with Increasing DHT Network Size';'and Percentage of Failed Nodes'})
legend('10%','20%','30%','40%','50%','60%','70%','80%','90%','Location','Best')
hold off