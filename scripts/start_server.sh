#!/bin/bash

CMD="java -cp $CLASSPATH:stanford-corenlp-wrapper.jar -Xmx10240m -XX:-UseGCOverheadLimit StanfordCoreNLPServer"

#if [ $# -eq 1 ]; then
PORT=$1
MODEL=$2
$CMD $PORT $MODEL
#else
#    echo "Usage: start_server.sh <port>"
#fi