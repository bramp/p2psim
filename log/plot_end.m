function plot_end()
% ends a generic plot

% shared variables
global global_leg;
global global_fontsize;

% draw and place the legend
h_legend=legend(global_leg);
h_legend=legend('Location', 'Best');
h_legend=legend('boxoff');

h_text = findobj(h_legend,'type','text');
set(h_text,'FontUnits','points','FontSize',global_fontsize);
set(h_text,'FontUnits','normal');

% clean up all these daft global things (bar the figure handle in case we
% want to save)
hold off;
clear global global_leg global_rep global_legcount global_style global_linewidth global_fontsize;