import unittest
import clock_cache

class ClockCacheTest(unittest.TestCase):

  def setUp(self):
    self.cache = clock_cache.ClockCache(10)

  def testSinglePutAndGet(self):
    self.cache.put(1, "one")
    self.assertEqual("one", self.cache.get(1));

  def testOverflow(self):
    """Test that adding an element to the cache when it's already full kicks out another element.
    """
    for x in xrange(10):
      self.cache.put(x, str(x))
    # We should still have all our values in the cache, since we haven't overflowed it yet.
    for x in xrange(10):
      self.assertEquals(str(x), self.cache.get(x))
    # Now overflow the cache.
    self.cache.put(-1, "-1")
    self.assertEquals("-1", self.cache.get(-1))
    # We should have kicked out the first element in the cache since we're just looping
    self.assertIsNone(self.cache.get(0));

  def testGetResetsItem(self):
    """Constantly access a single element in the cache and verify that it never gets removed,
    despite many cache overflows.
    """
    # Small cache to make the test simpler
    self.cache = clock_cache.ClockCache(3)
    for x in xrange(100):
      self.cache.put(x, x)
      self.assertIsNotNone(self.cache.get(0))

if __name__ == "__main__":
    # import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
