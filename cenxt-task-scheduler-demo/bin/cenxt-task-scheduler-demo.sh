#!/bin/sh

APP_PATH=${0##*/}
APP_NAME=${APP_PATH%%.*}
bin=`dirname "${BASH_SOURCE-$0}"`
APP_HOME=`cd "$bin"; pwd`
#APP_HOME=`cd "$bin"/..; pwd`

# SET JVM JAVA_OPTS
JAVA_OPTS=-Xms64m
JAVA_OPTS=$JAVA_OPTS' '-Xmx128m
JAVA_OPTS=$JAVA_OPTS' '-XX:PermSize=64m
JAVA_OPTS=$JAVA_OPTS' '-XX:MaxPermSize=64m
JAVA_OPTS=$JAVA_OPTS' '-XX:NewSize=64m
JAVA_OPTS=$JAVA_OPTS' '-XX:MaxNewSize=64m
JAVA_OPTS=$JAVA_OPTS' '-XX:+UseParNewGC
JAVA_OPTS=$JAVA_OPTS' '-XX:+UseConcMarkSweepGC
JAVA_OPTS=$JAVA_OPTS' '-XX:CMSFullGCsBeforeCompaction=5
JAVA_OPTS=$JAVA_OPTS' '-XX:+UseCMSCompactAtFullCollection
JAVA_OPTS=$JAVA_OPTS' '-XX:CMSMaxAbortablePrecleanTime=5000
JAVA_OPTS=$JAVA_OPTS' '-XX:+CMSClassUnloadingEnabled
JAVA_OPTS=$JAVA_OPTS' '-XX:CMSInitiatingOccupancyFraction=50
JAVA_OPTS=$JAVA_OPTS' '-XX:+DisableExplicitGC
JAVA_OPTS=$JAVA_OPTS' '-Dsun.net.client.defaultConnectTimeout=10000
JAVA_OPTS=$JAVA_OPTS' '-Dsun.net.client.defaultReadTimeout=30000
JAVA_OPTS=$JAVA_OPTS' '-Djava.net.preferIPv4Stack=true
#JAVA_OPTS=$JAVA_OPTS' '-verbose:gc
#JAVA_OPTS=$JAVA_OPTS' '-Xloggc:/applog/cenxt-task-scheduler-demo/gc-`date +%Y%m%d%H%M%S`.log
#JAVA_OPTS=$JAVA_OPTS' '-XX:+PrintGCDetails
#JAVA_OPTS=$JAVA_OPTS' '-XX:+PrintGCDateStamps
#JAVA_OPTS=$JAVA_OPTS' '-XX:+UseCompressedOops
JAVA_OPTS=$JAVA_OPTS' '-XX:+HeapDumpOnOutOfMemoryError
JAVA_OPTS=$JAVA_OPTS' '-XX:HeapDumpPath=/applog/cenxt-task-scheduler-demo

#******************************************************检查执行用户*****************************************************************#
#USER_ID=`id -u`

#if [ $USER_ID -eq 0 ];then
#	echo "do not use root user for safty";
#	exit 1
#fi
#***********************************************************************************************************************************#


#********************************************************打印系统信息***************************************************************#
#(函数)打印系统环境参数
info()
{
	echo "System Information:"
	echo "*********************************************************************************************"
	echo `/bin/uname`
	echo ""
	echo `uname -a`
	echo ""
	echo "JAVA_HOME=$JAVA_HOME"
	echo ""
	echo "APP_NAME="$APP_NAME
	echo "*********************************************************************************************"
}
#***********************************************************************************************************************************#



#****************************************************判断程序启动成功与否***********************************************************#
#判断程序是否已启动
psid=0
checkpid()
{
	psid=`ps -ef|grep "$APP_NAME".*jar|grep -v grep|grep -v kill|awk '{print $2}'`
	echo '***************psid ='$psid;
}
#***********************************************************************************************************************************#


#********************************************************检查程序执行状态***********************************************************#
#(函数)检查程序运行状态
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示正在运行并表示出pid
#3. 否则，提示程序未运行

status()
{
	checkpid
	if [[ $psid -ne 0 ]];  then
		echo "$APP_NAME is running! (pid=$psid)"
	else
 		echo "$APP_NAME is not running"
 	fi
 }
#***********************************************************************************************************************************#


#********************************************************启动程序*******************************************************************#
#启动程序
start()
{
	checkpid
	if [[ $psid -ne 0 ]]; then
	  echo "================================"
	  echo "warn: $APP_NAME already started! (pid=$psid)"
	  echo "================================"
	else
	  echo -n "Starting $APP_NAME ..."
 	   #nohup模式启动程序
	  "$JAVA_HOME"/bin/java -server $JAVA_OPTS -jar "$APP_HOME"/../"$APP_NAME"*.jar --spring.config.location=$APP_HOME/../config/ --spring.profiles.active=${SPRING_PROFILES_ACTIVE} --logging.config=$APP_HOME/../config/log4j2.xml
	  sleep 1;
	  checkpid
	  if [[ $psid -ne 0 ]]; then
	  	echo "(pid=$psid) [OK]"
	  else
	  	echo "[Failed]"
	  fi
 	fi
 }
#***********************************************************************************************************************************#


#********************************************************停止程序*******************************************************************#
#停止程序
stop()
{
	checkpid
 	if [[ $psid -ne 0  ]]; then
	   	echo -n "Stopping $APP_NAME ...(pid=$psid) "
	    #su - $RUNNING_USER -c "kill -9 $psid"
	    kill -9 $psid
	    if [[ $? -eq 0 ]]; then
	       echo "[OK]"
	    else
	       echo "[Failed]"
	    fi
	       checkpid
	    if [[ $psid -ne 0 ]]; then
	       stop
	    fi
     else
        echo "================================"
        echo "warn: $APP_NAME is not running"
        echo "================================"
    fi
}
#***********************************************************************************************************************************#


#读取脚本的第一个参数($1)，进行判断
#参数取值范围：{start|stop|restart|status|info}
case "$1" in
	 'start')     start    ;;
	 'stop')      stop     ;;
	 'status')    status   ;;
	 'info')      info     ;;
	 *)  echo "Usage:$0 {start|stop|status|info}"     exit 1
esac