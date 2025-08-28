#!/bin/bash

srv_url=localhost:8084   # put ingress-url here

print_date() {
	noNano="$(date -d@"$(( $2 / 1000000000 ))" +"%Y-%m-%d-%H:%M:%S")"
	nano="$(( $2 % 1000000000))"
	echo $1 $noNano.$nano
}


startTime=$(date +%s%N)
print_date "Start: " $startTime
b=""
c="content"
while [ "$b" != "$c" ];
do
  t=$(curl -s http://$srv_url/api/tasks/api/v1/tasks);  # if calling through ingress
  # t=$(curl -s http://$srv_url/api/v1/tasks);          # if calling directly
  echo $t;

	w=$(curl -s http://$srv_url/api/workitems/api/v1/workitems); # if calling through ingress
	# w=$(curl -s http://$srv_url/api/v1/workitems);             # if calling directly
	#b=${w:2:7};
	echo $w;
done
endTime=$(date +%s%N)
print_date "End: " $endTime
diffMiliSeconds="$((($endTime-$startTime + 500) / 1000000 ))"
diffMicroSeconds="$((($endTime-$startTime + 500) / 1000 ))"
diffMicroSeconds="$(($diffMicroSeconds-$diffMiliSeconds*1000))"

echo "Diff is $diffMiliSeconds.$diffMicroSeconds ms"

