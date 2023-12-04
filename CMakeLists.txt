cmake_minimum_required(VERSION 3.1)
project(ogdf_based_lib CXX)

# OGDF
set(OGDF_DIR "ogdf")

# JNI
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# TMAP
set(TMAP_DIR "tmap")

# SRC
set(SRC_DIR "src")

# TEST
set(TEST_DIR "tests/cpp_test")

target_include_directories(ogdf_based_lib PUBLIC ${OGDF_DIR} ${CMAKE_CURRENT_SOURCE_DIR})
target_include_directories(ogdf_based_lib PUBLIC ${TMAP_DIR})
target_include_directories(ogdf_based_lib PUBLIC ${SRC_DIR})
target_include_directories(ogdf_based_lib PUBLIC ${TEST_DIR})
add_library(ogdf_based_lib SHARED ogdf_based_lib.cpp ogdf_based_lib.h) # Das ist das wichtige hier (SHARED!), waere der OGDF_JNI_WRAPPER

find_package("../src")

target_link_libraries(ogdf_based_lib PRIVATE ${OGDF_DIR} ${TMAP_DIR} ${SRC_DIR} ${TEST_DIR}) # wrapper mit OGDF + TMAP linken

add_executable(ogdf_test ogdf_test.cpp) # test ob's auch funktioniert
target_link_libraries(ogdf_test PRIVATE ogdf_based_lib) # test linken
