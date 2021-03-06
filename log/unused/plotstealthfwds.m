hold on
grid on

plot(averagegroup(r5.Host_Peer_Count,rep),averagegroup(r5.DHT_MsgFwd_Count,rep),'bo-')
plot(averagegroup(r10.Host_Peer_Count,rep),averagegroup(r10.DHT_MsgFwd_Count,rep),'b*-')
plot(averagegroup(r20.Host_Peer_Count,rep),averagegroup(r20.DHT_MsgFwd_Count,rep),'bx-')
plot(averagegroup(r30.Host_Peer_Count,rep),averagegroup(r30.DHT_MsgFwd_Count,rep),'bs-')
plot(averagegroup(r50.Host_Peer_Count,rep),averagegroup(r50.DHT_MsgFwd_Count,rep),'bd-')

xlabel('DHT Network Size')
ylabel('Number of Forwarded Messages')
title({strcat('[',workload,']');'Number of Forwarded Messages with Increasing DHT Network Size';'and Percentage of Normal to Stealthy Peers'})
legend('5%','10%','20%','30%','50%','Location','Best')
hold off