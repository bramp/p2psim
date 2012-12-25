java -Xms300m -Xmx1024m -classpath ./bin sim.main.Simulator sim.workload.puredht.lifetime.ConstMsgGlobalLifetimeTest 1
java -Xms300m -Xmx1024m -classpath ./bin sim.main.Simulator sim.workload.puredht.lifetime.ConstMsgPerPeer50LifetimeTest 1
java -Xms300m -Xmx1024m -classpath ./bin sim.main.Simulator sim.workload.puredht.lifetime.ConstMsgPerPeer100LifetimeTest 1
java -Xms300m -Xmx1024m -classpath ./bin sim.main.Simulator sim.workload.puredht.lifetime.KeysReplicaLifetimeTest 1
java -Xms300m -Xmx1024m -classpath ./bin sim.main.Simulator sim.workload.puredht.lifetime.KeysLifetimeTest 1

for %%a IN (0.01,0.05,0.10,0.20,0.30,0.50) DO java -Xms300m -Xmx1024m -classpath ./bin sim.main.Simulator sim.workload.stealth.ConstMsgGlobalLifetimeTest %%a
for %%a IN (0.01,0.05,0.10,0.20,0.30,0.50) DO java -Xms300m -Xmx1024m -classpath ./bin sim.main.Simulator sim.workload.stealth.ConstMsgPerPeer50LifetimeTest %%a
for %%a IN (0.01,0.05,0.10,0.20,0.30,0.50) DO java -Xms300m -Xmx1024m -classpath ./bin sim.main.Simulator sim.workload.stealth.ConstMsgPerPeer100LifetimeTest %%a
for %%a IN (0.01,0.05,0.10,0.20,0.30,0.50) DO java -Xms300m -Xmx1024m -classpath ./bin sim.main.Simulator sim.workload.stealth.KeysReplicaLifetimeTest %%a
for %%a IN (0.01,0.05,0.10,0.20,0.30,0.50) DO java -Xms300m -Xmx1024m -classpath ./bin sim.main.Simulator sim.workload.stealth.KeysLifetimeTest %%a