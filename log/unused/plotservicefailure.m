% Decrease in number of forwarded messages.

data = importtab('P:\Service Failure\Global.tab');

s5 = struct();
s10 = struct();
s25 = struct();
s50 = struct();
s75 = struct();
s100 = struct();
s150 = struct();
s250 = struct();

x = find(data.Host_NormalPeer_Count == 5);
s5.Host_Peer_Count = data.Host_Peer_Count(x);
s5.Host_StealthPeer_Count = data.Host_StealthPeer_Count(x);
s5.DHT_MsgResent_Count = data.DHT_MsgResent_Count(x);
s5.DHT_MsgRecv_Count = data.DHT_MsgRecv_Count(x);
s5.DHT_Hops_Avg = data.DHT_Hops_Avg(x);

x = find(data.Host_NormalPeer_Count == 10);
s10.Host_Peer_Count = data.Host_Peer_Count(x);
s10.Host_StealthPeer_Count = data.Host_StealthPeer_Count(x);
s10.DHT_MsgResent_Count = data.DHT_MsgResent_Count(x);
s10.DHT_MsgRecv_Count = data.DHT_MsgRecv_Count(x);
s10.DHT_Hops_Avg = data.DHT_Hops_Avg(x);

x = find(data.Host_NormalPeer_Count == 25);
s25.Host_Peer_Count = data.Host_Peer_Count(x);
s25.Host_StealthPeer_Count = data.Host_StealthPeer_Count(x);
s25.DHT_MsgResent_Count = data.DHT_MsgResent_Count(x);
s25.DHT_MsgRecv_Count = data.DHT_MsgRecv_Count(x);
s25.DHT_Hops_Avg = data.DHT_Hops_Avg(x);

x = find(data.Host_NormalPeer_Count == 50);
s50.Host_Peer_Count = data.Host_Peer_Count(x);
s50.Host_StealthPeer_Count = data.Host_StealthPeer_Count(x);
s50.DHT_MsgResent_Count = data.DHT_MsgResent_Count(x);
s50.DHT_MsgRecv_Count = data.DHT_MsgRecv_Count(x);
s50.DHT_Hops_Avg = data.DHT_Hops_Avg(x);

x = find(data.Host_NormalPeer_Count == 100);
s100.Host_Peer_Count = data.Host_Peer_Count(x);
s100.Host_StealthPeer_Count = data.Host_StealthPeer_Count(x);
s100.DHT_MsgResent_Count = data.DHT_MsgResent_Count(x);
s100.DHT_MsgRecv_Count = data.DHT_MsgRecv_Count(x);
s100.DHT_Hops_Avg = data.DHT_Hops_Avg(x);

f = figure();
set(f,'defaultaxesfontsize', 14);

plot(averagegroup(s5.Host_StealthPeer_Count, 3), averagegroup(s5.DHT_MsgResent_Count, 3) ./ (averagegroup(s5.DHT_MsgRecv_Count, 3) +  averagegroup(s5.DHT_MsgResent_Count, 3)), 'k-', 'LineWidth', 2);
%plot(averagegroup(s5.Host_StealthPeer_Count, 3), averagegroup(s5.DHT_Hops_Avg, 3), 'k-', 'LineWidth', 2);
hold on

%plot(averagegroup(s10.Host_StealthPeer_Count, 3), averagegroup(s10.DHT_Hops_Avg, 3) , 'k--', 'LineWidth', 2);
plot(averagegroup(s10.Host_StealthPeer_Count, 3), averagegroup(s10.DHT_MsgResent_Count, 3) ./ (averagegroup(s10.DHT_MsgRecv_Count, 3) +  averagegroup(s10.DHT_MsgResent_Count, 3)), 'k--', 'LineWidth', 2);

%plot(averagegroup(s25.Host_StealthPeer_Count, 3), averagegroup(s25.DHT_Hops_Avg, 3) , 'k--', 'LineWidth', 2);
plot(averagegroup(s25.Host_StealthPeer_Count, 3), averagegroup(s25.DHT_MsgResent_Count, 3) ./ (averagegroup(s25.DHT_MsgRecv_Count, 3) +  averagegroup(s25.DHT_MsgResent_Count, 3)), 'k--', 'LineWidth', 2);


%plot(averagegroup(s50.Host_StealthPeer_Count, 3), averagegroup(s50.DHT_Hops_Avg, 3), 'k--', 'LineWidth', 2);
plot(averagegroup(s50.Host_StealthPeer_Count, 3), averagegroup(s50.DHT_MsgResent_Count, 3) ./ (averagegroup(s50.DHT_MsgRecv_Count, 3) +  averagegroup(s50.DHT_MsgResent_Count, 3)), 'k-.', 'LineWidth', 2);

%plot(averagegroup(s50.Host_StealthPeer_Count, 3), averagegroup(s50.DHT_Hops_Avg, 3), 'k--', 'LineWidth', 2);
plot(averagegroup(s100.Host_StealthPeer_Count, 3), averagegroup(s100.DHT_MsgResent_Count, 3) ./ (averagegroup(s100.DHT_MsgRecv_Count, 3) +  averagegroup(s100.DHT_MsgResent_Count, 3)), 'k-.', 'LineWidth', 2);


xlabel('DHT Network Size (Stealth Nodes)', 'FontSize', 14);
%ylabel('Network Stress', 'FontSize', 14);

h_legend=legend('5 Service Nodes', '10 Service Nodes', '25 Service Nodes', '50 Service Nodes', '100 Service Nodes');
h_legend=legend('Location', 'Best');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

hold off