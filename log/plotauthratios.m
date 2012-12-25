global global_rep;

% MUST CHANGE ACCORDING TO SIMULATION!
filtername = 'Sim_Param2';
filter = [1];

xaxis = 'Host_Peer_Count';
%yaxis = 'DHT_Message_Stretch_Avg';
%yaxis = 'Auth_Cache_OK_Count';
yaxis = 'Lookup_Latency';
%yaxis = 'Auth_Hit_Ratio';
%yaxis = 'Join_Latency';

plot_start(0,strrep(xaxis,'_',' '),strrep(yaxis,'_',' '));

lines = {gets pget};
captions = {'With (Gets)','Without (Gets)'};

for i=1:length(lines)
	line = lines{i};
	global_rep = max(line.Sim_Seed) - min(line.Sim_Seed) + 1;
	if(isfield(line, yaxis))	    
        for j=1:length(filter)
            x = find(line.(filtername) == filter(j));
            caption = [captions{i} ' ' num2str(filter(j)*100) '%'];
            plot_add(line.(xaxis)(x), line.(yaxis)(x), caption);
        end
	end
end

plot_end();