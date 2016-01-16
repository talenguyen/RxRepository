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

  @Test public void testCompare_SameSizeAndSameStartIndex_ExpectEqual() throws Exception {
    final List<String> list1 = generate(5, 0);
    final List<String> list2 = generate(5, 0);
    Assert.assertTrue(comparator.isSame(list1, list2));
  }

  @Test public void testCompare_DiffSizeAndSameStartIndex_ExpectEqual() throws Exception {
    final List<String> list1 = generate(5, 0);
    final List<String> list2 = generate(6, 0);
    Assert.assertTrue(comparator.isSame(list1, list2));
  }

  @Test public void testCompare_SameSizeAndDiffStartIndex_ExpectDiff() throws Exception {
    final List<String> list1 = generate(5, 1);
    final List<String> list2 = generate(5, 0);
    Assert.assertFalse(comparator.isSame(list1, list2));
  }

  @Test public void testCompare_DiffSizeAndDiffStartIndex_ExpectDiff() throws Exception {
    final List<String> list1 = generate(5, 0);
    final List<String> list2 = generate(6, 1);
    Assert.assertFalse(comparator.isSame(list1, list2));
  }

 List<String> generate(int size, int startIndex) {
    int endIndex = startIndex + size;
    final List<String> result = new ArrayList<>(size);
    for (int i = startIndex; i < endIndex; i++) {
      result.add("Item: " + i);
    }
    return result;
  }
}