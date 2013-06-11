#!/bin/zsh

MAINDIR=$(dirname $0:A)/../
CMD="java -cp $CLASSPATH:$MAINDIR/stanford-corenlp-wrapper.jar -Xmx4096m -XX:-UseGCOverheadLimit StanfordCoreNLPServer"

#e.g., start_server.sh  9999
#      start_server.sh  9999 edu/stanford/nlp/models/lexparser/englishFactored.ser.gz


if [ $# -eq 2 ]; then
 PORT=$1
 MODEL=$2
 eval $CMD $PORT $MODEL
elif [ $# -eq 1 ]; then
 PORT=$1
 eval $CMD $PORT
else
 echo "Usage: start_server.sh <port> (<model>)"
fi
