rep = input('How many times were the simulations run? > ')

a = importtab('p:\Stealth Churn.KeysChurnTest-0.01.tab');
b = importtab('p:\Stealth Churn.KeysChurnTest-0.05.tab');
c = importtab('p:\Stealth Churn.KeysChurnTest-0.1.tab');
d = importtab('p:\Stealth Churn.KeysChurnTest-0.2.tab');
e = importtab('p:\Stealth Churn.KeysChurnTest-0.3.tab');
f = importtab('p:\Stealth Churn.KeysChurnTest-0.5.tab');

a.Host_Peer_Count = a.Host_StealthPeer_Count + a.Host_NormalPeer_Count;
b.Host_Peer_Count = b.Host_StealthPeer_Count + b.Host_NormalPeer_Count;
c.Host_Peer_Count = c.Host_StealthPeer_Count + c.Host_NormalPeer_Count;
d.Host_Peer_Count = d.Host_StealthPeer_Count + d.Host_NormalPeer_Count;
e.Host_Peer_Count = e.Host_StealthPeer_Count + e.Host_NormalPeer_Count;
f.Host_Peer_Count = f.Host_StealthPeer_Count + f.Host_NormalPeer_Count;

figure
hold on
grid on
plot(averagegroup(a.Host_Peer_Count,rep), averagegroup(a.DHT_PutMessage_Count ./ a.DHT_PutEvent_Count, rep), 'r')
plot(averagegroup(b.Host_Peer_Count,rep), averagegroup(b.DHT_PutMessage_Count ./ b.DHT_PutEvent_Count, rep), 'g')
plot(averagegroup(c.Host_Peer_Count,rep), averagegroup(c.DHT_PutMessage_Count ./ c.DHT_PutEvent_Count, rep), 'b')
plot(averagegroup(d.Host_Peer_Count,rep), averagegroup(d.DHT_PutMessage_Count ./ d.DHT_PutEvent_Count, rep), 'r-')
plot(averagegroup(e.Host_Peer_Count,rep), averagegroup(e.DHT_PutMessage_Count ./ e.DHT_PutEvent_Count, rep), 'g-')
plot(averagegroup(f.Host_Peer_Count,rep), averagegroup(f.DHT_PutMessage_Count ./ f.DHT_PutEvent_Count, rep), 'b-')

legend('0.01%','0.05%','0.10%','0.20%','0.30%','0.50%')
xlabel('Peer Count')
ylabel('Number of key movements (per key)')
title('Per key movements vs Peer Count')
hold off

figure
hold on
grid on
plot(averagegroup(a.Host_Peer_Count,rep), averagegroup(a.DHT_PutMessage_Count ./ a.DHT_PutEvent_Count, rep), 'r')
plot(averagegroup(b.Host_Peer_Count,rep), averagegroup(b.DHT_PutMessage_Count ./ b.DHT_PutEvent_Count, rep), 'g')
plot(averagegroup(c.Host_Peer_Count,rep), averagegroup(c.DHT_PutMessage_Count ./ c.DHT_PutEvent_Count, rep), 'b')
plot(averagegroup(d.Host_Peer_Count,rep), averagegroup(d.DHT_PutMessage_Count ./ d.DHT_PutEvent_Count, rep), 'r-')
plot(averagegroup(e.Host_Peer_Count,rep), averagegroup(e.DHT_PutMessage_Count ./ e.DHT_PutEvent_Count, rep), 'g-')
plot(averagegroup(f.Host_Peer_Count,rep), averagegroup(f.DHT_PutMessage_Count ./ f.DHT_PutEvent_Count, rep), 'b-')

legend('0.01%','0.05%','0.10%','0.20%','0.30%','0.50%')
xlabel('Peer Count')
ylabel('Number of key movements (per key)')
title('Per key movements vs Peer Count')
hold off