s = importtab('P:\old_logs\Justin\Swarm\KeysTest\Global.tab');
n = importtab('P:\old_logs\Justin\Swarm\KeysTestNormal\Global.tab');

%s = importtab('P:\old_logs\Justin\Swarm~\KeysChurnTest\Global.tab');
%n = importtab('P:\old_logs\Justin\Swarm~\KeysChurnNormalTest\Global.tab');

f = figure('Position', [200 200 658 420]);

set(f,'defaultaxesfontsize', 14);

width = 2;
rep = 3;

ratios = [0.95 0.50 0.05];

x = find(n.Host_SwarmPeerRatio == ratios(3));
plot(averagegroup(n.Host_Peer_Count(x),rep),averagegroup(n.DHT_GetReplyMessage_Stretch_Avg(x),rep),'k--','LineWidth', width);
hold on

%x = find(s.Host_SwarmPeerRatio == ratios(3));
%plot(averagegroup(s.Host_Peer_Count(x),rep),averagegroup(s.DHT_GetReplyMessage_Stretch_Avg(x),rep),'k-','LineWidth', width);

x = find(n.Host_SwarmPeerRatio == ratios(2));
plot(averagegroup(n.Host_Peer_Count(x),rep),averagegroup(n.DHT_GetReplyMessage_Stretch_Avg(x),rep),'k-.','LineWidth', width);

%x = find(s.Host_SwarmPeerRatio == ratios(2));
%plot(averagegroup(s.Host_Peer_Count(x),rep),averagegroup(s.DHT_GetReplyMessage_Stretch_Avg(x),rep),'k:','LineWidth', width);

x = find(n.Host_SwarmPeerRatio == ratios(1));
plot(averagegroup(n.Host_Peer_Count(x),rep),averagegroup(n.DHT_GetReplyMessage_Stretch_Avg(x),rep),'k--x','LineWidth', width);

%x = find(s.Host_SwarmPeerRatio == ratios(1));
%plot(averagegroup(s.Host_Peer_Count(x),rep),averagegroup(s.DHT_GetReplyMessage_Stretch_Avg(x),rep),'k-x','LineWidth', width);

%title('Average DHT Stretch for GetReply Messages');

xlabel('DHT Network Size', 'FontSize', 14);
ylabel('Average Stretch', 'FontSize', 14);

h_legend=legend('Pastry (95% Mobile)', 'Pastry (50% Mobile)', 'Pastry (5% Mobile)');
h_legend=legend('Location', 'Best');
%h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

%axis([0 1000 1 2.6]);

hold off
clear count styles width h_legend h_text f