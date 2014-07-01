package com.voyager.practice.caches.lru;

import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A simple least-recently-used cache. This is practice code; writing this without looking at any
 * documentation or algorithms.
 * 
 * This class is not thread safe.
 * 
 * Possible improvements:
 *  1. We currently evict the most recently-inserted object in case of overflow. That's probably
 *  not the best decision.
 *  2. Make it thread-safe?
 * @author dmontauk
 */

public class LRUCache<K, V> {
  
  static class Node<K, V> {
    K key;
    V value;
    Node<K, V> prev;
    Node<K, V> next;
    public Node(K key, V value, Node<K, V> prev, Node<K, V> next) {
      this.key = key;
      this.value = value;
      this.prev = prev;
      this.next = next;
    }
    
    @Override
    public String toString() {
      String prev_str = "";
      if (prev != null) {
        prev_str = prev.key + " -> ";
      }
      String next_str = "";
      if (next != null) {
        next_str = " -> " + next.key;
      }
      return prev_str + "Node [key=" + key + " value=" + value + "]" + next_str;
    }
  }
  
  final private Map<K, Node<K, V>> map;
  int max_size;
  private Node<K, V> first;
  private Node<K, V> last;
  
  public LRUCache(int max_size) {
    this.max_size = max_size;
    map = Maps.newHashMapWithExpectedSize(max_size);
  }
  
  public void put(K key, V value) {
    if (map.get(key) != null) {
      // Don't add an existing value to our LRU.
      return;
    }
    if (map.size() < max_size) {
      // Add the value to our map and append to the linked list, since we still have space in the
      // cache.
      Node<K, V> node = new Node<K, V>(key, value, last, null);
      // Special case for the first value we put into our cache.
      if (first == null) {
        first = node;
      } else {
        last.next = node;
      }
      last = node;
      map.put(key, node);
      return;
    }
    // Reset the last node to the new value
    map.remove(last.key);
    last.key = key;
    last.value = value;
    map.put(key, last);
  }
  
  public V get(K key) {
    Node<K, V> node = map.get(key);
    if (node != null) {
      if (first == node) {
        // Special case of already-first-node.
        return node.value;
      }
      Node<K, V> prev = node.prev;
      Node<K, V> next = node.next;
      // Mend the "hole" in our linked list.
      if (prev != null) {
        prev.next = next;
      }
      if (next != null) {
        next.prev = prev;
      }
      // Put the node at the beginning of our linked list since it was just accessed.
      node.prev = null;
      node.next = first;
      first.prev = node;
      first = node;
      if (last == node) {
        // Special case of moving a last-node first.
        last = prev;
      }

      return node.value;
    }
    return null;
  }

  public void resize(int new_size) {
    Preconditions.checkArgument(new_size > 0, "Size must be > 0.");
    if (max_size > new_size) {
      Node<K, V> node = last;
      // TODO(dmontauk): calling map.size() in a loop may be inefficient - need to check.
      while (node != null && map.size() > new_size) {
        map.remove(node.key);
        // Remove all the pointers just to be safe.
        if (node.prev != null) {
          node.prev.next = null;
        }
        Node<K, V> prev_node = node.prev;
        node.prev = null;
        node = prev_node;
      }
    }
    max_size = new_size;
  }
  
  public int size() {
    return map.size();
  }
  
  @Override
  public String toString() {
    List<String> nodes = Lists.newArrayList();
    Node<K, V> node = first;
    while (node != null) {
      nodes.add(node.toString());
      node = node.next;
    }
    return Joiner.on("|").join(nodes);
  }
}
