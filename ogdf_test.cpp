#include "ogdf_based_lib.h"
#include <iostream>

int main() {
  std::cout << "binomial of 10, 2 is " << ogdf_binomial(10, 2) << std::endl; // should output 45
  tmap_test();                  // should output 0
  return 0;
}
