#!/bin/bash

print_date() {
	noNano="$(date -d@"$(( $2 / 1000000000 ))" +"%Y-%m-%d-%H:%M:%S")"
	nano="$(( $2 % 1000000000))"
	echo $1 $noNano.$nano
}

podDelTime=$(date +%s%N)
print_date "Before Pod Del: " podDelTime
k delete po $1 -n weekplanner

startTime=$(date +%s%N)
print_date "Start: " $startTime
a=""
while [ "$a" == "" ];
do
	a=$(curl -s http://<server>/api/categories/api/v1/categories);
	#echo $a;
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
