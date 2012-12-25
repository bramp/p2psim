s05 = importtab('P:\Stealth Lifetime Tests.ConstMsgPerPeer100LifetimeTest-0.05.tab');
s20 = importtab('P:\Stealth Lifetime Tests.ConstMsgPerPeer100LifetimeTest-0.2.tab');
s50 = importtab('P:\Stealth Lifetime Tests.ConstMsgPerPeer100LifetimeTest-0.5.tab');
n = importtab('P:\Normal 2\1119469984838.ConstMsgPerPeer100Test\(Global)-2-1000-0-3.tab');

f = figure('Position', [200 200 658 420]);

set(f,'defaultaxesfontsize', 14);

width = 2;
rep = 3;

n.DHT_Hops_Avg = (n.DHT_Hops_Avg .* (n.DHT_Hops_Avg > 1));
n.DHT_Hops_Avg = n.DHT_Hops_Avg + (n.DHT_Hops_Avg == 0);

plot(averagegroup(n.Host_Peer_Count,rep),averagegroup(n.DHT_Hops_Avg,rep),'k-','LineWidth', width);
hold on
plot(averagegroup(s50.Host_Peer_Count,rep),averagegroup(s50.DHT_Hops_Avg,rep),'k--','LineWidth', width);
plot(averagegroup(s20.Host_Peer_Count,rep),averagegroup(s20.DHT_Hops_Avg,rep),'k:','LineWidth', width);
plot(averagegroup(s05.Host_Peer_Count,rep),averagegroup(s05.DHT_Hops_Avg,rep),'k-.','LineWidth', width);

xlabel('DHT Network Size', 'FontSize', 14);
ylabel('Average DHT Hops', 'FontSize', 14);

h_legend=legend('Pastry','Stealth (50%)','Stealth (80%)','Stealth (95%)');
h_legend=legend('Location', 'NorthWest');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

axis([0 1000 0.95 2.8]);

hold off
clear count styles width h_legend h_text f