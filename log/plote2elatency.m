
a = importtab('1124472271sim.workload.mobile.MovingPastryTest.MovingPastryTest-10/Global.tab');
b = importtab('1124472271sim.workload.mobile.StaticPastryTest.StaticPastryTest-10/Global.tab');

// Sort the data
a = [a.Host_Peer_Count a.DHT_Message_E2ELatency_Avg];
b = [b.Host_Peer_Count b.DHT_Message_E2ELatency_Avg];

plot(averagegroup(a.Host_Peer_Count, 3), averagegroup(a.DHT_Message_E2ELatency_Avg, 3), 'k-','LineWidth', 2);
plot(averagegroup(b.Host_Peer_Count, 3), averagegroup(b.DHT_Message_E2ELatency_Avg, 3), 'k-','LineWidth', 2);

f = figure();
set(f,'defaultaxesfontsize', 14);

width = 2;
rep = 3;

%xlabel('Percentage Failure', 'FontSize', 14);
%ylabel('Percentage of Resent Messages', 'FontSize', 14);

h_legend=legend('Mobile Nodes','Static Nodes');
h_legend=legend('Location', 'Best');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

hold off