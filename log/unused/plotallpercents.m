impfile = input('Filename?> ');
workload = input('Workload Name?> ');
rep = input('Repetitions?> ');
r10 = importtab(strcat(impfile,'-0.10.tab'));
r20 = importtab(strcat(impfile,'-0.20.tab'));
r30 = importtab(strcat(impfile,'-0.30.tab'));
r40 = importtab(strcat(impfile,'-0.40.tab'));
r50 = importtab(strcat(impfile,'-0.50.tab'));
r60 = importtab(strcat(impfile,'-0.60.tab'));
r70 = importtab(strcat(impfile,'-0.70.tab'));
r80 = importtab(strcat(impfile,'-0.80.tab'));
r90 = importtab(strcat(impfile,'-0.90.tab'));
clear impfile;

f = figure('Visible','off','Position',[0 0 200 100]);
plothopcount;
saveas(f,strcat(workload,'-dhthops.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotlatency;
saveas(f,strcat(workload,'-dhtlat.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotnethopcount;
saveas(f,strcat(workload,'-nethops.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotnetlatency;
saveas(f,strcat(workload,'-netlat.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotmaxhopcount;
saveas(f,strcat(workload,'-dhthopsmax.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotmaxlatency;
saveas(f,strcat(workload,'-dhtlatmax.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotmaxnethopcount;
saveas(f,strcat(workload,'-nethopsmax.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotmaxnetlatency;
saveas(f,strcat(workload,'-netlatmax.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotstretch;
saveas(f,strcat(workload,'-stretch.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotfwds;
saveas(f,strcat(workload,'-fwdcount.png'),'png');
close(f);

clear f;