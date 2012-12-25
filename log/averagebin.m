function ret = averagebin(xy, limit)
% Averages values by grouping them into limit sized bins

xy = sort(xy);
ret = [];

for x1 = min(xy(:,1)):limit:max(xy(:,1))
	x2 = x1 + limit;
	
	set = (xy(:,1) >= x1) .* (xy(:,1) < x2);
	
	if (sum(set) > 0)
		ret = [ret; x1 mean(xy(find(set),2))];
	end
	
end

