function exporttab(filename, x)
% imports data from a tab file with a text header row

colheaders = fieldnames(x);

fid = fopen(filename, 'w');

for i=1:length(colheaders)
	fwrite(fid, char(colheaders(i)));
	
	if (i < length(colheaders))
		fprintf(fid, '\t');
	end
end
for row=1:length(x.Sim_SimDuration)

	fprintf(fid, '\n');

	for i=1:length(colheaders)
		data = x.(char(colheaders(i)));
		
		fprintf(fid, '%.10g', data(row));
		
		if (i < length(colheaders))
			fprintf(fid, '\t');
		end
	end
end

fclose(fid);

% insert the variables
%for i=1:length(a.colheaders)
%		d = size(a.data);
%    data = a.data(start:d(1),i);
%    data(find([isnan(data)]))=[];
%    x.(a.colheaders{i}) = data;
%end 