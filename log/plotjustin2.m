plot_start(1, 'Time', 'BestChoice');

times = [10000200 10481692 10963184 11444676 11926168 12407660 12889152 13370644 13852136 14333628 14815120 15296612 15778104 16259596 16741088 ];
RTBestChoice = [0.658005 0.480439 0.478905 0.472735 0.469951 0.488158 0.485178 0.493514 0.490926 0.484967 0.479219 0.471815 0.468565 0.473681 0.458543 ];
plot_add(times, RTBestChoice, 'Moving Swarm');

times = [10000200 10240200 10480200 10720200 10960200 11200200 11440200 11680200 11920200 12160200 12400200 12640200 12880200 13120200 13360200 ];
RTBestChoice = [0.711793 0.711793 0.711793 0.711793 0.711793 0.711793 0.711793 0.711793 0.711793 0.711793 0.711793 0.711793 0.711793 0.711793 0.711793 ];
plot_add(times, RTBestChoice, 'Static Swarm');

times = [10000200 10499813 10999426 11499039 11998653 12498266 12997879 13497492 13997106 14496719 14996332 15495945 15995559 16495172 16994785 ];
RTBestChoice = [0.900431 0.502392 0.484744 0.498285 0.505176 0.494297 0.492251 0.494808 0.507825 0.513784 0.493168 0.498555 0.494101 0.482363 0.492205 ];
plot_add(times, RTBestChoice, 'Moving Pastry');

times = [10000200 10240200 10480200 10720200 10960200 11200200 11440200 11680200 11920200 12160200 12400200 12640200 12880200 13120200 13360200 ];
RTBestChoice = [1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 ];
plot_add(times, RTBestChoice, 'Static Pastry');

plot_save('1 - BestChoice', 'png');


plot_start(1, 'Time', 'Difference In Known Delay');

times = [10000200 10481692 10963184 11444676 11926168 12407660 12889152 13370644 13852136 14333628 14815120 15296612 15778104 16259596 16741088 ];
RTDiff = [-4.082671 -47.47415 -57.272886 -54.234667 -54.122664 -55.856391 -55.713907 -60.114907 -61.354501 -58.108948 -54.416168 -54.494899 -52.615584 -55.302254 -50.840822 ];
plot_add(times, RTDiff, 'Moving Swarm');

times = [10000200 10240200 10480200 10720200 10960200 11200200 11440200 11680200 11920200 12160200 12400200 12640200 12880200 13120200 13360200 ];
RTDiff = [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ];
plot_add(times, RTDiff, 'Static Swarm');

times = [10000200 10499813 10999426 11499039 11998653 12498266 12997879 13497492 13997106 14496719 14996332 15495945 15995559 16495172 16994785 ];
RTDiff = [-15.12086 -196.908615 -193.066201 -201.080104 -201.176095 -195.386139 -198.032803 -195.977127 -198.306146 -199.409077 -192.321977 -194.890688 -192.918377 -192.770662 -194.948113 ];
plot_add(times, RTDiff, 'Moving Pastry');

times = [10000200 10240200 10480200 10720200 10960200 11200200 11440200 11680200 11920200 12160200 12400200 12640200 12880200 13120200 13360200 ];
RTDiff = [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ];
plot_add(times, RTDiff, 'Static Pastry');

plot_save('1 - Diff', 'png');

plot_start(1, 'Time', 'Difference In Known Delay (Absolute)');

times = [10000200 10481692 10963184 11444676 11926168 12407660 12889152 13370644 13852136 14333628 14815120 15296612 15778104 16259596 16741088 ];
RTDiffAbs = [8.476009 101.571712 113.713091 113.049293 113.419001 113.333895 113.305143 116.723087 116.790502 116.578987 113.339453 113.109158 112.423692 113.788606 112.53601 ];
plot_add(times, RTDiffAbs, 'Moving Swarm');

times = [10000200 10240200 10480200 10720200 10960200 11200200 11440200 11680200 11920200 12160200 12400200 12640200 12880200 13120200 13360200 ];
RTDiffAbs = [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ];
plot_add(times, RTDiffAbs, 'Static Swarm');

times = [10000200 10499813 10999426 11499039 11998653 12498266 12997879 13497492 13997106 14496719 14996332 15495945 15995559 16495172 16994785 ];
RTDiffAbs = [26.051926 264.885866 266.255778 273.527865 271.889083 271.005628 274.486744 266.703103 267.092578 266.88196 262.931921 264.40065 263.51237 267.1987 266.772287 ];
plot_add(times, RTDiffAbs, 'Moving Pastry');

times = [10000200 10240200 10480200 10720200 10960200 11200200 11440200 11680200 11920200 12160200 12400200 12640200 12880200 13120200 13360200 ];
RTDiffAbs = [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ];
plot_add(times, RTDiffAbs, 'Static Pastry');

plot_save('1 - DiffAbs', 'png');