package com.twitter.heron.localmode.grouping;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.twitter.heron.proto.system.HeronTuples;

import junit.framework.Assert;

public class ShuffleGroupingTest {

  @Before
  public void before() throws Exception {
  }

  @After
  public void after() throws Exception {
  }

  /**
   * Method: test round robin nature of shuffle grouping
   */
  @Test
  public void testRoundRobin() throws Exception {
    List<Integer> task_ids = new LinkedList<>();
    task_ids.add(0);
    task_ids.add(2);
    task_ids.add(4);
    task_ids.add(8);

    ShuffleGrouping g = new ShuffleGrouping(task_ids);
    HeronTuples.HeronDataTuple dummy = HeronTuples.HeronDataTuple.getDefaultInstance();
    List<Integer> dest = g.getListToSend(dummy);

    Assert.assertEquals(dest.size(), 1);
    int first = dest.get(0);
    int index = -1;
    for (int i = 0; i < task_ids.size(); ++i) {
      if (task_ids.get(i) == first) {
        index = i;
        break;
      }
    }
    dest.clear();

    for (int i = 0; i < 100; ++i) {
      dest = g.getListToSend(dummy);

      Assert.assertEquals(dest.size(), 1);
      int d = dest.get(0);
      index = (index + 1) % task_ids.size();
      Assert.assertEquals((Integer) d, task_ids.get(index));
      dest.clear();
    }
  }

  /**
   * Method: test random start
   */
  @Test
  public void testRandomStart() throws Exception {
    List<Integer> task_ids = new LinkedList<>();
    task_ids.add(0);
    task_ids.add(1);

    int zeros = 0;
    int ones = 0;
    int count = 1000;

    for (int i = 0; i < count; ++i) {
      ShuffleGrouping g = new ShuffleGrouping(task_ids);
      HeronTuples.HeronDataTuple dummy = HeronTuples.HeronDataTuple.getDefaultInstance();
      List<Integer> dest = g.getListToSend(dummy);

      Assert.assertEquals(dest.size(), 1);
      int first = dest.get(0);
      if (first == 0) {
        zeros++;
      } else {
        ones++;
      }
      dest.clear();
    }

    double variance = ((double) Math.abs(zeros - ones)) / count;
    Assert.assertTrue(variance <= 0.1);
  }
}