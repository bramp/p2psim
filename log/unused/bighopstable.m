%a = [5 10 15 25 50 75 100 125 150 175 200 225 250];
a = [5 10 25 50 75 100 250];

x = importtab('P:\Stealth KeysTest2i (fixed service nodes)\Global.tab');
HopsNoReply = ((x.DHT_Hops_Avg .* x.DHT_MsgRecv_Count)-x.DHT_GetReplyMessage_Count)./(x.DHT_MsgRecv_Count-x.DHT_GetReplyMessage_Count);
ans = [];
for i = 1:length(a)
	y = find (x.Host_NormalPeer_Count == a(i));
	ans = [ans; a(i) mean(logb(a(i), 16)) mean(HopsNoReply(y)) std(HopsNoReply(y)) ];
end

ans

x = importtab('P:\Churn\Stealth (fixed service nodes)\Global.tab');
HopsNoReply = ((x.DHT_Hops_Avg .* x.DHT_MsgRecv_Count)-x.DHT_GetReplyMessage_Count)./(x.DHT_MsgRecv_Count-x.DHT_GetReplyMessage_Count);
ans = [];
for i = 1:length(a)
	y = find (x.Host_NormalPeer_Count == a(i));
	ans = [ans; a(i) mean(logb(a(i), 16)) mean(HopsNoReply(y)) std(HopsNoReply(y)) ];
end

ans
