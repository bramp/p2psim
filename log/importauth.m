lines = { 
 importtab('P:\old_logs\Auth 3\auth-sim.workload.authentication.PastryAuthTest\Join.tab');
 importtab('P:\old_logs\Auth 3\auth-sim.workload.authentication.PastryAuthTest\Gets.tab');
 importtab('P:\old_logs\Auth 3\auth-sim.workload.authentication.PastryAuthTest\Global.tab');

 importtab('P:\old_logs\Auth 3\auth-sim.workload.authentication.PastryTest\Join.tab');
 importtab('P:\old_logs\Auth 3\auth-sim.workload.authentication.PastryTest\Gets.tab');
 importtab('P:\old_logs\Auth 3\auth-sim.workload.authentication.PastryTest\Global.tab');
 
 };

glob = lines{3};

for i=1:length(lines)

    if(~isfield(lines{i},'Sim_Seed') || ~isfield(lines{i},'Sim_Param2'))
        %This needs copying since gets doesn't have it!
        lines{i}.Sim_Seed = glob.Sim_Seed;
        lines{i}.Sim_Param0 = glob.Sim_Param0;
        lines{i}.Sim_Param1 = glob.Sim_Param1;
        lines{i}.Sim_Param2 = glob.Sim_Param2;
        lines{i}.Host_Peer_Count = glob.Host_Peer_Count;
    end

end

join = lines{1};
gets = lines{2};
glob = lines{3};

pjoi = lines{4};
pget = lines{5};
pglo = lines{6};

clear lines;