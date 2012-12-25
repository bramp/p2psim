s1 = importtab('P:\NormalSimulFail.ConstMsgPerPeer100SimulFailTest-0.1.tab');
s2 = importtab('P:\NormalSimulFail.ConstMsgPerPeer100SimulFailTest-0.2.tab');
s3 = importtab('P:\NormalSimulFail.ConstMsgPerPeer100SimulFailTest-0.3.tab');
s4 = importtab('P:\NormalSimulFail.ConstMsgPerPeer100SimulFailTest-0.4.tab');
s5 = importtab('P:\NormalSimulFail.ConstMsgPerPeer100SimulFailTest-0.5.tab');
s6 = importtab('P:\NormalSimulFail.ConstMsgPerPeer100SimulFailTest-0.6.tab');
s7 = importtab('P:\NormalSimulFail.ConstMsgPerPeer100SimulFailTest-0.7.tab');
s8 = importtab('P:\NormalSimulFail.ConstMsgPerPeer100SimulFailTest-0.8.tab');
s9 = importtab('P:\NormalSimulFail.ConstMsgPerPeer100SimulFailTest-0.9.tab');

count = 1000;
resents0 = 0;
resents1 = averagegroup(s1.DHT_MsgResent_Count(find(s1.Host_Peer_Count == count)) ./ s1.DHT_MsgSent_Count(find(s1.Host_Peer_Count == count)),3);
resents2 = averagegroup(s2.DHT_MsgResent_Count(find(s2.Host_Peer_Count == count)) ./ s2.DHT_MsgSent_Count(find(s2.Host_Peer_Count == count)),3);
resents3 = averagegroup(s3.DHT_MsgResent_Count(find(s3.Host_Peer_Count == count)) ./ s3.DHT_MsgSent_Count(find(s3.Host_Peer_Count == count)),3);
resents4 = averagegroup(s4.DHT_MsgResent_Count(find(s4.Host_Peer_Count == count)) ./ s4.DHT_MsgSent_Count(find(s4.Host_Peer_Count == count)),3);
resents5 = averagegroup(s5.DHT_MsgResent_Count(find(s5.Host_Peer_Count == count)) ./ s5.DHT_MsgSent_Count(find(s5.Host_Peer_Count == count)),3);
resents6 = averagegroup(s6.DHT_MsgResent_Count(find(s6.Host_Peer_Count == count)) ./ s6.DHT_MsgSent_Count(find(s6.Host_Peer_Count == count)),3);
resents7 = averagegroup(s7.DHT_MsgResent_Count(find(s7.Host_Peer_Count == count)) ./ s7.DHT_MsgSent_Count(find(s7.Host_Peer_Count == count)),3);
resents8 = averagegroup(s8.DHT_MsgResent_Count(find(s8.Host_Peer_Count == count)) ./ s8.DHT_MsgSent_Count(find(s8.Host_Peer_Count == count)),3);
resents9 = averagegroup(s9.DHT_MsgResent_Count(find(s9.Host_Peer_Count == count)) ./ s9.DHT_MsgSent_Count(find(s9.Host_Peer_Count == count)),3);

percent = 0:10:90;
fails = [resents0 resents1 resents2 resents3 resents4 resents5 resents6 resents7 resents8 resents9];

f = figure();
hold on

plot(percent,fails,'k-','LineWidth',2);

