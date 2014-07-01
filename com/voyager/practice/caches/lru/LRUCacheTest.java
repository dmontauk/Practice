package com.voyager.practice.caches.lru;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LRUCacheTest {
  
  private LRUCache<Integer, String> cache;

  @Before
  public void setUp() {
    cache = new LRUCache<Integer, String>(10);
  }

  @Test
  public void testSimpleInsertAndGet() {
    cache.put(1, "one");
    assertEquals("one", cache.get(1));
    assertEquals(1, cache.size());
    
    cache.put(2, "two");
    assertEquals("two", cache.get(2));
    assertEquals(2, cache.size());
  }
  
  @Test
  public void testMultipleInserts() {
    cache.put(1, "one");
    cache.put(2, "two");
    cache.put(3, "three");
    assertEquals(3, cache.size());
    assertEquals("one", cache.get(1));
    assertEquals("three", cache.get(3));
    assertEquals("two", cache.get(2));
  }
  
  @Test
  public void testOverflowEvictsLast() {
    cache = new LRUCache<Integer, String>(2);
    cache.put(1, "one");
    cache.put(2, "two");
    cache.put(3, "three");
    // two should have been evicted.
    assertEquals("one", cache.get(1));
    assertEquals(null, cache.get(2));
    assertEquals("three", cache.get(3));
  }
  
  @Test
  public void testGetChangesEviction() {
    cache = new LRUCache<Integer, String>(2);
    cache.put(1, "one");
    cache.put(2, "two");
    cache.get(2);
    cache.put(3, "three");
    // one should have been evicted.
    assertEquals(null, cache.get(1));
    assertEquals("two", cache.get(2));
    assertEquals("three", cache.get(3));
  }
  
  @Test
  public void testManyRandomInserts() {
    Random rand = new Random();
    for (int i = 0; i < 10000; i++) {
      int key = rand.nextInt();
      String value = "value" + key;
      cache.put(key, value);
      assertEquals(value, cache.get(key));
    }
    assertEquals(10, cache.size());
  }
  
  @Test
  public void testGrowCache() {
    for (int i = 0; i < 20; i++) {
      String value = "value" + i;
      cache.put(i, value);
    }
    assertEquals(10, cache.size());
    
    cache.resize(20);
    
    // We should still have elements 0-8 and 19.
    assertEquals(10, cache.size());
    for (int i = 0 ; i < 9; i++) {
      assertNotNull(cache.get(i));;
    }
    assertNotNull(cache.get(19));
    
    // Stick another 20 items in and see how we grow
    for (int i = 20; i < 40; i++) {
      String value = "value" + i;
      cache.put(i, value);
    }
    assertEquals(20, cache.size());
  }
  
  @Test
  public void testShrinkCache() {
    for (int i = 0; i < 10; i++) {
      String value = "value" + i;
      cache.put(i, value);
    }
    assertEquals(10, cache.size());
    
    cache.resize(5);

    // We should still have elements 0-4 in the cache.
    assertEquals(5, cache.size());
    for (int i = 0 ; i < 5; i++) {
      assertNotNull(cache.get(i));;
    }
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testShrinkCacheToZero() {
    cache.resize(0);
  }
}
