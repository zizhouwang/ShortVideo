#!/bin/bash
procnum=`ps -ef|grep "musical-ly/index"|grep -v grep|wc -l`
if [ $procnum -lt 1 ]; then
    ./yii musical-ly/index > null &
fi
