s = importtab('P:\old_logs\Justin\Swarm~\KeysTest\Global.tab');
n = importtab('P:\old_logs\Justin\Swarm~\KeysTestNormal\Global.tab');

plot_e2edelay_get
saveas(gcf, 'plot_e2edelay_get', 'jpg')
close(gcf);

plot_e2edelay_getreply
saveas(gcf, 'plot_e2edelay_getreply', 'jpg')
close(gcf);

plot_e2edelay_getreply_improve
saveas(gcf, 'plot_e2edelay_getreply_improve', 'jpg')
close(gcf);

plot_hops_get
saveas(gcf, 'plot_hops_get', 'jpg')
close(gcf);

plot_hops_getreply
saveas(gcf, 'plot_hops_getreply', 'jpg')
close(gcf);

plot_stretch_get
saveas(gcf, 'plot_stretch_get', 'jpg')
close(gcf);

plot_stretch_getreply
saveas(gcf, 'plot_stretch_getreply', 'jpg')
close(gcf);

s = importtab('P:\old_logs\Justin\Swarm~\KeysChurnTest\Global.tab');
n = importtab('P:\old_logs\Justin\Swarm~\KeysChurnNormalTest\Global.tab');

plot_e2edelay_get
saveas(gcf, 'plot_e2edelay_get_churn', 'jpg')
close(gcf);

plot_e2edelay_getreply
saveas(gcf, 'plot_e2edelay_getreply_churn', 'jpg')
close(gcf);

plot_e2edelay_getreply_improve
saveas(gcf, 'plot_e2edelay_getreply_improve_churn', 'jpg')
close(gcf);

plot_hops_get
saveas(gcf, 'plot_hops_get_churn', 'jpg')
close(gcf);

plot_hops_getreply
saveas(gcf, 'plot_hops_getreply_churn', 'jpg')
close(gcf);

plot_stretch_get
saveas(gcf, 'plot_stretch_get_churn', 'jpg')
close(gcf);

plot_stretch_getreply
saveas(gcf, 'plot_stretch_getreply_churn', 'jpg')
close(gcf);