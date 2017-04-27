#include "io.h"
// Computes the factorial of n in bottom-up fashion
int factorial(int n) {
  int i;
  int product;

  // Initialise product to 1 (base-case)
  product = 1;
  // Initialise iterator i to 1
  i = 1;

  while (i <= n) {
    product = product * i;
    i = i + 1;
  }
  return product;
}

void main() {
  int i; // Outer iterator
  int j; // Inner iterator
  int n; // Number of rows to compute

  print_s("Enter the number of rows> ");
  n = read_i();

  // Initialise outer iterator to 0
  i = 0;

  // Iterate over the number of rows to display
  while (i < n) {
    // Initialise inner iterator to 0
    j = 0;

    // Display spaces for row i
    while ((n - i) - 2 >= j) {
      print_s(" ");
      j = j + 1; // Increment inner iterator
    }

    // Reset inner iterator to 0
    j = 0;

    // Compute and display the numbers in row i
    while (j <= i) {
      {
	int n; // A single number in the triangle
	int k; // Difference between i and j

	k = i - j;
	n = factorial(i) / (factorial(j) * factorial(k));
	print_i(n);print_s(" ");
      }
      j = j + 1; // Increment inner iterator
    }
    print_s("\n");
    i = i + 1; // Increment outer iterator
  }
}