data = importtab('P:\Stealth KeysTest2 (fixed service nodes)\Global.tab');

hopsnoreply = ((data.DHT_Hops_Avg .* data.DHT_MsgRecv_Count)-data.DHT_GetReplyMessage_Count)./(data.DHT_MsgRecv_Count-data.DHT_GetReplyMessage_Count);

f = figure('Position', [200 200 658 420]);
set(f,'defaultaxesfontsize', 14);

count = [250 75 25 5];
style = {'k-';'k--';'k-.';'k:';'ko';'kx';'k+';'ks';'kd'};
width = 2;

for i=1:length(count)
    x = find(data.Host_NormalPeer_Count == count(i));
    plot(averagegroup(data.Host_StealthPeer_Count(x),3),averagegroup(hopsnoreply(x),3),char(style(i)),'LineWidth', width);
    %plot([0,max(data.Host_StealthPeer_Count(x))],[logb(count(i),16),logb(count(i),16)],'k-','LineWidth',1);
    
    hold on
end

xlabel('Number of Stealth Nodes', 'FontSize', 14);
ylabel('Average DHT Hops', 'FontSize', 14);

h_legend=legend('250 Service nodes', '75 Service nodes', '25 Service nodes', '5 Service nodes');
h_legend=legend('Location', 'Best');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

hold off
clear count styles width h_legend h_text f