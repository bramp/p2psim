%Plots cdf of messages recv per node, for stealth and normal

[norm1 norm2 norm3 data1 data2 data3] = normvsstealth();

f = figure('Position', [200 200 658 420]);
set(f,'defaultaxesfontsize', 14);

noderecvs = [data1.noderecvs; data2.noderecvs; data3.noderecvs];
[y, x] = ecdf(noderecvs);
semilogx(x, y, 'k-', 'LineWidth', 2);

hold on

x = find(noderecvs > 31);
noderecvs = noderecvs(x);
[y, x] = ecdf(noderecvs);
semilogx(x, y, 'k--', 'LineWidth', 2);

noderecvs = [norm1.noderecvs; norm2.noderecvs; norm3.noderecvs];
[y, x] = ecdf(noderecvs);
semilogx(x, y, 'k:', 'LineWidth', 2);

h_legend=legend('Stealth (95%)', 'Service Nodes Only', 'Pastry');
h_legend=legend('Location', 'SouthEast');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')


xlabel('Messages received per node', 'FontSize', 14);
ylabel('CDF', 'FontSize', 14);