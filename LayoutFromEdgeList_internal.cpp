#include <stdint.h>
#include <tuple>
#include <vector>
#include <iostream>
#include "layout.hh"
#include "LayoutFromEdgeList_internal.hpp"

// Helper function to flatten vectors into a 1D array
float* flattenVectors(const std::vector<float>& vector1, const std::vector<float>& vector2, size_t& result_size) {
    result_size = vector1.size() + vector2.size();
    float* result = new float[result_size];
    size_t index = 0;

    for (float value : vector1) {
        result[index++] = value;
    }
    for (float value : vector2) {
        result[index++] = value;
    }
    return result;
}

float** LayoutFromEdgeList_internals(int number_of_nodes, int* sources, int* destinations, float* weights, int number_of_edges) {

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

    // Flatten the vectors into a 1D array
    size_t result_size;
    float* result = flattenVectors(vector1, vector2, result_size);

    // Now, result is a 1D array containing the flattened vectors
    return reinterpret_cast<float**>(result);
}
