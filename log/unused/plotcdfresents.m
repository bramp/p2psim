%Plots cdf of # of resents per messages, for stealth and normal

%[norm1 norm2 norm3 data1 data2 data3] = normvsstealthchurn();

f = figure('Position', [200 200 658 420]);
set(f,'defaultaxesfontsize', 14);

%resents = [data1.resents; data2.resents; data3.resents];
%[y, x] = ecdf(resents);
%plot(x, y, 'k-', 'LineWidth', 2);

resents = [norm1.resents; norm2.resents; norm3.resents] / 10;
%[y, x] = ecdf(resents);
%plot(x, y, 'k:', 'LineWidth', 2);

[y, x] = hist(resents, 50);

% remove 0 value and switch to very small number (so log(y) doesn't break)
y(find (y == 0)) = 1;

semilogy(x, y, 'k-', 'LineWidth', 2);

axis([0 35 0.7 10^7]); 

hold on

%h_legend=legend('Pastry');
%h_legend=legend('Location', 'NorthEast');
%h_legend=legend('boxoff');

%h_text = findobj(h_legend,'type','text');
%set(h_text,'FontUnits','points','FontSize',14);
%set(h_text,'FontUnits','normal');

%axis([0 50 1 10000000]);

xlabel('Number of resends per message', 'FontSize', 14);
ylabel('Frequency', 'FontSize', 14);