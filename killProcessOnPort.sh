#!/bin/bash

#Input: Port list such as 2555, 8080, 80
# e.g. ./killProcessOnPort 2555 8080 80
# Syntax ./killProcessOnPort port[,port...]

#Steps:

# Check arugment validation
if [ $# -lt 1 ]; then
 echo "At least one port must be specified. Syntax ./killProcessOnPort port[ port...]"
 exit
fi

kill_candidates=()

#Iterate over the arguments of ports
for port in $@
do
 #Show information of process listening on port
 lsof -n -i4TCP:$port | grep LISTEN

 #Parse process id from process information
 pid=$(lsof -n -i4TCP:$port | grep LISTEN | tr -s ' ' | cut -d ' ' -f 2)

 #Add process id into kill candidates list
 if [ -n "$pid" ]; then
 	kill_candidates[$port]=$pid
 fi

done

#Exit if there is no process listening on argument ports
if [ ${#kill_candidates[@]} -lt 1 ]; then
 echo "No process listening on your ports"
 exit;
fi

#Show kill candidates
echo "Process having pids below will be killed"
echo ${kill_candidates[@]}
for port in "${!kill_candidates[@]}"
do
  echo "-Port: $port"
  echo "--PID: ${kill_candidates[$port]}"
done

# Prompt user confirmation (e.g. 'Kill process with id in: kill-candidates ?(y/n)')
# User yes->kill process in kill-candidate processes list
read -p "Are you sure to kill those processes? (y/n) " RESP
if [ "$RESP" = "n" ]; then
  echo "Glad to hear it"
else
  kill -9 ${kill_candidates[@]}
fi


