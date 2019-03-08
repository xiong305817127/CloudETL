#赋值进程名给proc_key
proc_key="db-proxy"
#echo ${proc_key}

# 获取进程id
PROCESS=`ps -ef|grep ${proc_key}|grep -v grep|grep -v PPID|awk '{ print $2}'`

#echo ${PROCESS[*]}
length=${#PROCESS[*]}
#echo "graph 进程个数为=${length}"
#echo "graph 进程id =${PROCESS[*]}"

flag=0
for i in $PROCESS
do 
  flag=1
#  echo "loop process $i"
done

#如果进程正在运行，退出
#echo "flag =$flag"
if [ $flag -ge 1 ]; 
then
  echo "idatrix-es 进程正在运行,进程id =${PROCESS[*]}"
  echo "退出"
  exit 1
fi

echo "启动服务...."
nohup java -Xbootclasspath/a:/config -jar db-proxy.jar >db-proxy.log 2>&1 &
tail -f db-proxy.log
