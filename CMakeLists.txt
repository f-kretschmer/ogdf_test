cmake_minimum_required(VERSION 3.12)
project(ogdf-tmap-wrapper CXX)
set (CMAKE_CXX_STANDARD 11)

# JNI
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# OGDF/TMAP
find_library(OGDF NAMES "ogdf" "OGDF" PATHS "tmap/ogdf-conda/src" REQUIRED)
find_library(TMAP NAMES "tmap" PATHS "tmap/out/" REQUIRED)
# NOTE: windows requires .lib files additional to .dll. To find .dll libraries: set(CMAKE_FIND_LIBRARY_SUFFIXES .dll ${CMAKE_FIND_LIBRARY_SUFFIXES})

add_library(LayoutFromEdgeList_internal SHARED LayoutFromEdgeList_internal.cpp LayoutFromEdgeList_internal.hpp)
add_library(LayoutFromEdgeList_internal_JNI SHARED LayoutFromEdgeList_internal_JNI.cpp)

# target_include_directories(LayoutFromEdgeList_internal PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
# target_include_directories(LayoutFromEdgeList_internal_JNI PUBLIC ${JNI_INCLUDE_DIRS})

target_include_directories(LayoutFromEdgeList_internal PUBLIC "tmap/ogdf-conda/src/include")
target_include_directories(LayoutFromEdgeList_internal PUBLIC "tmap/src/_tmap")

target_link_libraries(LayoutFromEdgeList_internal PUBLIC ${OGDF} ${TMAP}) # wrapper mit OGDF + TMAP linken
target_link_libraries(LayoutFromEdgeList_internal_JNI PRIVATE LayoutFromEdgeList_internal)


add_executable(LayoutFromEdgeList_test test_cpp-endpoint.cpp)
# target_include_directories(LayoutFromEdgeList_test PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
target_link_libraries(LayoutFromEdgeList_test PRIVATE LayoutFromEdgeList_internal) # test linken
