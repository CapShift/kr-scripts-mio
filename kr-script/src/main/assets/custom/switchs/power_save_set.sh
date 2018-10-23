#!/system/bin/sh
state=$1
settings put global low_power $1;
settings put global low_power_sticky $1;

# Whether or not app auto restriction is enabled. When it is enabled, settings app will  auto restrict the app if it has bad behavior(e.g. hold wakelock for long time).
# [app_auto_restriction_enabled]

#Whether or not to enable Forced App Standby on small battery devices.         * Type: int (0 for false, 1 for true)
# forced_app_standby_for_small_battery_enabled

# Feature flag to enable or disable the Forced App Standby feature.         * Type: int (0 for false, 1 for true)
# forced_app_standby_enabled

# Whether or not to enable the User Absent, Radios Off feature on small battery devices.         * Type: int (0 for false, 1 for true)
# user_absent_radios_off_for_small_battery_enabled

echo '充电状态下可能无法使用省电模式'
echo '-'

if [[ $state = "1" ]]
then
    echo "开启应用自动限制 可能需要Android Pie"
    settings put global app_auto_restriction_enabled true

    echo "开启应用强制standby"
    settings put global forced_app_standby_enabled 1

    echo "开启应用standby"
    settings put global app_standby_enabled 1

    echo "开启小容量电池设备应用强制standby"
    settings put global forced_app_standby_for_small_battery_enabled 1

    ai=`settings get system ai_preload_user_state`
    if [[ ! "$ai" = "null" ]]
    then
      echo "关闭MIUI10的ai预加载"
      settings put system ai_preload_user_state 0
    fi

    echo "开启安卓原生的省电模式"
    settings put global low_power 1
    settings put global low_power_sticky 1

    echo "关闭调试服务和日志进程"
    stop woodpeckerd 2> /dev/null
    stop debuggerd 2> /dev/null
    stop debuggerd64 2> /dev/null
    stop atfwd 2> /dev/null
    stop perfd 2> /dev/null
    stop logd 2> /dev/null
    if [[ -e /sys/zte_power_debug/switch ]]; then
        echo 0 > /sys/zte_power_debug/switch
    fi
    if [[ -e /sys/zte_power_debug/debug_enabled ]]; then
        echo N > /sys/kernel/debug/debug_enabled
    fi
    stop cnss_diag 2> /dev/null
    killall -9 cnss_diag 2> /dev/null
    stop subsystem_ramdump 2> /dev/null
    #stop thermal-engine 2> /dev/null
    stop tcpdump 2> /dev/null
    stop logd 2> /dev/null
    stop adbd 2> /dev/null
    #killall -9 magiskd 2> /dev/null
    killall -9 magisklogd 2> /dev/null

    echo "清理后台休眠白名单"
    for item in `dumpsys deviceidle whitelist`
    do
        app=`echo "$item" | cut -f2 -d ','`
        #echo "deviceidle whitelist -$app"
        dumpsys deviceidle whitelist -$app
        am set-inactive $app true 2> /dev/null
    done
    dumpsys deviceidle step
    dumpsys deviceidle step
    dumpsys deviceidle step
    dumpsys deviceidle step

    #echo '关闭GPS定位'
    # Android M or M+
    #settings put secure location_providers_allowed -gps

    # Android M-
    #settings put secure location_providers_allowed network

    level=`settings get global low_power_trigger_level`
    maxlevel=`settings get global low_power_trigger_level_max`
    if [[ "$level" = "null" ]]
    then
        echo ''
        #echo '进入省电模式的电流级别当前设为null，将不会自动进入省电模式'
    fi

    echo '注意：开启省电模式后，Scene可能会无法保持后台'
    echo '并且，可能会收不到后台消息推送！'
    echo ''
else
    echo "关闭应用强制standby"
    settings put global forced_app_standby_enabled 0

    echo "关闭安卓原生的省电模式"
    settings put global low_power 0
    settings put global low_power_sticky 0
fi

echo '状态已切换，部分深度定制的系统此操作可能无效！'
echo '-'


