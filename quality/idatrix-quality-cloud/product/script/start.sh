#!/bin/bash

package=$(cd `dirname $0`; pwd)

PENTAHO_DI_JAVA_OPTIONS="-verbose:gc -XX:+PrintGCDetails -Xms8192m -Xmx16384m "
#PENTAHO_DI_JAVA_OPTIONS="-XX:-UseGCOverheadLimit -verbose:gc -XX:+PrintGCDetails -Xms1024m -Xmx2048m "

OPT="${OPT} ${PENTAHO_DI_JAVA_OPTIONS} -Dfile.encoding=UTF-8 -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2 "
DEBUG=""
param="config/quality-config.xml"

#delete error.log
[[ -e  ${package}/error.log ]] && rm ${package}/error.log 

if [ ! x$1 = x ];then
	if [ "x$1" = "x-o" ];then
		isout=true
	elif [ "x$1" = "x-d" ];then
		DEBUG=" -Xdebug -Xrunjdwp:transport=dt_socket,address=9999,server=y,suspend=n "
	else
		param=$1
	fi
fi

if [ ! x$2 = x ];then
	if [[ "server" = $2 ]]; then
		OPT="${OPT} -Didatrix.web.deployment=false " 
	fi
fi


error_message () {
  echo
  echo "!!!"
  echo -e "!!! ${1}"
  echo "!!!"
  echo 
  
}

test_environment () {

  # Test to see that Java is installed and working
  java &>/dev/null || JAVA_EXIT=$? || true
  if [ "${JAVA_EXIT}" != "1" ]; then
	if [ ! -z ${ETL_USE_ETC_PROFILE} ]; then
		source /etc/profile
	fi
	if [ ! -z ${ETL_USE_USER_BASHRC} ]; then
		source ~/.bashrc
	fi	
	java &>/dev/null || JAVA_EXIT=$? || true
	if [ "${JAVA_EXIT}" != "1" ]; then
	  error_message "We could not find a working Java JVM. 'java' command fails."
      exit -1;
	fi
    
  fi

  # Che requires Java version 1.8 or higher.
  JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
  if [  -z "${JAVA_VERSION}" ]; then
      error_message "Failure running java -version. "
      exit -1;
  fi

  if [[ "${JAVA_VERSION}" < "1.8" ]]; then
      error_message "ETL requires Java version 1.8 or higher. We found ${JAVA_VERSION}."
      exit -1;
  fi

}

stop_service () {

proc_key="launcher.jar"
if [ ! x$1 = x ];then
proc_key=$1
fi
echo "stop server, key :${proc_key}"
proc_id=`ps -ef|grep -v "grep"|grep ${proc_key}|awk '{print $2}'`
if [[ -z $proc_id ]];then
    echo "The task is not running ! "
else
     echo "server key pid:"${proc_id[@]}
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
	 sleep 1s
fi

}


cd $package
[[ ! -d $package/logs ]] && mkdir $package/logs
logfilename=${param##*/}
logfilename=${logfilename%.*}.log
if [[ "config/quality-config.xml" = $param ]]; then
	if [ ! -e ${param} ] || [ ! `grep -c "masterIp" ${param}` -eq '0' ]; then
		default_port=60092
		default_IP=`/sbin/ifconfig -a|grep inet|grep -v 127.0.0.1|grep -v inet6|awk '{print $2}'|tr -d "addr:"`
		param=" ${default_IP} ${default_port} "
		echo " Visit: ${default_IP}:${default_port} "
		logfilename=quality.log
	fi
fi


OPT=" ${OPT} -DETL_LOG_FILENAME=${logfilename} ${DEBUG} " 

test_environment

export _JAVA_SR_SIGNUM=12

# stop server
stop_service $param
echo "service is starting..."

#nohup java ${OPT} ${DEBUG} -jar etl.jar $param	> $package/logs/etl.log  2>&1 &
nohup java ${OPT} -jar launcher/launcher.jar $param >/dev/null  2>&1 & 
if [ "$isout" = "true" ];then
	tail -900f  $package/logs/${logfilename}
fi








