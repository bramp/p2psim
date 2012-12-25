function x = averagegroup(grp,rep)
% averages out groups of values within an array into single values in a new
% array
for i = 1:rep:length(grp)
    if(i+(rep-1) <= length(grp))
        x( ( (i-1) /rep) + 1 ) = sum( grp(i:i+(rep-1)))/rep;
    end
end

