global global_rep;

% MUST CHANGE ACCORDING TO SIMULATION!
filtername = 'Sim_Param2';
filter = 0.99;

xaxis = 'Host_Peer_Count';
% xaxis = 'Host_Wifi_Count';
% xaxis = 'Host_Wifi_Ratio';
% yaxis = 'DHT_Message_Count';
% yaxis = 'DHT_Message_Resent_Count';
 yaxis = 'DHT_GetMessage_E2ELatency_Avg';
% yaxis = 'DHT_GetMessage_Hops_Avg';
% yaxis = 'Net_UnreachablePacket_Count';
% yaxis = 'DHT_GetMessage_Stretch_Avg';

yaxis2 = 'DHT_GetReplyMessage_E2ELatency_Avg';

% yaxis2 = 'null';

if(strcmp(yaxis2,'null'))
    plot_start(0,strrep(xaxis,'_',' '),strrep(yaxis,'_',' '));
else
    plot_start(0,strrep(xaxis,'_',' '),[strrep(yaxis,'_',' ') ' + ' strrep(yaxis2,'_',' ')]);
end

lines = {movpas movswm stapas movswm};
captions = {'Moving Pastry', 'Moving Swarm', 'Static Pastry', 'Static Swarm'};

for i=1:length(lines)
	line = lines{i};
	global_rep = max(line.Sim_Seed) + 1;
	if(isfield(line, yaxis))
	    if(strcmp(yaxis2,'null'))
	        line.(yaxis2) = zeros(length(line.(yaxis)),1);
	    end
	    
	    x = find(line.(filtername) == filter);
	    plot_add(line.(xaxis)(x), line.(yaxis)(x) + line.(yaxis2)(x), captions{i});
	end
end



plot_end();