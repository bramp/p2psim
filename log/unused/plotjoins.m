n = importtab2('P:\Normal.tab');
s = importtab2('P:\stealth.tab');

n.SentBandwidth = n.JoinMessage * 30 + n.JoinFinishedMessage * 30 + n.PairsMessage * 30 + n.Pairs * 22;
n.TotalBandwidth = n.TotalJoinMessage * 30 + n.TotalJoinFinishedMessage * 30 + n.TotalPairsMessage * 30 + n.TotalPairs * 22;
n.TotalMessages = n.TotalJoinMessage + n.TotalJoinFinishedMessage + n.TotalPairsMessage;

s.SentBandwidth = s.JoinMessage * 30 + s.JoinFinishedMessage * 30 + s.PairsMessage * 30 + s.Pairs * 22;
s.TotalBandwidth = s.TotalJoinMessage * 30 + s.TotalJoinFinishedMessage * 30 + s.TotalPairsMessage * 30 + s.TotalPairs * 22;
s.TotalMessages = s.TotalJoinMessage + s.TotalJoinFinishedMessage + s.TotalPairsMessage;


f = figure('Position', [200 200 658 420]);
set(f,'defaultaxesfontsize', 14);

loglog(averagegroup(n.Node - 1, 3), averagegroup(n.TotalMessages, 3), 'k-', 'LineWidth', 2);

hold on

x = find(s.Ratio == 0.5);
loglog(averagegroup(s.Node(x), 3), averagegroup(s.TotalMessages(x), 3), 'k--', 'LineWidth', 2);

%x = find(s.Ratio == 0.3);
%loglog(averagegroup(s.Node(x), 3), averagegroup(s.TotalMessages(x), 3), 'k--', 'LineWidth', 2);

x = find(s.Ratio == 0.2);
loglog(averagegroup(s.Node(x), 3), averagegroup(s.TotalMessages(x), 3), 'k:', 'LineWidth', 2);

%x = find(s.Ratio == 0.1);
%loglog(averagegroup(s.Node(x), 3), averagegroup(s.TotalMessages(x), 3), 'k*-', 'LineWidth', 2);

x = find(s.Ratio == 0.05);
loglog(averagegroup(s.Node(x), 3), averagegroup(s.TotalMessages(x), 3), 'k-.', 'LineWidth', 2);

xlabel('DHT Network Size', 'FontSize', 14);
ylabel('Total Exchanged Messages', 'FontSize', 14);
%legend('Pastry', '50%', '30%', '20%', '10%', '1%');

h_legend=legend('Pastry', 'Stealth (50%)', 'Stealth (80%)', 'Stealth (95%)');
h_legend=legend('Location', 'NorthWest');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

hold off

f = figure('Position', [200 200 658 420]);
set(f,'defaultaxesfontsize', 14);

loglog(averagegroup(n.Node - 1, 3), averagegroup(n.TotalMessages ./ (n.Node - 1), 3), 'k-', 'LineWidth', 2);

hold on

x = find(s.Ratio == 0.5);
loglog(averagegroup(s.Node(x), 3), averagegroup(s.TotalMessages(x) ./ s.Node(x), 3), 'k--', 'LineWidth', 2);

%x = find(s.Ratio == 0.3);
%loglog(averagegroup(s.Node(x), 3), averagegroup(s.TotalMessages(x) ./ s.Node(x), 3), 'k--', 'LineWidth', 2);

x = find(s.Ratio == 0.2);
loglog(averagegroup(s.Node(x), 3), averagegroup(s.TotalMessages(x) ./ s.Node(x), 3), 'k:', 'LineWidth', 2);

%x = find(s.Ratio == 0.1);
%loglog(averagegroup(s.Node(x), 3), averagegroup(s.TotalMessages(x) ./ s.Node(x), 3), 'k*-', 'LineWidth', 2);

x = find(s.Ratio == 0.05);
loglog(averagegroup(s.Node(x), 3), averagegroup(s.TotalMessages(x) ./ s.Node(x), 3), 'k-.', 'LineWidth', 2);

xlabel('DHT Network Size', 'FontSize', 14);
ylabel('Exchanged Messages per Node', 'FontSize', 14);

h_legend=legend('Pastry', 'Stealth (50%)', 'Stealth (80%)', 'Stealth (95%)');
h_legend=legend('Location', 'NorthWest');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')