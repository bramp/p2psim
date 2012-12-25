rep = input('How many times were the simulations run? > ')

figure
hold on
grid on
plot(averagegroup(Host_Failure_Count,rep),(averagegroup(DHT_MsgResent_Count,rep)./(averagegroup(DHT_MsgSent_Count,rep)+averagegroup(DHT_MsgFwd_Count,rep))),'b-')
xlabel('Node Failure Count')
ylabel('Resent Messages / Total Messages Sent')
title('Node Failure Count with Resent Messages / Total Messages Sent')
hold off