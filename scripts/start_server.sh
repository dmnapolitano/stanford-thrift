#!/bin/zsh

MAINDIR=$(dirname $0:A)/../

if [ $# -eq 3 ]; then
 PORT=$1
 MODEL=$2
 HEAPSIZE=$3
 java -cp $CLASSPATH:$MAINDIR/stanford-corenlp-wrapper.jar -Xmx$HEAPSIZE -XX:-UseGCOverheadLimit StanfordCoreNLPServer $PORT $MODEL
elif [ $# -eq 2 ]; then
 PORT=$1
 HEAPSIZE=$2
 java -cp $CLASSPATH:$MAINDIR/stanford-corenlp-wrapper.jar -Xmx$HEAPSIZE -XX:-UseGCOverheadLimit StanfordCoreNLPServer $PORT
else
 echo "Usage: $(basename $0) <port> [<model>] <heapsize>"
 echo "e.g., $(basename $0) 9999 edu/stanford/nlp/models/lexparser/englishFactored.ser.gz 4G"
 echo "or, $(basename $0) 9999 4G"
 echo "Parser model is optional; will use edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz by default."
fi


