figure
hold on
grid on
hist(delays)
xlabel('DHT End to End Latency (ms)')
ylabel('Frequency')
title('DHT End to End Latency Distribution')
hold off

figure
hold on
grid on
cdfplot(delays)
xlabel('DHT End to End Latency (ms)')
ylabel('CDF')
title('DHT End to End Latency Distribution')
hold off

figure
hold on
grid on
hist(hops)
xlabel('DHT Hop Count)')
ylabel('Frequency')
title('DHT Hop Distribution')
hold off

figure
hold on
grid on
cdfplot(hops)
xlabel('DHT Hop Count')
ylabel('CDF')
title('DHT Hop Distribution')
hold off

figure
hold on
grid on
hist(nodefwds)
xlabel('Forwarded Message Count')
ylabel('Frequency')
title('Forwarded Message Distribution')
hold off

figure
hold on
grid on
cdfplot(nodefwds)
xlabel('Forwarded Message Count')
ylabel('CDF')
title('Forwarded Message Distribution')
hold off

figure
hold on
grid on
hist(noderecvs)
xlabel('Received Message Count')
ylabel('Frequency')
title('Received Message Distribution')
hold off

figure
hold on
grid on
cdfplot(noderecvs)
xlabel('Received Message Count')
ylabel('CDF')
title('Received Message Distribution')
hold off