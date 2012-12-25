function x = importtab(file)
% imports data from a tab file with a text header row
a = importdata(file,'\t');
x = struct();

start = 1;

%find when results start
for i=1:length(a.colheaders)
    if (strcmp(a.colheaders{i}, 'Sim_SimDuration') == 1)
    		c = find (a.data(:, i) > 0); 
    		start = c(1,:);
    end
end

% insert the variables
for i=1:length(a.colheaders)

    % remove illegal chars from a.colheaders{i}
    a.colheaders{i} = strrep(a.colheaders{i}, '.', '_');
    a.colheaders{i} = strrep(a.colheaders{i}, '(', '_');
    a.colheaders{i} = strrep(a.colheaders{i}, ')', '_');

		% Remove some results we don't want
		if (isempty( [findstr(a.colheaders{i}, 'RTDiff') findstr(a.colheaders{i}, 'RTBest')] ) )
			d = size(a.data);
	    data = a.data(start:d(1),i);
	    data(find([isnan(data)]))=[];
	    x.(a.colheaders{i}) = data;
	  end
end 

% Now add the Host_Peer_Count
if(~isfield(x,'Host_Peer_Count') & isfield(x,'Host_ServicePeer_Count') & isfield(x,'Host_StealthPeer_Count'))
		x.Host_Peer_Count = x.Host_ServicePeer_Count + x.Host_StealthPeer_Count;
end

if(~isfield(x,'Host_Peer_Count') & isfield(x,'Host_SwarmPeer_Count'))
		x.Host_Peer_Count = x.Host_SwarmPeer_Count;
end

if(isfield(x,'Auth_Cache_OK_Count') & isfield(x,'Auth_WAIT_Count') & isfield(x,'Auth_Cache_OK_Count'))
    x.Auth_Hit_Ratio = x.Auth_Cache_OK_Count ./ (x.Auth_WAIT_Count + x.Auth_Cache_OK_Count);
end

if (isfield(x,'DHT_GetMessage_E2ELatency_Avg') & isfield(x,'DHT_GetReplyMessage_E2ELatency_Avg'))
    x.Lookup_Latency = x.DHT_GetMessage_E2ELatency_Avg + x.DHT_GetReplyMessage_E2ELatency_Avg;
end

if (isfield(x,'DHT_JoinMessage_E2ELatency_Avg') & isfield(x,'DHT_JoinFinishedMessage_E2ELatency_Avg'))
    if(isfield(x,'Net_AuthReplyPacket_E2ELatency_Avg') & isfield(x,'Net_AuthMePacket_E2ELatency_Avg'))
        x.Join_Latency = x.DHT_JoinMessage_E2ELatency_Avg + x.DHT_JoinFinishedMessage_E2ELatency_Avg + x.Net_AuthMePacket_E2ELatency_Avg + x.Net_AuthReplyPacket_E2ELatency_Avg;
    else
        x.Join_Latency = x.DHT_JoinMessage_E2ELatency_Avg + x.DHT_JoinFinishedMessage_E2ELatency_Avg;
    end
end

if (isfield(x,'DHT_GetMessage_Count') & isfield(x,'DHT_NullGet_Count'))
    x.Get_Failures = x.DHT_GetMessage_Count - x.DHT_NullGet_Count;
end

