/**
 * Determines whether a given number is palindromic.
 *
 * A number n = a1a2a3,...,aN is said to be palindromic if 
 * for all I in {1,...,N}: aI = a(N+1-I).
 *
 * Written by Daniel HillerstrÃ¶m
 */
#include "io.h"

void main() {
  int i; // temporary (pointer) variable
  int n; // The number in question
  int reverse; // the reverse of the number

  // Read an integer from stdin
  print_s("Enter integer> "); n = read_i();

  // Initial reverse; and pointer variable
  reverse = 0;
  i = n;

  while (i != 0) {
    reverse = (reverse * 10) + (i % 10);
    i       = i / 10;
  }

  print_i(n);
  if (n == reverse) print_s(" is palindromic.\n");
  else print_s(" is not palindromic.\n");
}
