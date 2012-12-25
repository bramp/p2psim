function x = importtab(file)
% imports data from a tab file with a text header row
a = importdata(file,'\t');
x = struct();

start = 1;

colheaders = a.colheaders;

%find when results start
for i=1:length(colheaders)
    if (strcmp(colheaders{i}, 'Sim_SimDuration') == 1)
    		c = find (a.data(:, i) > 0); 
    		start = c(1,:);
    end
end 

% insert the variables
for i=1:length(a.colheaders)
		d = size(a.data);
    data = a.data(start:d(1),i);
    data(find([isnan(data)]))=[];
    x.(a.colheaders{i}) = data;
end 

if(~isfield(x,'DHT_MsgFwd_Count'))
		x.DHT_MsgFwd_Count = x.Host_Peer_Count * 0;
end

if(~isfield(x,'Host_Peer_Count'))

	x.Host_Peer_Count = zeros(size(x.Sim_SimDuration));

	if(isfield(x,'Host_SwarmPeer0_1_Count'))
		x.Host_Peer_Count = x.Host_Peer_Count + x.Host_SwarmPeer0_1_Count;
	end

	if(isfield(x,'Host_SwarmPeer0_9_Count'))
		x.Host_Peer_Count = x.Host_Peer_Count + x.Host_SwarmPeer0_9_Count;
	end

	if(isfield(x,'Host_SwarmPeer1_0_Count'))
		x.Host_Peer_Count = x.Host_Peer_Count + x.Host_SwarmPeer1_0_Count;
	end
	
end

%Add the %percentage field
if( isfield(x,'Host_SwarmPeer0_1_Count') & isfield(x,'Host_SwarmPeer0_9_Count'))
	ratios = [0.95 0.90 0.75 0.50 0.25 0.10 0.05];
	x.Host_SwarmPeerRatio = x.Host_SwarmPeer0_1_Count ./ x.Host_SwarmPeer_Count;
	
	%Clean this field up by rounding to the nearest value
	for i=1:length(x.Host_SwarmPeerRatio)
		b = abs(x.Host_SwarmPeerRatio(i) - ratios);
		x.Host_SwarmPeerRatio(i) = ratios(find(b == min(b)));
	end
end

%if(~isfield(x,'DHT_Stretch'))	
%	x.DHT_Stretch = (x.DHT_E2ELatencyTotal ./ x.DHT_E2ELatencyUnicastTotal);
%end

% undefined stretch should be 1 (may occur if a sim is zero length)
%x.DHT_Stretch(find(x.DHT_E2ELatencyUnicastTotal < 1)) = 1;