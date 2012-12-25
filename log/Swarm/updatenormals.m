s = importtab('P:\Swarm\KeysTest\Global.tab');
sc = importtab('P:\Swarm\KeysChurnTest\Global.tab');

n = importtab('P:\Swarm\KeysTestNormal\Global.tab');
nc = importtab('P:\Swarm\KeysChurnNormalTest\Global.tab');

n.Host_SwarmPeerRatio = s.Host_SwarmPeerRatio;
nc.Host_SwarmPeerRatio = sc.Host_SwarmPeerRatio;

exporttab('P:\Swarm\KeysTestNormal\Global.tab', n);
exporttab('P:\Swarm\KeysChurnNormalTest\Global.tab', nc);

exporttab('P:\Swarm\KeysTest\Global.tab', s);
exporttab('P:\Swarm\KeysChurnTest\Global.tab', sc);