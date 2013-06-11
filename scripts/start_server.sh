#!/bin/zsh

MAINDIR=$(dirname $0:A)/../

#e.g., start_server.sh  9999 edu/stanford/nlp/models/lexparser/englishFactored.ser.gz 4096m


if [ $# -eq 3 ]; then
 PORT=$1
 MODEL=$2
 HEAPSIZE=$3
 java -cp $CLASSPATH:$MAINDIR/stanford-corenlp-wrapper.jar -Xmx$HEAPSIZE -XX:-UseGCOverheadLimit StanfordCoreNLPServer $PORT $MODEL
else
 echo "Usage: $(basename $0) <port> <model> <heapsize>"
 echo "e.g., $(basename $0) 9999 edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz 4G"
fi


