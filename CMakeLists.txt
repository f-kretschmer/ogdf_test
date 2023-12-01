cmake_minimum_required(VERSION 3.1)
project(ogdf-user-example CXX)

# OGDF
set(OGDF_DIR "ogdf")
find_library(OGDF NAMES "ogdf" PATHS "ogdf" REQUIRED)

# JNI
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# TMAP
set(TMAP_DIR "tmap")
find_library(TMAP NAMES "tmap" PATHS "tmap" REQUIRED)


add_library(ogdf_based_lib SHARED ogdf_based_lib.cpp ogdf_based_lib.h) # Das ist das wichtige hier (SHARED!), waere der OGDF_JNI_WRAPPER
target_include_directories(ogdf_based_lib PRIVATE ${OGDF_DIR})
target_include_directories(ogdf_based_lib PRIVATE ${TMAP_DIR})
target_link_libraries(ogdf_based_lib PRIVATE ${OGDF} ${TMAP} ) # wrapper mit OGDF + TMAP linken

add_executable(ogdf_test ogdf_test.cpp) # test ob's auch funktioniert
target_link_libraries(ogdf_test PRIVATE ogdf_based_lib) # test linken
