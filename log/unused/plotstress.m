% Decrease in number of forwarded messages.

%stealth = importstress('P:\Reruns\Without Churn\1120590070.stealth.KeysTest-0.05\traces\');
%normal = importstress('P:\Reruns\Without Churn\1120590070.puredht.KeysTest-0.05\traces\');
stealth = importstress('P:\Reruns\With Churn\1120594450.stealth.KeysChurnTest-0.05\traces\');
normal = importstress('P:\Reruns\With Churn\1120589240.puredht.KeysChurnTest-0.05\traces\');

stealth2 = [stealth.Nodes; stealth.Stress; stealth.StressMax]';
normal2 = [normal.Nodes; normal.Stress; normal.StressMax]';

stealth2 = sortrows(stealth2, 1);
normal2 = sortrows(normal2, 1);

f = figure('Position', [200 200 658 420]);
set(f,'defaultaxesfontsize', 14);

loglog(averagegroup(normal2(:,1), 3), averagegroup(normal2(:,3), 3), 'k-', 'LineWidth', 2);
hold on

loglog(averagegroup(normal2(:,1), 3), averagegroup(normal2(:,2), 3), 'k--', 'LineWidth', 2);
loglog(averagegroup(stealth2(:,1), 3), averagegroup(stealth2(:,3), 3), 'k:', 'LineWidth', 2);
loglog(averagegroup(stealth2(:,1), 3), averagegroup(stealth2(:,2), 3), 'k-.', 'LineWidth', 2);

xlabel('DHT Network Size', 'FontSize', 14);
ylabel('Network Stress', 'FontSize', 14);

%h_legend=legend('Pastry', 'Stealth (95%)');
h_legend=legend('Pastry Max', 'Pastry Average', 'Stealth (95%) Max', 'Stealth (95%) Average');
h_legend=legend('Location', 'NorthWest');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

hold off