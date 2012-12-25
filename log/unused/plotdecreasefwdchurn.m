% Decrease in number of forwarded messages.

n = importtab('P:\Churn\Normal ~unfinished\1119898155.KeysChurnTest-0.05\Global.tab');
s1 = importtab('P:\Churn\Stealth\1119891269.KeysChurnTest-0.01\Global.tab');
s5 = importtab('P:\Churn\Stealth\1119891269.KeysChurnTest-0.05\Global.tab');
s10 = importtab('P:\Churn\Stealth\1119891269.KeysChurnTest-0.1\Global.tab');
s20 = importtab('P:\Churn\Stealth\1119891269.KeysChurnTest-0.2\Global.tab');
s30 = importtab('P:\Churn\Stealth\1119891269.KeysChurnTest-0.3\Global.tab');
s50 = importtab('P:\Churn\Stealth\1119891269.KeysChurnTest-0.5\Global.tab');

n.DHT_MsgFwd_Count = n.DHT_MsgFwd_Count(1:51);
s1.DHT_MsgFwd_Count = s1.DHT_MsgFwd_Count(1:51);
s5.DHT_MsgFwd_Count = s5.DHT_MsgFwd_Count(1:51);
s10.DHT_MsgFwd_Count = s10.DHT_MsgFwd_Count(1:51);
s20.DHT_MsgFwd_Count = s20.DHT_MsgFwd_Count(1:51);
s30.DHT_MsgFwd_Count = s30.DHT_MsgFwd_Count(1:51);
s50.DHT_MsgFwd_Count = s50.DHT_MsgFwd_Count(1:51);

f = figure('Position', [200 200 658 420]);
set(f,'defaultaxesfontsize', 14);

n.MsgFwd = (n.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;
s50.MsgFwd = (s50.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;
s30.MsgFwd = (s30.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;
s20.MsgFwd =  (s20.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;
s10.MsgFwd =  (s10.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;
s5.MsgFwd =  (s5.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;
s1.MsgFwd =  (s1.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;

%plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(n.MsgFwd, 3), 'k-', 'LineWidth', 2);

plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(s50.MsgFwd , 3), 'k-', 'LineWidth', 2);
hold on
%plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(s30.MsgFwd , 3), 'k--', 'LineWidth', 2);
plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(s20.MsgFwd , 3), 'k--', 'LineWidth', 2);
plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(s10.MsgFwd , 3), 'k:', 'LineWidth', 2);
plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(s5.MsgFwd , 3), 'k-.', 'LineWidth', 2);
%plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(s1.MsgFwd , 3), 'k--', 'LineWidth', 2);

%axis([0 1000 -10 100])

xlabel('DHT Network Size', 'FontSize', 14);
ylabel({'Percentage of Forwarded';'Messages Compared to Pastry'}, 'FontSize', 14);

%h_legend=legend('Stealth (50%)', 'Stealth (70%)', 'Stealth (80%)', 'Stealth (90%)', 'Stealth (95%)', 'Stealth (99%)');
h_legend=legend('Stealth (50%)', 'Stealth (80%)', 'Stealth (90%)', 'Stealth (95%)');
h_legend=legend('Location', 'SouthEast');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

hold off