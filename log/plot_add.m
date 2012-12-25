function plot_add(fieldX,fieldY,leg)
% adds a set of data to the existing generic plot

% shared variables
global global_rep;
global global_leg;
global global_legcount;
global global_style;
global global_linewidth;

% record the legend string for this data
global_leg(global_legcount + 1) = {leg};

[M,N] = size(fieldX);
if (M == 1)
	fieldX = rot90(fieldX);
end

[M,N] = size(fieldY);
if (M == 1)
	fieldY = rot90(fieldY);
end

% sort and average the data passed in
sorted = [fieldX fieldY];
sorted = sortrows(sorted);
xsort = averagegroup(sorted(:,1),global_rep);
ysort = averagegroup(sorted(:,2),global_rep);

% cycle through available line styles when plotting
nextstyle = global_style(mod(global_legcount,length(global_style(:,1))) + 1,:);
plot(xsort,ysort,nextstyle,'LineWidth', global_linewidth);
global_legcount = global_legcount + 1;