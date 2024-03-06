#!/bin/bash

pwd
mkdir dependencies

# write dependencies to temp file
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    ldd *.so | grep -v 'not found' | grep -v ':' > lddout
elif [[ "$OSTYPE" == "msys" ]]; then
    ldd *.dll | grep -v 'not found' | grep -v ':' > lddout
elif [[ "$OSTYPE" == "darwin"* ]]; then
    chmod +x ../install_utils/mac_ldd.sh
    ../install_utils/mac_ldd.sh *.dylib 2> lddout
fi

echo "===DEPENDENCIES==="
cat lddout

# for more libraries, just add another '-e' part with the corresponding pattern
cat lddout | grep -e 'c++' \
                  -e 'gcc_s' \
                  -e 'omp' \
                  -e 'COIN' \
                  -e 'OGDF' \
                  -e 'tmap' \
    | cut -d' ' -f3 | xargs -r -I{} cp {} dependencies/ || true
