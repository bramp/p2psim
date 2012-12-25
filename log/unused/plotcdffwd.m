%Plots cdf of messages fwd per node, for stealth and normal

[norm1 norm2 norm3 data1 data2 data3] = normvsstealth();

f = figure('Position', [200 200 658 420]);
set(f,'defaultaxesfontsize', 14);

nodefwds = [data1.nodefwds; data2.nodefwds; data3.nodefwds];
[y, x] = ecdf(nodefwds);
semilogx(x, y, 'k-', 'LineWidth', 2);

hold on

nodefwds = [norm1.nodefwds; norm2.nodefwds; norm3.nodefwds];
[y, x] = ecdf(nodefwds);
semilogx(x, y, 'k:', 'LineWidth', 2);

h_legend=legend('Stealth (95%)', 'Pastry');
h_legend=legend('Location', 'SouthEast');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')


xlabel('Messages forwarded per node', 'FontSize', 14);
ylabel('CDF', 'FontSize', 14);
