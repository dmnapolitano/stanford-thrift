#!/bin/bash

CMD="java -cp $CLASSPATH:stanford-parser-wrapper.jar StanfordParserServer"

if [ $# -eq  2 ]; then
    PORT=$1
    MODELFILE=$2
    $CMD $PORT $MODELFILE
elif [ $# -eq 1 ]; then
    PORT=$1
    $CMD $PORT
else
    echo "Usage: start_server.sh <port> [<modelfile>]"
fi
