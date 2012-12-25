%s = importtab('P:\Stretch\1120558907.KeysChurnTest-0.05\Global.tab');
%n = importtab('P:\Stretch\1120487117.KeysChurnTest-0.05\Global.tab');
%s1 = importtab('P:\Stretch\1120543316.KeysTest-0.05\Global.tab');
%n1 = importtab('P:\Stretch\1120544197.KeysTest-0.05\Global.tab');
s = importtab('P:\Reruns\With Churn\1120594450.stealth.KeysChurnTest-0.05\Global.tab');
n = importtab('P:\Reruns\With Churn\1120589240.puredht.KeysChurnTest-0.05\Global.tab');
s1 = importtab('P:\Reruns\Without Churn\1120590070.stealth.KeysTest-0.05\Global.tab');
n1 = importtab('P:\Reruns\Without Churn\1120590070.puredht.KeysTest-0.05\Global.tab');


f = figure('Position', [200 200 658 420]);

set(f,'defaultaxesfontsize', 14);

width = 2;
rep = 3;

plot(averagegroup(n.Host_Peer_Count,rep),averagegroup(n.DHT_Stretch,rep),'k-','LineWidth', width);
hold on

plot(averagegroup(n1.Host_Peer_Count,rep),averagegroup(n1.DHT_Stretch,rep),'k-.','LineWidth', width);
plot(averagegroup(s.Host_Peer_Count,rep),averagegroup(s.DHT_Stretch,rep),'k:','LineWidth', width);
plot(averagegroup(s1.Host_Peer_Count,rep),averagegroup(s1.DHT_Stretch,rep),'k--','LineWidth', width);

xlabel('DHT Network Size', 'FontSize', 14);
ylabel('DHT Stretch', 'FontSize', 14);

%axis( [0 1000 1 4] );

h_legend=legend('Pastry with Churn', 'Pastry', 'Stealth (95%) with Churn', 'Stealth (95%)');
h_legend=legend('Location', 'East');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

hold off
clear count styles width h_legend h_text f
