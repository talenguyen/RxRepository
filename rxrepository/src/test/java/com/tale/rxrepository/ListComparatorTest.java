package com.tale.rxrepository;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RxRepository
 *
 * Created by Giang Nguyen on 12/25/15.
 * Copyright (c) 2015 Umbala. All rights reserved.
 */
public class ListComparatorTest {

  protected ListComparator<String> comparator;

  @Before public void setUp() throws Exception {
    comparator = new ListComparator<>();
  }

  @Test public void testCompareEqual() throws Exception {
    final List<String> list1 = generate(5, 0);
    final List<String> list2 = generate(5, 0);
    final int compareResult = comparator.compare(list1, list2);
    Assert.assertEquals(0, compareResult);
  }

  @Test public void testCompareDiff1() throws Exception {
    final List<String> list1 = generate(5, 0);
    final List<String> list2 = generate(6, 0);
    final int compareResult = comparator.compare(list1, list2);
    Assert.assertTrue(compareResult != 0);
  }

  @Test public void testCompareDiff2() throws Exception {
    final List<String> list1 = generate(6, 0);
    final List<String> list2 = generate(5, 0);
    final int compareResult = comparator.compare(list1, list2);
    Assert.assertTrue(compareResult != 0);
  }

  @Test public void testCompareDiff3() throws Exception {
    final List<String> list1 = generate(5, 1);
    final List<String> list2 = generate(5, 0);
    final int compareResult = comparator.compare(list1, list2);
    Assert.assertTrue(compareResult != 0);
  }

  private List<String> generate(int size, int startIndex) {
    int endIndex = startIndex + size;
    final List<String> result = new ArrayList<>(size);
    for (int i = startIndex; i < endIndex; i++) {
      result.add("Item: " + i);
    }
    return result;
  }
}