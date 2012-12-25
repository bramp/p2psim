% Decrease in number of forwarded messages.

n = importtab('P:\Normal 2.KeysTest.tab');
%s1 = importtab('P:\Stealth (Requests Only).KeysTest-0.01.tab');
s5 = importtab('P:\Stealth (Requests Only).KeysTest-0.05.tab');
%s10 = importtab('P:\Stealth (Requests Only).KeysTest-0.10.tab');
s20 = importtab('P:\Stealth (Requests Only).KeysTest-0.20.tab');
%s30 = importtab('P:\Stealth (Requests Only).KeysTest-0.30.tab');
s50 = importtab('P:\Stealth (Requests Only).KeysTest-0.50.tab');

f = figure('Position', [200 200 658 420]);
set(f,'defaultaxesfontsize', 14);

n.MsgFwd = (n.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;
s50.MsgFwd = (s50.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;
%s30.MsgFwd = (s30.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;
s20.MsgFwd =  (s20.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;
%s10.MsgFwd =  (s10.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;
s5.MsgFwd =  (s5.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;
%s1.MsgFwd =  (s1.DHT_MsgFwd_Count ./ n.DHT_MsgFwd_Count) * 100;

%plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(n.MsgFwd, 3), 'k-', 'LineWidth', 2);

plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(s50.MsgFwd , 3), 'k-', 'LineWidth', 2);
hold on
%plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(s30.MsgFwd , 3), 'k--', 'LineWidth', 2);
plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(s20.MsgFwd , 3), 'k--', 'LineWidth', 2);
%plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(s10.MsgFwd , 3), 'k:', 'LineWidth', 2);
plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(s5.MsgFwd , 3), 'k-.', 'LineWidth', 2);
%plot(averagegroup(n.Host_Peer_Count, 3), averagegroup(s1.MsgFwd , 3), 'k--', 'LineWidth', 2);

axis([0 1000 -5 100])

xlabel('DHT Network Size', 'FontSize', 14);
ylabel({'Fraction of forwarded';'messages compared to Pastry'}, 'FontSize', 14);

%h_legend=legend('Stealth (50%)', 'Stealth (70%)', 'Stealth (80%)', 'Stealth (90%)', 'Stealth (95%)', 'Stealth (99%)');
h_legend=legend('Stealth (50%)', 'Stealth (80%)', 'Stealth (95%)');
h_legend=legend('Location', 'SouthEast');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

hold off