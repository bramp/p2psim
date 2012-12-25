function x = importtab2(file)

% imports data from a tab file with a text header row
a = importdata(file,'\t');

x = struct();

% check we loaded the file
if (isstruct(a))
	% insert the variables
	for i=1:length(a.colheaders)
			d = size(a.data);
	    data = a.data(1:d(1),i);
	    data(find([isnan(data)]))=[];
	    x.(a.colheaders{i}) = data;
	end 
	
end