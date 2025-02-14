#!/bin/bash
PROJECT_ROOT="/home/habin/action"
JAR_FILE=$PROJECT_ROOT/tune_fun-0.0.1-SNAPSHOT.jar

APP_LOG=$PROJECT_ROOT/app.log
ERROR_LOG=$PROJECT_ROOT/error.log
DEPLOY_LOG=$PROJECT_ROOT/deploy.log
JAVA_EXECUTABLE=/usr/local/jdk-21/bin/java
#JAVA_EXECUTABLE=$(which java)

TIME_NOW=$(date +%c)

# build 파일 복사
echo "$TIME_NOW > $JAR_FILE 파일 복사" >> $DEPLOY_LOG
cp $PROJECT_ROOT/build/libs/tune_fun-0.0.1-SNAPSHOT.jar $JAR_FILE

# jar 파일 실행
echo "$TIME_NOW > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
nohup $JAVA_EXECUTABLE -jar $JAR_FILE --spring.profiles.active=dev > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $JAR_FILE)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG
