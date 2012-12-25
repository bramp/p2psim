function plot_start(rep,labelX,labelY)
% begins a generic plot

% shared variables
global global_rep;
global global_legcount;
global global_leg;
global global_style;
global global_fontsize;
global global_linewidth;
global global_fig;

% PLOT APPEARANCE - alter to taste
global_fontsize = 14;
global_linewidth = 2;
global_style = char('r-','g--','b:','k:','k:+','k:o');
%global_style = char('k-','k--','k:','k-.','k-+','k-o','k-*');
%global_style = char('r-','r--','r:','g-','g--','g:');
plotsize = [200 200 658 420];

% number of repetitions to average across
global_rep = rep;

% initialise legend variables
global_legcount = 0;
global_leg = cell(0);

% draw graph
global_fig = figure('Position', plotsize);

set(global_fig,'defaultaxesfontsize', global_fontsize);
xlabel(labelX, 'FontSize', global_fontsize);
ylabel(labelY, 'FontSize', global_fontsize);

box on

hold on
