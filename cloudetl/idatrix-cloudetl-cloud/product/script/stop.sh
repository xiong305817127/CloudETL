#!/bin/bash

param="config/cloudetl-config.xml"
if [ ! x$1 = x ];then
param=$1
fi

if [[ "config/cloudetl-config.xml" = $param ]]; then
	if [ ! -e ${param} ] || [ ! `grep -c "masterIp" ${param}` -eq '0' ]; then
		#default_port=60090
		#default_IP=`/sbin/ifconfig -a|grep inet|grep -v 127.0.0.1|grep -v inet6|awk '{print $2}'|tr -d "addr:"`
		#param=" ${default_IP} ${default_port} "
		param=""
	fi
fi

proc_key="launcher.jar $param"

echo $proc_key
proc_id=`ps -ef|grep -v "grep"|grep "${proc_key}"|awk '{print $2}'`
if [[ -z $proc_id ]];then
    echo "The task is not running ! "
else
     echo "ETL 9090 pid:"${proc_id[@]}
     echo "------kill the task!------"
     for id in ${proc_id[*]}
     do
       thread=`ps -mp ${id}|wc -l`
       kill -9 ${id}
       if [ $? -eq 0 ];then
            echo "service is stoped"
       else
            echo "kill proc failed "
       fi
     done
fi


