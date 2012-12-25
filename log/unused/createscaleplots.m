rep = input('How many times were the simulations run? > ');

if (~exist('Host_Peer_Count'))
    Host_Peer_Count = Host_NormalPeer_Count + Host_StealthPeer_Count;
end

figure
hold on
grid on
plot(averagegroup(Host_Peer_Count,rep),averagegroup(DHT_E2ELatency_Max,rep),'r--')
plot(averagegroup(Host_Peer_Count,rep),averagegroup(DHT_E2ELatency_Avg,rep),'b-')
plot(averagegroup(Host_Peer_Count,rep),averagegroup(DHT_E2ELatency_Min,rep),'g:')
legend('Max','Avg','Min','Location','BestOutside')
xlabel('Peer Count')
ylabel('DHT End to End Latency (ms)')
title('DHT End to End Latency with Peer Count')
hold off

figure
hold on
grid on
plot(averagegroup(Host_Peer_Count,rep),averagegroup(Net_E2ELatency_Max,rep),'r--')
plot(averagegroup(Host_Peer_Count,rep),averagegroup(Net_E2ELatency_Avg,rep),'b-')
plot(averagegroup(Host_Peer_Count,rep),averagegroup(Net_E2ELatency_Min,rep),'g:')
legend('Max','Avg','Min','Location','BestOutside')
xlabel('Peer Count')
ylabel('Underlying Network End to End Latency (ms)')
title('Underlying Network End to End Latency with Peer Count')
hold off

figure
hold on
grid on
plot(averagegroup(Host_Peer_Count,rep),averagegroup(DHT_Hops_Avg,rep),'b-')
xlabel('Peer Count')
ylabel('Average DHT Hops')
title('Average DHT Hops with Peer Count')
hold off

figure
hold on
grid on
plot(averagegroup(Host_Peer_Count,rep),averagegroup(Net_Hops_Max,rep),'r--')
plot(averagegroup(Host_Peer_Count,rep),averagegroup(Net_Hops_Avg,rep),'b-')
plot(averagegroup(Host_Peer_Count,rep),averagegroup(Net_Hops_Min,rep),'g:')
legend('Max','Avg','Min','Location','BestOutside')
xlabel('Peer Count')
ylabel('Underlying Network Hops')
title('Underlying Network Hops with Peer Count')
hold off

figure
hold on
grid on
plot(averagegroup(Host_Peer_Count,rep),averagegroup(DHT_MsgSent_Count,rep),'r--')
plot(averagegroup(Host_Peer_Count,rep),averagegroup(DHT_MsgRecv_Count,rep),'b-')
plot(averagegroup(Host_Peer_Count,rep),averagegroup(DHT_MsgFwd_Count,rep),'g:')
legend('Sent','Received','Forwarded','Location','BestOutside')
xlabel('Peer Count')
ylabel('Messages')
title('Messages with Peer Count')
hold off

figure
hold on
grid on
plot(averagegroup(Host_Peer_Count,3),averagegroup(DHT_E2ELatencyTotal./DHT_E2ELatencyUnicastTotal,3))
xlabel('Peer Count')
ylabel('Stretch')
title('Stretch with Peer Count')
hold off