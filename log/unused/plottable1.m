nodes = [5 10 25 50 100 150 200 250];
theor = [0.5805 0.8305 1.1610 1.4110 1.6610 1.8072 1.9110 1.9914];
pastsim =[0.7986 0.8982 1.2115 1.4981 1.6932 1.7908 1.9106 2.0066];
stealthsim =[0.9656 0.9753 1.2533 1.5315 1.7031 1.8112 1.9161 2.0188];
var = [0.0433 0.0275 0.0278 0.0192 0.0165 0.0171 0.0136 0.0191];

f = figure('Position', [200 200 658 420]);

set(f,'defaultaxesfontsize', 14);

width = 2;
rep = 3;

plot(nodes,theor,'k:','LineWidth', width);
hold on
plot(nodes,pastsim,'k--','LineWidth', width);
errorbar(nodes,stealthsim,var,'k-','LineWidth', width);

xlabel('Service Nodes', 'FontSize', 14);
ylabel('Average DHT Hops', 'FontSize', 14);

h_legend=legend('Theoretical','Pastry','Stealth');
h_legend=legend('Location', 'NorthWest');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',14)
set(h_text,'FontUnits','normal')

hold off
clear count styles width h_legend h_text f