%Plots cdf of # of hops per messages, for stealth and normal

norm1 = importtab2 ('P:\Normal Churn ~unfinished\1119898155.KeysChurnTest-0.05\traces\output-n152-0.05-0.txt.gz.tab');
norm2 = importtab2 ('P:\Normal Churn ~unfinished\1119898155.KeysChurnTest-0.05\traces\output-n152-0.05-1.txt.gz.tab');
norm3 = importtab2 ('P:\Normal Churn ~unfinished\1119898155.KeysChurnTest-0.05\traces\output-n152-0.05-2.txt.gz.tab');
data1 = importtab2 ('P:\Stealth Churn\1119891269.KeysChurnTest-0.05\traces\output-n1000-0.05-0.txt.gz.tab');
data2 = importtab2 ('P:\Stealth Churn\1119891269.KeysChurnTest-0.05\traces\output-n1000-0.05-1.txt.gz.tab');
data3 = importtab2 ('P:\Stealth Churn\1119891269.KeysChurnTest-0.05\traces\output-n1000-0.05-2.txt.gz.tab');

f = figure();
set(f,'defaultaxesfontsize', 14);

hops = [data1.hops; data2.hops; data3.hops];
[y, x] = ecdf(hops);
plot(x, y, 'k-', 'LineWidth', 2);

hold on

hops = [norm1.hops; norm2.hops; norm3.hops];
[y, x] = ecdf(hops);
plot(x, y, 'k:', 'LineWidth', 2);

h_legend=legend('Stealth (95%)', 'Pastry');
h_legend=legend('Location', 'SouthEast');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')


xlabel('Number of hops per message', 'FontSize', 14);
ylabel('CDF', 'FontSize', 14);