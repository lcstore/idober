#!/bin/bash

PROFILE_FILE=~/.bash_profile
if [ -f "$PROFILE_FILE" ]; then
   echo "source:$PROFILE_FILE"
   source $PROFILE_FILE
else
   echo "Not exist:$PROFILE_FILE"
fi 
PROFILE_FILE=/etc/profile
if [ -f "$PROFILE_FILE" ]; then
   echo "source:$PROFILE_FILE"
   source $PROFILE_FILE
else
   echo "Not exist:$PROFILE_FILE"
fi 

NODE_NAME='/apps/src/tomcat-idober'
NODE_PIDS=`ps -ef | grep ${NODE_NAME}|grep -v grep |awk '{print $2}'`
if [[ "W"$NODE_PIDS != "W" ]]; then
	echo "[`date`],status.App[$NODE_NAME] is runing on:${NODE_PIDS[*]}"
else
	echo "[`date`],status.App[$NODE_NAME] is shutdown..."
	echo "[`date`],reboot.App[$NODE_NAME] ..."
    sh ./deploy-idober.sh 
fi