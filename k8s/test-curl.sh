#!/bin/bash

server_url=localhost:8084   # put ingress-url here

print_date() {
	noNano="$(date -d@"$(( $2 / 1000000000 ))" +"%Y-%m-%d-%H:%M:%S")"
	nano="$(( $2 % 1000000000))"
	echo $1 $noNano.$nano
}

if [ "$1" == "" ]; then
	echo pod name is required;
	exit 0;
fi

podDelTime=$(date +%s%N)
print_date "Before Pod Del: " podDelTime
kubectl delete po $1 -n weekplanner

startTime=$(date +%s%N)
print_date "Start: " $startTime
b=""
c="content"
while [ "$b" != "$c" ];
do
  t=$(curl -s http://$server_url/api/tasks/api/v1/tasks);  # if calling through ingress
  # t=$(curl -s http://$server_url/api/v1/tasks);          # if calling directly
  #  echo $t;
	w=$(curl -s http://$server_url/api/workitems/api/v1/workitems);  # if calling through ingress
  # w=$(curl -s http://$server_url/api/v1/workitems);              # if calling directly
	b=${w:2:7};
	#echo $w;
	#echo $b;
done
endTime=$(date +%s%N)
print_date "End: " $endTime
diffMiliSeconds="$((($endTime-$startTime + 500) / 1000000 ))"
diffMicroSeconds="$((($endTime-$startTime + 500) / 1000 ))"
diffMicroSeconds="$(($diffMicroSeconds-$diffMiliSeconds*1000))"

echo "Diff is $diffMiliSeconds.$diffMicroSeconds ms"


diffMiliSeconds="$((($endTime-$podDelTime + 500) / 1000000 ))"
diffMicroSeconds="$((($endTime-$podDelTime + 500) / 1000 ))"
diffMicroSeconds="$(($diffMicroSeconds-$diffMiliSeconds*1000))"

echo "E2E is $diffMiliSeconds.$diffMicroSeconds ms"