count = 100;
resents1 = averagegroup(s1.DHT_MsgResent_Count(find(s1.Host_Peer_Count == count)) ./ s1.DHT_MsgSent_Count(find(s1.Host_Peer_Count == count)),3);
resents2 = averagegroup(s2.DHT_MsgResent_Count(find(s2.Host_Peer_Count == count)) ./ s2.DHT_MsgSent_Count(find(s2.Host_Peer_Count == count)),3);
resents3 = averagegroup(s3.DHT_MsgResent_Count(find(s3.Host_Peer_Count == count)) ./ s3.DHT_MsgSent_Count(find(s3.Host_Peer_Count == count)),3);
resents4 = averagegroup(s4.DHT_MsgResent_Count(find(s4.Host_Peer_Count == count)) ./ s4.DHT_MsgSent_Count(find(s4.Host_Peer_Count == count)),3);
resents5 = averagegroup(s5.DHT_MsgResent_Count(find(s5.Host_Peer_Count == count)) ./ s5.DHT_MsgSent_Count(find(s5.Host_Peer_Count == count)),3);
resents6 = averagegroup(s6.DHT_MsgResent_Count(find(s6.Host_Peer_Count == count)) ./ s6.DHT_MsgSent_Count(find(s6.Host_Peer_Count == count)),3);
resents7 = averagegroup(s7.DHT_MsgResent_Count(find(s7.Host_Peer_Count == count)) ./ s7.DHT_MsgSent_Count(find(s7.Host_Peer_Count == count)),3);
resents8 = averagegroup(s8.DHT_MsgResent_Count(find(s8.Host_Peer_Count == count)) ./ s8.DHT_MsgSent_Count(find(s8.Host_Peer_Count == count)),3);
resents9 = averagegroup(s9.DHT_MsgResent_Count(find(s9.Host_Peer_Count == count)) ./ s9.DHT_MsgSent_Count(find(s9.Host_Peer_Count == count)),3);
fails = [resents0 resents1 resents2 resents3 resents4 resents5 resents6 resents7 resents8 resents9];
plot(percent,fails,'k--','LineWidth',2);

count = 10;
resents1 = averagegroup(s1.DHT_MsgResent_Count(find(s1.Host_Peer_Count == count)) ./ s1.DHT_MsgSent_Count(find(s1.Host_Peer_Count == count)),3);
resents2 = averagegroup(s2.DHT_MsgResent_Count(find(s2.Host_Peer_Count == count)) ./ s2.DHT_MsgSent_Count(find(s2.Host_Peer_Count == count)),3);
resents3 = averagegroup(s3.DHT_MsgResent_Count(find(s3.Host_Peer_Count == count)) ./ s3.DHT_MsgSent_Count(find(s3.Host_Peer_Count == count)),3);
resents4 = averagegroup(s4.DHT_MsgResent_Count(find(s4.Host_Peer_Count == count)) ./ s4.DHT_MsgSent_Count(find(s4.Host_Peer_Count == count)),3);
resents5 = averagegroup(s5.DHT_MsgResent_Count(find(s5.Host_Peer_Count == count)) ./ s5.DHT_MsgSent_Count(find(s5.Host_Peer_Count == count)),3);
resents6 = averagegroup(s6.DHT_MsgResent_Count(find(s6.Host_Peer_Count == count)) ./ s6.DHT_MsgSent_Count(find(s6.Host_Peer_Count == count)),3);
resents7 = averagegroup(s7.DHT_MsgResent_Count(find(s7.Host_Peer_Count == count)) ./ s7.DHT_MsgSent_Count(find(s7.Host_Peer_Count == count)),3);
resents8 = averagegroup(s8.DHT_MsgResent_Count(find(s8.Host_Peer_Count == count)) ./ s8.DHT_MsgSent_Count(find(s8.Host_Peer_Count == count)),3);
resents9 = averagegroup(s9.DHT_MsgResent_Count(find(s9.Host_Peer_Count == count)) ./ s9.DHT_MsgSent_Count(find(s9.Host_Peer_Count == count)),3);
fails = [resents0 resents1 resents2 resents3 resents4 resents5 resents6 resents7 resents8 resents9];
plot(percent,fails,'k:','LineWidth',2);

set(f,'defaultaxesfontsize', 14);

width = 2;
rep = 3;

xlabel('Percentage Failure', 'FontSize', 14);
ylabel('Percentage of Resent Messages', 'FontSize', 14);

h_legend=legend('1000 Pastry Nodes','100 Pastry Nodes','10 Pastry Nodes');
h_legend=legend('Location', 'NorthWest');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

hold off
clear count styles width h_legend h_text f