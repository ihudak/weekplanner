DIR=`pwd`
for d in */ ; do
    [ -L "${d%/}" ] && continue
    sed -i.bak "s;{CURDIR};${DIR}/${d%/};g" $d${d%/}.yaml
    rm $d*.bak
done

