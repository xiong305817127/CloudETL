#赋值进程名给proc_key
proc_key="db-proxy"
#echo ${proc_key}

# 获取进程id
PROCESS=`ps -ef|grep ${proc_key}|grep -v grep|grep -v PPID|awk '{ print $2}'`

# kill process
for i in $PROCESS
do 
  flag=1
  echo "kill  process $i"
  kill -9 $i
done



echo "准备中,请稍后..."
# sleep 60 秒
sleep 60s
echo  "启动服务..."
nohup java -Xbootclasspath/a:/config -jar db-proxy.jar >db-proxy.log 2>&1 &
tail -f db-proxy.log
