impfile = input('Filename?> ');
workload = input('Workload Name?> ');
rep = input('Repetitions?> ');
p10 = importtab(strcat(impfile,'-0.10.tab'));
p20 = importtab(strcat(impfile,'-0.20.tab'));
p30 = importtab(strcat(impfile,'-0.30.tab'));
p40 = importtab(strcat(impfile,'-0.40.tab'));
p50 = importtab(strcat(impfile,'-0.50.tab'));
p60 = importtab(strcat(impfile,'-0.60.tab'));
p70 = importtab(strcat(impfile,'-0.70.tab'));
p80 = importtab(strcat(impfile,'-0.80.tab'));
p90 = importtab(strcat(impfile,'-0.90.tab'));
clear impfile;