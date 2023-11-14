#include <ogdf/basic/Math.h>
#include <layout.hh>
#include <iostream>

int ogdf_binomial(int n, int k) {
  return ogdf::Math::binomial(n, k);
}

void tmap_test() {
  auto gp = tmap::GraphProperties();
  std::cout << gp.mst_weight << std::endl;
}
