./set_cur_dir.sh

for d in */ ; do
    [ -L "${d%/}" ] && continue
    kubectl apply -f $d${d%/}.yaml
done
