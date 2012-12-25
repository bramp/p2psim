function [norm1 norm2 norm3 data1 data2 data3] = normvsstealth()
	norm1 = importtab2 ('P:\Reruns\Without Churn\1120590070.puredht.KeysTest-0.05\traces\output-n1000-0.05-0.txt.gz.tab');
	norm2 = importtab2 ('P:\Reruns\Without Churn\1120590070.puredht.KeysTest-0.05\traces\output-n1000-0.05-1.txt.gz.tab');
	norm3 = importtab2 ('P:\Reruns\Without Churn\1120590070.puredht.KeysTest-0.05\traces\output-n1000-0.05-2.txt.gz.tab');
	data1 = importtab2 ('P:\Reruns\Without Churn\1120590070.stealth.KeysTest-0.05\traces\output-n1000-0.05-0.txt.gz.tab');
	data2 = importtab2 ('P:\Reruns\Without Churn\1120590070.stealth.KeysTest-0.05\traces\output-n1000-0.05-1.txt.gz.tab');
	data3 = importtab2 ('P:\Reruns\Without Churn\1120590070.stealth.KeysTest-0.05\traces\output-n1000-0.05-2.txt.gz.tab');