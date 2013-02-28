#!/bin/bash

CMD="python parser_client.py"

if [ $# -eq 3 ]; then
    SERVER=$1
    PORT=$2
    INFILE=$3
    $CMD $SERVER $PORT $INFILE
else
    echo "Usage: parse.sh <server> <port> [<inputfile>]"
fi
