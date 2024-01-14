for d in */ ; do
    [ -L "${d%/}" ] && continue
    kubectl delete -f $d${d%/}.yaml
done