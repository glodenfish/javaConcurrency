package com.functional;

import java.util.Arrays;

public class Test {

  public static void main(String[] args) {
    int[] arr = {1, 2, 3, 5, 7, 8, 9};
    Arrays.stream(arr).forEach(System.out::println);
    Arrays.stream(arr).map(x -> x=x+1).forEach(System.out::println);
    Arrays.stream(arr).forEach(System.out::println);

  }

}
