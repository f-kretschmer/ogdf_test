#include <stdint.h>
#include <tuple>
#include <vector>
#include <iostream>
#include <layout.hh>
#include "de_unijena_bioinf_TreeVisualization_TreeVisualizer.h"

float** LayoutFromEdgeList_internals(int number_of_nodes, int* sources, int* destinations, float* weights, int number_of_edges);

JNIEXPORT jobjectArray JNICALL Java_de_unijena_bioinf_TreeVisualization_TreeVisualizer_LayoutFromEdgeList_1internal
  (JNIEnv *env, jobject thisObject, jint j_number_of_nodes, jintArray j_sources, jintArray j_destinations,
  jfloatArray j_weights, jint j_number_of_edges) {
    
    
    int number_of_nodes = (int)j_number_of_nodes;
    int number_of_edges = (int)j_number_of_edges;

    /*
    // convert to jni pointer
    jint *elements_sources = (*env)->GetIntArrayElements(env, j_sources, 0);
    jint *elements_destinations = (*env)->GetIntArrayElements(env, j_destinations, 0);
    jfloat *elements_weights = (*env)->GetFloatArrayElements(env, j_weights, 0);

    // convert to cpp pointer
    int* sources = (int*)elements_sources;
    int* destinations = (int*)elements_destinations;
    float* weights = (float*)elements_weights;
    */

    int* sources = (int*)j_sources;
    int* destinations = (int*)j_destinations;
    float* weights = (float*)j_weights;

    // call Function
    float** res = LayoutFromEdgeList_internals(number_of_nodes, sources, destinations, weights, number_of_edges);

    // calculate Array sizes
    size_t numRows = 4;
    int numColumns = number_of_nodes;

    // Create the outer jobjectArray
    jobjectArray outerArray = env->NewObjectArray(numRows, env->FindClass("[F"), nullptr);

    // Iterate over each row
    for (int i = 0; i < numRows; i++) {
      // Create a jfloatArray for each row
      jfloatArray innerArray = env->NewFloatArray(numColumns);

      // Set the elements of the jfloatArray
      env->SetFloatArrayRegion(innerArray, 0, numColumns, res[i]);

      // Set the jfloatArray as an element of the outer jobjectArray
      env->SetObjectArrayElement(outerArray, i, innerArray);

      // Delete local references to avoid memory leaks
      env->DeleteLocalRef(innerArray);
    }

    /*
    // Release the elements of the jintArray
    (*env)->ReleaseIntArrayElements(env, j_sources, elements_sources, JNI_ABORT);
    (*env)->ReleaseIntArrayElements(env, j_destinations, elements_destinations, JNI_ABORT);
    (*env)->ReleaseFloatArrayElements(env, j_weights, elements_weights, JNI_ABORT);
    */
    // Return the jobjectArray
    return outerArray;
  }

float ** LayoutFromEdgeList_internals(int number_of_nodes, int* sources, int* destinations, float* weights, int number_of_edges) {

    // Try Find GraphProperties
    auto gp = tmap::GraphProperties();
    std::cout << gp.mst_weight << std::endl;

    // Create a vector of tuples
    std::vector<std::tuple<uint32_t, uint32_t, float>> edges;
    
    // Populate the vector with tuples
    for (int i = 0; i < number_of_nodes; ++i) {
        edges.push_back(std::make_tuple(static_cast<uint32_t>(sources[i]), static_cast<uint32_t>(destinations[i]), weights[i]));
    }
    std::tuple<std::vector<float>, std::vector<float>, std::vector<uint32_t>, std::vector<uint32_t>, tmap::GraphProperties> returned = tmap::LayoutFromEdgeList(number_of_nodes, edges);


    // Extract the vectors from the tuple
    std::vector<float>& vector1 = std::get<0>(returned);
    std::vector<float>& vector2 = std::get<1>(returned);
    std::vector<uint32_t>& vector3 = std::get<2>(returned);
    std::vector<uint32_t>& vector4 = std::get<3>(returned);

    // Convert the uint32_t vectors into float vectors
    std::vector<float> floatVector3(vector3.begin(), vector3.end());
    std::vector<float> floatVector4(vector4.begin(), vector4.end());

    // Determine the dimensions of the 2D float array
    size_t numRows = 4;
    size_t numCols = 0; // Initialize max_size to zero

    // Iterate over the vectors and update max_size if a larger size is found
    numCols = std::max(numCols, vector1.size());
    numCols = std::max(numCols, vector2.size());
    numCols = std::max(numCols, floatVector3.size());
    numCols = std::max(numCols, floatVector4.size());

    // Allocate memory for the 2D float array
    float** result = new float*[numRows];
    for (size_t i = 0; i < numRows; ++i) {
        result[i] = new float[numCols];
    }

    // Copy the elements from the vectors into the 2D float array
    for (size_t i = 0; i < numCols; ++i) {
        if (i < vector1.size()) {
            result[i][0] = vector1[i];
        }
        if (i < vector2.size()) {
            result[i][1] = vector2[i];
        }
        if (i < floatVector3.size()) {
            result[i][2] = floatVector3[i];
        }
        if (i < floatVector4.size()) {
            result[i][3] = floatVector4[i];
        }
    }
    return result;
}
