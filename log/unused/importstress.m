function x = importstress(path)
files = dir([path '*.tab']);

x = struct();
x.Nodes = [];
x.Stress = [];
x.StressMax = [];
x.TotalPackets = [];

for i = 1:length(files)
	file = [path files(i).name];
	data = importtab2(file);
		
	if(isfield(data,'nodesents'))
			x.Nodes = [x.Nodes length(data.nodesents)];
			%x.Stress = [x.Stress (length(data.link_dup) + sum(data.link_dup)) / length(data.link_dup)];
			%x.Stress2 = [x.Stress2 (length(data.link_dup) + sum(data.link_dup) - sum(data.resents)) / length(data.link_dup)];
			%x.StressMax = [x.StressMax max(data.link_dup)];
			
			%a = data.linkcount_uni / sum(data.linkcount_uni);
			a = data.linkcount_uni;
			
			x.Stress = [x.Stress (mean(a))];
			x.StressMax = [x.StressMax max(a)];
			x.TotalPackets = [x.TotalPackets sum(a)];
			
			clear a;
	end
	
	clear data;

end