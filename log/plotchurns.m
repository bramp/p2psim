global global_rep;

% MUST CHANGE ACCORDING TO SIMULATION!
filtername = 'Sim_Param2';
filter = [0.05];

xaxis = 'Sim_Param1';
%yaxis = 'DHT_RouteFailed_Count';
%yaxis = 'DHT_GetMessage_Hops_Avg';
yaxis = 'Lookup_Latency';
%yaxis = 'DHT_GetMessage_Resent_Count';
%yaxis = 'Get_Failures';
%yaxis = 'DHT_GetMessage_Stretch_Avg';
plot_start(0,'Mean inter-arrival time (s)',strrep(yaxis,'_',' '));

lines = {serv stea both};
captions = {'Service','Stealth','Both'};

for i=1:length(lines)
	line = lines{i};
	global_rep = max(line.Sim_Seed) - min(line.Sim_Seed) + 1;
	if(isfield(line, yaxis))	    
        for j=1:length(filter)
            x = find(line.(filtername) == filter(j));
            plot_add(line.(xaxis)(x), line.(yaxis)(x), captions{i});
        end
	end
end

plot_end();