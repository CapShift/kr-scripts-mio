#!/system/bin/sh
state=$1

if [ $state = '1' ]; then
    pm enable com.google.android.gsf
    pm enable com.google.android.gsf.login
    pm enable com.google.android.gms
    pm enable com.android.vending
    pm enable com.google.android.play.games
else
    pm disable com.google.android.gsf
    pm disable com.google.android.gsf.login
    pm disable com.google.android.gms
    pm disable com.android.vending
    pm disable com.google.android.play.games
fi;
