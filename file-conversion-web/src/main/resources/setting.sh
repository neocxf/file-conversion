#!/usr/bin/env bash

set -o nounset                              # Treat unset variables as an error

PROCESSES=$(ps axo pid,ppid,command)

# Find eclipse launcher PID
LAUNCHER_PID=$(echo "$PROCESSES" | grep "/usr/lib/eclipse/eclipse" |grep -v "launcher"|awk '{print $1}')
echo "Launcher PID $LAUNCHER_PID"

# Find eclipse PID
ECLIPSE_PID=$(echo "$PROCESSES" | egrep "[[:digit:]]* $LAUNCHER_PID " | awk '{print $1}')
echo "Eclipse PID $ECLIPSE_PID"

# Find running eclipse sub-process PIDs
SUB_PROCESS=$(echo "$PROCESSES" | egrep "[[:digit:]]* $ECLIPSE_PID " | awk '{print $1}')

# List processes
echo
for PROCESS in $SUB_PROCESS; do
    DRIVER=$(ps --no-headers o pid,ppid,command $PROCESS | awk '{print $NF}')
    echo "$PROCESS $DRIVER"
done

echo "Kill a process using: 'kill -SIGTERM \$PID'"