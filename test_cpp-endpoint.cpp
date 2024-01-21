#include <iostream>
#include "LayoutFromEdgeList_internal.hpp"

int main() {
	int * sources = new int[6] { 0, 0, 1, 3, 3, 4 };
	int * destinations = new int[6] { 2, 4, 0, 0, 2, 3 };
	float * weights = new float[6] { 0.91, 0.19, 0.89, 0.64, 0.67, 0.01 };


	auto res = LayoutFromEdgeList_internals(5, sources, destinations, weights, 6);


	// for (auto& row : res)
	// {
	// 	for (auto& column : row)
	// 	{
	// 		std::cout << column << " ";
	// 	}
        //         std::cout << endl;
	// }

        // std::cout << res << std::endl;
	std::cout << res[0][0] << std::endl;
        std::cout << res[0][1] << std::endl;
        std::cout << res[0][2] << std::endl;

	return 0;
}
