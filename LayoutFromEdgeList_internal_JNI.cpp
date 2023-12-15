#include "LayoutFromEdgeList_internal.hpp"
#include <jni.h>

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
