#!/bin/bash

pwd
mkdir dependencies
# for more libraries, just add another '-e' part with the corresponding pattern
ldd libCOIN.so | grep -v 'not found' | grep -e 'stdc++' \
                                            -e 'gcc_s' \
                                            -e 'omp' \
                                            -e 'COIN' \
                                            -e 'OGDF' \
                                            -e 'tmap' \
    | cut -d' ' -f3 | xargs -r0 cp -t dependencies
