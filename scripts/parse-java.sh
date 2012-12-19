#!/bin/bash

CMD="java -cp $CLASSPATH StanfordParserClient"

if [ $# -eq 3 ]; then
    SERVER=$1
    PORT=$2
    INFILE=$3
    $CMD $SERVER $PORT $INFILE
else
    echo "Usage: parse.sh <server> <port> [<inputfile>]"
fi
