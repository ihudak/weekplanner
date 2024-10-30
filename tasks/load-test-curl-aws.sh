#!/bin/bash

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
	a=$(curl -s http://a08a438d851bf4b429ea343238054bf2-94890095.us-east-1.elb.amazonaws.com:83/api/v1/tasks);
	#b=${a:2:7};
	echo $a;
done
endTime=$(date +%s%N)
print_date "End: " $endTime
diffMiliSeconds="$((($endTime-$startTime + 500) / 1000000 ))"
diffMicroSeconds="$((($endTime-$startTime + 500) / 1000 ))"
diffMicroSeconds="$(($diffMicroSeconds-$diffMiliSeconds*1000))"

echo "Diff is $diffMiliSeconds.$diffMicroSeconds ms"
