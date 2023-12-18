#include <iostream>
#include "LayoutFromEdgeList_internal.hpp"

int main() {
	int * sources = new int[5] { 1, 2, 3, 4, 1 };
	int * destinations = new int[5] { 2, 3, 4, 1, 3 };
	float * weights = new float[5] { 0.5, 1.0, 0.5, 1.0, 1.2 };


	// auto res = LayoutFromEdgeList_internals(4, sources, destinations, weights, 5);


	// for (auto& row : res)
	// {
	// 	for (auto& column : row)
	// 	{
	// 		std::cout << column << " ";
	// 	}
        //         std::cout << endl;
	// }

        // std::cout << res << std::endl;

	return 0;
}
