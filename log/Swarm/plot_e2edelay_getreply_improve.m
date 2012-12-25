%s = importtab('P:\Swarm\KeysTest\Global.tab');
%n = importtab('P:\Swarm\KeysTestNormal\Global.tab');

%s = importtab('P:\Swarm\KeysChurnTest\Global.tab');
%n = importtab('P:\Swarm\KeysChurnNormalTest\Global.tab');

f = figure('Position', [200 200 658 420]);

set(f,'defaultaxesfontsize', 14);

width = 2;
rep = 3;

% 95% High, 75% High.. 5% High
%ratios = [0.95 0.90 0.75 0.50 0.25 0.10 0.05];
ratios = [0.95 0.75 0.50 0.25 0.05];


x = find(n.Host_SwarmPeerRatio == ratios(5));
y = find(s.Host_SwarmPeerRatio == ratios(5));
plot(averagegroup(n.Host_Peer_Count(x),rep),averagegroup(n.DHT_GetReplyMessage_E2ELatency_Avg(x) - s.DHT_GetReplyMessage_E2ELatency_Avg(y),rep),'k-','LineWidth', width);
hold on

x = find(n.Host_SwarmPeerRatio == ratios(4));
y = find(s.Host_SwarmPeerRatio == ratios(4));
plot(averagegroup(n.Host_Peer_Count(x),rep),averagegroup(n.DHT_GetReplyMessage_E2ELatency_Avg(x) - s.DHT_GetReplyMessage_E2ELatency_Avg(y),rep),'k--','LineWidth', width);

x = find(n.Host_SwarmPeerRatio == ratios(3));
y = find(s.Host_SwarmPeerRatio == ratios(3));
plot(averagegroup(n.Host_Peer_Count(x),rep),averagegroup(n.DHT_GetReplyMessage_E2ELatency_Avg(x) - s.DHT_GetReplyMessage_E2ELatency_Avg(y),rep),'k:','LineWidth', width);

x = find(n.Host_SwarmPeerRatio == ratios(2));
y = find(s.Host_SwarmPeerRatio == ratios(2));
plot(averagegroup(n.Host_Peer_Count(x),rep),averagegroup(n.DHT_GetReplyMessage_E2ELatency_Avg(x) - s.DHT_GetReplyMessage_E2ELatency_Avg(y),rep),'k-.','LineWidth', width);

x = find(n.Host_SwarmPeerRatio == ratios(1));
y = find(s.Host_SwarmPeerRatio == ratios(1));
plot(averagegroup(n.Host_Peer_Count(x),rep),averagegroup(n.DHT_GetReplyMessage_E2ELatency_Avg(x) - s.DHT_GetReplyMessage_E2ELatency_Avg(y),rep),'k-x','LineWidth', width);

%title('Improvement in End-to-End Latency for GetReply Messages');

xlabel('DHT Network Size', 'FontSize', 14);
ylabel('Decrease in End-to-End Latency', 'FontSize', 14);

h_legend=legend('95% Mobile', '75% Mobile', '50% Mobile', '25% Mobile', '5% Mobile');
h_legend=legend('Location', 'Best');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

hold off
clear count styles width h_legend h_text f