#!/bin/bash

pwd
mkdir dependencies

# write dependencies to temp file
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    LD_LIBRARY_PATH=../tmap/ogdf-conda/src ldd *.so | grep -v 'not found' | grep -v ':' > lddout
elif [[ "$OSTYPE" == "msys" ]]; then
    ldd *.dll | grep -v 'not found' | grep -v ':' > lddout
elif [[ "$OSTYPE" == "darwin"* ]]; then
    export DYLD_PRINT_LIBRARIES=1
    export DYLD_PRINT_LIBRARIES_POST_LAUNCH=1
    ./LayoutFromEdgeList_test 2> lddout
fi

echo "===DEPENDENCIES==="
cat lddout

# for more libraries, just add another '-e' part with the corresponding pattern
cat lddout | grep -e 'c++' \
                  -e 'gcc_s' \
                  -e 'libomp' \
                  -e 'COIN' \
                  -e 'OGDF' \
                  -e 'tmap' \
    | cut -d' ' -f3 | xargs -r -I{} cp {} dependencies/ || true
