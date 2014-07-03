'''
A simple cache that uses the Clock algorithm to evict older entries.
@author: dmontauk
'''

class ClockCache(object):

  def __init__(self, max_size):
    self.max_size = max_size
    self.clock = [CachedObject(False, None, None) for unused_x in xrange(max_size)]
    self.hand = 0
    self.map = {}

  def __IncrementHand(self):
    self.hand += 1
    if self.hand >= self.max_size:
      self.hand = 0

  def put(self, key, value):
    """Puts the given key/value pair into our cache. 
    
    This method finds the next empty space in the cache, or the first space that hasn't been
    recently used. As the "hand" moves around the "clock" looking for a space it sets the 
    "recently_used" bit to false.
    
    Note that new objects in the cache are considered "cold"; i.e. their recently_used bit is False
    until get() is called on them.
    """
    cached_obj = self.map.get(key)
    if cached_obj is not None:
      cached_obj.value = value
      return
    # Not in our cache; we need to add the key/value pair
    obj_at_hand = self.clock[self.hand]
    while obj_at_hand.value is not None and obj_at_hand.recently_used is True:
      obj_at_hand.recently_used = False
      self.__IncrementHand()
      obj_at_hand = self.clock[self.hand]
    # We now have an position in the cache we can overwrite. First, remove the object from the map,
    # if it's in there.
    if obj_at_hand.key is not None:
      self.map.pop(obj_at_hand.key)
    obj_at_hand.key = key
    obj_at_hand.value = value
    self.map[key] = obj_at_hand
    self.__IncrementHand()

  def get(self, key):
    obj = self.map.get(key)
    if obj is not None:
      obj.recently_used = True
      return obj.value
    return None

class CachedObject(object):
  def __init__(self, recently_used, key, value):
    self.recently_used = recently_used
    self.key = key
    self.value = value
  def __repr__(self):
    return "CachedObject[recently_used=%s key=%s value=%s" % (
        self.recently_used, self.key, self.value)
