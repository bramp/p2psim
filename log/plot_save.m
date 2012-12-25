function plot_save(filename, filetype)
% automatically ends and saves the current plot
global global_fig;
plot_end();

if nargin < 2, filetype = 'eps'; end
saveas(global_fig, [filename '.' filetype], filetype);
close;
clear global global_fig;