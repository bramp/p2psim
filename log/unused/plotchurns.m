norm = importtab ('P:\Churn\Normal ~unfinished\global.tab');
data = importtab ('P:\Churn\Stealth (fixed service nodes)\global.tab');

figure
hold on

count = [5,25,75,250];

x = find(data.Host_NormalPeer_Count == count(1));
plot(averagegroup(data.Host_StealthPeer_Count(x) ,3), averagegroup(data.DHT_MsgFwd_Count(x) ,3), 'k-','LineWidth', 2);

x = find(data.Host_NormalPeer_Count == count(2));
plot(averagegroup(data.Host_StealthPeer_Count(x) ,3), averagegroup(data.DHT_MsgFwd_Count(x) ,3), 'k-','LineWidth', 2);

x = find(data.Host_NormalPeer_Count == count(3));
plot(averagegroup(data.Host_StealthPeer_Count(x) ,3), averagegroup(data.DHT_MsgFwd_Count(x) ,3), 'k-','LineWidth', 2);

x = find(data.Host_NormalPeer_Count == count(4));
plot(averagegroup(data.Host_StealthPeer_Count(x) ,3), averagegroup(data.DHT_MsgFwd_Count(x) ,3), 'k-','LineWidth', 2);

x = find(data.Host_NormalPeer_Count == count(1));
plot(averagegroup(data.Host_StealthPeer_Count(x) ,3), averagegroup(data.DHT_MsgFwd_Count(x) ,3), 'k-','LineWidth', 2);

x = find(data.Host_NormalPeer_Count == count(2));
plot(averagegroup(data.Host_StealthPeer_Count(x) ,3), averagegroup(data.DHT_MsgFwd_Count(x) ,3), 'k-','LineWidth', 2);

x = find(data.Host_NormalPeer_Count == count(3));
plot(averagegroup(data.Host_StealthPeer_Count(x) ,3), averagegroup(data.DHT_MsgFwd_Count(x) ,3), 'k-','LineWidth', 2);

x = find(data.Host_NormalPeer_Count == count(4));
plot(averagegroup(data.Host_StealthPeer_Count(x) ,3), averagegroup(data.DHT_MsgFwd_Count(x) ,3), 'k-','LineWidth', 2);


clear impfile;

plotstealthresends;
