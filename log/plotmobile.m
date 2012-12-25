global global_rep;

% MUST CHANGE ACCORDING TO SIMULATION!
filtername = 'Sim_Param3';
filter = 10;

harpas.Host_Wifi_Ratio = harpas.Host_Wifi_Count ./ harpas.Host_Peer_Count;
harste.Host_Wifi_Ratio = harste.Host_Wifi_Count ./ harste.Host_Peer_Count;
movpas.Host_Wifi_Ratio = movpas.Host_Wifi_Count ./ movpas.Host_Peer_Count;
movste.Host_Wifi_Ratio = movste.Host_Wifi_Count ./ movste.Host_Peer_Count;
stapas.Host_Wifi_Ratio = stapas.Host_Wifi_Count ./ stapas.Host_Peer_Count;
staste.Host_Wifi_Ratio = staste.Host_Wifi_Count ./ staste.Host_Peer_Count;

% xaxis = 'Host_Peer_Count';
% xaxis = 'Host_Wifi_Count';
xaxis = 'Host_Wifi_Ratio';
% yaxis = 'DHT_Message_Count';
% yaxis = 'DHT_Message_Resent_Count';
% yaxis = 'DHT_GetMessage_E2ELatency_Avg'
% yaxis = 'DHT_GetMessage_Hops_Avg';
yaxis = 'Net_UnreachablePacket_Count';
% yaxis2 = 'DHT_GetReplyMessage_E2ELatency_Avg';
yaxis2 = 'null';

if(strcmp(yaxis2,'null'))
    plot_start(0,strrep(xaxis,'_',' '),strrep(yaxis,'_',' '));
else
    plot_start(0,strrep(xaxis,'_',' '),[strrep(yaxis,'_',' ') ' + ' strrep(yaxis2,'_',' ')]);
end

lines = {harste movste staste harpas movpas stapas};
captions = {'Hard Stealth', 'Moving Stealth', 'Static Stealth', 'Hard Pastry', 'Moving Pastry', 'Static Pastry'};

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