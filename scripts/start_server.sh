#!/bin/zsh

MAINDIR=$(dirname $0:A)/../

if [ $# -eq 3 ]; then
 PORT=$1
 HEAPSIZE=$2
 CONFIG=$3
 java -cp $CLASSPATH:$MAINDIR/stanford-corenlp-wrapper.jar -Xmx$HEAPSIZE -XX:-UseGCOverheadLimit org.ets.research.nlp.stanford_thrift.StanfordCoreNLPServer $PORT $CONFIG
else
 echo "Usage: $(basename $0) <port> <heapsize> <config file>"
 echo "See scripts/ for an example config file."
fi


