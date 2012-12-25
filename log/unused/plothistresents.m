%Plots cdf of # of resents per messages, for stealth and normal

[norm1 norm2 norm3 data1 data2 data3] = normvsstealth();

f = figure();
set(f,'defaultaxesfontsize', 14);

resents = [data1.resents; data2.resents; data3.resents];
%[y, x] = ecdf(resents);
% plot(x, y, 'k-', 'LineWidth', 2);
hist(resents,200);

hold on

resents = [norm1.resents; norm2.resents; norm3.resents];
%[y, x] = ecdf(resents);
%plot(x, y, 'k:', 'LineWidth', 2);

h_legend=legend('Stealth (95%)', 'Pastry');
h_legend=legend('Location', 'SouthEast');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')


xlabel('Number of resends per message', 'FontSize', 14);
ylabel('CDF', 'FontSize', 14);