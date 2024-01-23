#include <stdint.h>
#include <tuple>
#include <vector>
#include <iostream>
#include "layout.hh"
#include "LayoutFromEdgeList_internal.hpp"

float ** LayoutFromEdgeList_internals(int number_of_nodes, int* sources, int* destinations, float* weights, int number_of_edges) {

    // Try Find GraphProperties
    auto gp = tmap::GraphProperties();
    //std::cout << gp.mst_weight << std::endl;

    // Create a vector of tuples
    std::vector<std::tuple<uint32_t, uint32_t, float>> edges;

    // Populate the vector with tuples
    for (int i = 0; i < number_of_nodes; ++i) {
        edges.push_back(std::make_tuple(static_cast<uint32_t>(sources[i]), static_cast<uint32_t>(destinations[i]), weights[i]));
    }
	// generate LayoutConfig
    tmap::LayoutConfiguration config  = tmap::LayoutConfiguration();
	
	// call function
    std::tuple<std::vector<float>, std::vector<float>, std::vector<uint32_t>, std::vector<uint32_t>, tmap::GraphProperties> returned = tmap::LayoutFromEdgeList(number_of_nodes, edges, config, false, true);
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
        result[i] = new float[numCols+1];
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
