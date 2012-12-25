%Plots cdf of # of delay per messages, for stealth and normal

[norm1 norm2 norm3 data1 data2 data3] = normvsstealth();

f = figure();
set(f,'defaultaxesfontsize', 14);

delays = [data1.delays; data2.delays; data3.delays];
[y, x] = ecdf(delays);
semilogx(x, y, 'k-', 'LineWidth', 2);

hold on

delays = [norm1.delays; norm2.delays; norm3.delays];
[y, x] = ecdf(delays);
semilogx(x, y, 'k:', 'LineWidth', 2);

h_legend=legend('Stealth (95%)', 'Pastry');
h_legend=legend('Location', 'SouthEast');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')


xlabel('Delay', 'FontSize', 14);
ylabel('CDF', 'FontSize', 14);