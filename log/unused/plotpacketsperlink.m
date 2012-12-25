%Plots packets per link CDF for Normal and Stealth

[norm1 norm2 norm3 data1 data2 data3] = normvsstealth();

f = figure('Position', [200 200 658 420]);
set(f,'defaultaxesfontsize', 14);

linkcount_uni = [data1.linkcount_uni; data2.linkcount_uni; data3.linkcount_uni];
[y, x] = ecdf(linkcount_uni);
semilogx(x, y, 'k-', 'LineWidth', 2);

hold on

linkcount_uni = sort(linkcount_uni, 'descend');
linkcount_uni = linkcount_uni(1:round(length(linkcount_uni) / (100 / 20) ));
[y, x] = ecdf(linkcount_uni);
semilogx(x, y, 'k--', 'LineWidth', 2);

linkcount_uni = [norm1.linkcount_uni; norm2.linkcount_uni; norm3.linkcount_uni];
[y, x] = ecdf(linkcount_uni);
semilogx(x, y, 'k:', 'LineWidth', 2);

h_legend=legend('Stealth (95%)', 'Stealth 80th Percentile', 'Pastry');
%h_legend=legend('Location', 'SouthEast');
h_legend=legend('Location', 'NorthWest');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

%axis( [0 10^6 0 1] );

xlabel('Packets per link', 'FontSize', 14);
ylabel('CDF', 'FontSize', 14);
