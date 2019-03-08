#!/bin/sh
#if [ $# -lt 1 ]
#then
#  echo "缺少参数：procedure_name"
#  exit 1
#fi

proc_key="graph"
echo ${proc_key}
PROCESS=`ps -ef|grep ${proc_key}|grep -v grep|grep -v PPID|awk '{ print $2}'`
for i in $PROCESS
do
  echo "Kill the $1 process [ $i ]"
  kill -9 $i
done
