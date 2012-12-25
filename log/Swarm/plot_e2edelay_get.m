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


%x = find(s.Host_SwarmPeerRatio == ratios(5));
%plot(averagegroup(s.Host_Peer_Count(x),rep),averagegroup(s.DHT_GetMessage_E2ELatency_Avg(x),rep),'k-','LineWidth', width);
%hold on

x = find(n.Host_SwarmPeerRatio == ratios(5));
plot(averagegroup(n.Host_Peer_Count(x),rep),averagegroup(n.DHT_GetMessage_E2ELatency_Avg(x),rep),'k--','LineWidth', width);
hold on

%x = find(s.Host_SwarmPeerRatio == ratios(3));
%plot(averagegroup(s.Host_Peer_Count(x),rep),averagegroup(s.DHT_GetMessage_E2ELatency_Avg(x),rep),'k-.','LineWidth', width);

x = find(n.Host_SwarmPeerRatio == ratios(3));
plot(averagegroup(n.Host_Peer_Count(x),rep),averagegroup(n.DHT_GetMessage_E2ELatency_Avg(x),rep),'k:','LineWidth', width);

%x = find(s.Host_SwarmPeerRatio == ratios(1));
%plot(averagegroup(s.Host_Peer_Count(x),rep),averagegroup(s.DHT_GetMessage_E2ELatency_Avg(x),rep),'k-x','LineWidth', width);

x = find(n.Host_SwarmPeerRatio == ratios(1));
plot(averagegroup(n.Host_Peer_Count(x),rep),averagegroup(n.DHT_GetMessage_E2ELatency_Avg(x),rep),'k--x','LineWidth', width);
hold on

%title('Average End-to-End Latency for Get Messages');

xlabel('DHT Network Size', 'FontSize', 14);
ylabel('Average End-to-End Latency', 'FontSize', 14);

h_legend=legend('Pastry (95% Mobile)', 'Pastry (50% Mobile)',  'Pastry (5% Mobile)');
h_legend=legend('Location', 'Best');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

hold off
clear count styles width h_legend h_text f