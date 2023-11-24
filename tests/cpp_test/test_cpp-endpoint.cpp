#include <stdint.h>
#include <tuple>
#include <vector>
#include <iostream>
#include <LayoutFromEdgeList_internal.cpp>

int main() {
	uint32_t vertex_count = 4;
	int sources[5] = { 1, 2, 3, 4, 1 };
	int destinations[5] = { 2, 3, 4, 1, 3 };
	float weights[5] = { 0.5, 1.0, 0.5, 1.0, 1.2 };


	LayoutFromEdgeList_internals(4, &sources, &destinations, &weights, 5);
	return 0;
}