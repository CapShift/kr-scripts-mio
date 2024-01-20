package com.projectkr.shell.utils;

public interface Constants {
    String cpu_dir = "/sys/devices/system/cpu/cpu0/";
    String cpufreq_sys_dir = "/sys/devices/system/cpu/cpu0/cpufreq/";
    String scaling_min_freq = cpufreq_sys_dir + "scaling_min_freq";
    String scaling_cur_freq = cpufreq_sys_dir + "cpuinfo_cur_freq";
    String scaling_max_freq = cpufreq_sys_dir + "scaling_max_freq";
    String scaling_governor = cpufreq_sys_dir + "scaling_governor";
    String scaling_available_governors = cpufreq_sys_dir + "scaling_available_governors";

}
