impfile = input('Filename?> ');
workload = input('Workload Name?> ');
rep = input('Repetitions?> ');
r1 = importtab(strcat(impfile,'-0.01.tab'));
r5 = importtab(strcat(impfile,'-0.05.tab'));
r10 = importtab(strcat(impfile,'-0.1.tab'));
r20 = importtab(strcat(impfile,'-0.2.tab'));
r30 = importtab(strcat(impfile,'-0.3.tab'));
r50 = importtab(strcat(impfile,'-0.5.tab'));
clear impfile;

f = figure('Visible','off','Position',[0 0 200 100]);
plotstealthhopcount;
saveas(f,strcat(workload,'-dhthops.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotstealthlatency;
saveas(f,strcat(workload,'-dhtlat.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotstealthnethopcount;
saveas(f,strcat(workload,'-nethops.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotstealthnetlatency;
saveas(f,strcat(workload,'-netlat.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotstealthmaxhopcount;
saveas(f,strcat(workload,'-dhthopsmax.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotstealthmaxlatency;
saveas(f,strcat(workload,'-dhtlatmax.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotstealthmaxnethopcount;
saveas(f,strcat(workload,'-nethopsmax.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotstealthmaxnetlatency;
saveas(f,strcat(workload,'-netlatmax.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotstealthstretch;
saveas(f,strcat(workload,'-stretch.png'),'png');
close(f);

f = figure('Visible','off','Position',[0 0 200 100]);
plotstealthfwds;
saveas(f,strcat(workload,'-fwdcount.png'),'png');
close(f);

clear f;