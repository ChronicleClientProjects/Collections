package vanilla.java.collections.impl;

/*
 * Copyright 2011 Peter Lawrey
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import vanilla.java.collections.HugeMapBuilder;
import vanilla.java.collections.api.HugeAllocation;
import vanilla.java.collections.api.HugeElement;
import vanilla.java.collections.api.HugeMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.*;

public abstract class AbstractHugeMap<K, KE extends HugeElement<K>, V, VE extends HugeElement<V>, MA extends HugeAllocation> extends AbstractHugeContainer<V, MA> implements HugeMap<K, V> {
  public static final long HASH_MASK = 0xFFFFFFFFFL; // so divide by 32 is positive.
  protected final List<KE> keyElements = new ArrayList<KE>();
  protected final List<VE> valueElements = new ArrayList<VE>();
  protected final List<K> keyImpls = new ArrayList<K>();
  protected final List<V> valueImpls = new ArrayList<V>();
  protected final IntBuffer[] keysBuffers = new IntBuffer[256];
  private final Set<K> keySet = new AHMSet();
  private final Collection<V> values = new AHMValues();
  private final Set<Entry<K, V>> entrySet = new AHMEntrySet();

  protected AbstractHugeMap(HugeMapBuilder<K, V> hmb) {
    super(hmb);
    for (int i = 0; i < keysBuffers.length; i++)
      keysBuffers[i] = ByteBuffer.allocate(512 * 1024).order(ByteOrder.nativeOrder()).asIntBuffer();
    ensureCapacity(1);
  }

  VE acquireValueElement(long n) {
    if (valueElements.isEmpty())
      return createValueElement(n);
    VE mte = valueElements.remove(valueElements.size() - 1);
    mte.index(n);
    return mte;
  }

  protected abstract VE createValueElement(long n);

  KE acquireKeyElement(long n) {
    if (keyElements.isEmpty())
      return createKeyElement(n);
    KE mte = keyElements.remove(keyElements.size() - 1);
    mte.index(n);
    return mte;
  }

  protected abstract KE createKeyElement(long n);

  @Override
  public void recycle(Object o) {
    if (o == null) return;
    switch (((HugeElement) o).hugeElementType()) {
      case Element:
        if (valueElements.size() < allocationSize)
          valueElements.add((VE) o);
        break;
      case BeanImpl:
        if (valueImpls.size() < allocationSize)
          valueImpls.add((V) o);
        break;
      case KeyElement:
        if (keyElements.size() < allocationSize)
          keyElements.add((KE) o);
        break;
      case KeyImpl:
        if (keyImpls.size() < allocationSize)
          keyImpls.add((K) o);
        break;
    }
  }

  protected K acquireKeyImpl() {
    if (valueImpls.isEmpty())
      return createKeyImpl();
    return keyImpls.remove(keyImpls.size() - 1);
  }

  protected abstract K createKeyImpl();

  protected V acquireValueImpl() {
    if (valueImpls.isEmpty())
      return createValueImpl();
    return valueImpls.remove(valueImpls.size() - 1);
  }

  protected abstract V createValueImpl();

  // Map
  protected long indexOf(KE key, boolean free, boolean remove) {
    long hash = key.longHashCode() & HASH_MASK;
    int loHash = (int) (hash % keysBuffers.length);
    int hiHash = (int) (hash / keysBuffers.length);
    final IntBuffer keysBuffer = keysBuffers[loHash];

    KE ke = acquireKeyElement(0);
    try {
      for (int i = 0, len = keysBuffer.limit(); i < len; i++) {
        final int i1 = keysBuffer.get((hiHash + i) % len);
        if (i1 == 0) {
          if (free) {
            final int loc = size();
            ensureCapacity(loc + 1);
            keysBuffer.put((hiHash + i) % len, loc + 1);

            if (keysBuffer.position() >= keysBuffer.limit() * 3 / 4)
              growBuffer(loHash);
            else if (i > 5 && keysBuffer.position() >= keysBuffer.limit() * 2 / 3)
              growBuffer(loHash);
            else if (i > 15 && keysBuffer.position() >= keysBuffer.limit() / 2)
              growBuffer(loHash);
            else
              keysBuffer.position(keysBuffer.position() + 1);
            longSize++;
            return loc;
          }
          return -1;
        }
        ke.index(i1 - 1);
        if (ke.equals(key)) {
          if (remove) {
            keysBuffer.put((hiHash + i) % len, 0);
            // used field.
            keysBuffer.position(keysBuffer.position() - 1);
          }
          return i1 - 1;
        }
      }
      return -1;
    } finally {
      recycle(ke);
    }
  }

  private void growBuffer(int loHash) {
    final IntBuffer buffer1 = keysBuffers[loHash];
    final IntBuffer buffer2 = ByteBuffer.allocate(buffer1.capacity() * 8).order(ByteOrder.nativeOrder()).asIntBuffer();
    keysBuffers[loHash] = buffer2;
    KE ke = acquireKeyElement(0);
    int used = 0;
    OUTER:
    for (int j = 0; j < buffer1.capacity(); j++) {
      int index = buffer1.get(j);
      if (index == 0) continue;
      ke.index(index - 1);

      int hiHash = (int) (ke.longHashCode() & HASH_MASK / keysBuffers.length);
      for (int i = 0, len = buffer2.limit(); i < len; i++) {
        final int i1 = buffer2.get((hiHash + i) % len);
        if (i1 == 0) {
          buffer2.put((hiHash + i) % len, index);
          used++;
          continue OUTER;
        }
      }
    }
    recycle(ke);
    buffer2.position(used);
  }

  @Override
  public boolean containsKey(Object key) {
    return key instanceof HugeElement && indexOf((KE) key, false, false) >= 0;
  }

  @Override
  public boolean containsValue(Object value) {
    for (V v : values())
      if (v.equals(value))
        return true;
    return false;
  }

  @Override
  public V get(Object key) {
    if (!(key instanceof HugeElement)) return null;
    long index = indexOf((KE) key, false, false);
    if (index < 0) return null;
    return (V) acquireValueElement(index);
  }

  @Override
  public V put(K key, V value) {
    VE ve = acquireValueElement(indexOf((KE) key, true, false));
    V v = null;
    if (!setRemoveReturnsNull) {
      v = acquireValueImpl();
      ((HugeElement<V>) v).copyOf((V) ve);
    }
    ve.copyOf(value);
    recycle(ve);
    return v;
  }

  @Override
  public V remove(Object key) {
    if (!(key instanceof HugeElement)) return null;
    long index = indexOf((KE) key, false, true);
    if (index < 0) return null;

    return removeAt(index);
  }

  private V removeAt(long index) {
    VE ve2 = acquireValueElement(index);
    V v = null;
    if (!setRemoveReturnsNull) {
      v = acquireValueImpl();
      ((HugeElement<V>) v).copyOf((V) ve2);
    }
    if (index < longSize() - 1) {
      VE ve1 = acquireValueElement(longSize() - 1);
      ve1.copyOf((V) ve2);
      recycle(ve1);
    }
    recycle(ve2);
    return v;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  public int[] sizes() {
    int[] sizes = new int[keysBuffers.length];
    for (int i = 0; i < keysBuffers.length; i++)
      sizes[i] = keysBuffers[i].position();
    return sizes;
  }

  public int[] capacities() {
    int[] sizes = new int[keysBuffers.length];
    for (int i = 0; i < keysBuffers.length; i++)
      sizes[i] = keysBuffers[i].capacity();
    return sizes;
  }

  @Override
  public Set<K> keySet() {
    return keySet;
  }

  @Override
  public Collection<V> values() {
    return values;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return entrySet;
  }

  private class AHMSet extends AbstractSet<K> {
    @Override
    public Iterator<K> iterator() {
      return new Iterator<K>() {
        final KE ke = acquireKeyElement(-1);

        @Override
        public boolean hasNext() {
          return ke.index() < longSize - 1;
        }

        @Override
        public K next() {
          ke.index(ke.index() + 1);
          return (K) ke;
        }

        @Override
        public void remove() {
          AbstractHugeMap.this.remove(ke);
        }
      };
    }

    @Override
    public int size() {
      return AbstractHugeMap.this.size();
    }
  }

  private class AHMValues extends AbstractCollection<V> {
    @Override
    public Iterator<V> iterator() {
      return new Iterator<V>() {
        final VE ve = acquireValueElement(-1);

        @Override
        public boolean hasNext() {
          return ve.index() < longSize - 1;
        }

        @Override
        public V next() {
          ve.index(ve.index() + 1);
          return (V) ve;
        }

        @Override
        public void remove() {
          AbstractHugeMap.this.remove(acquireKeyElement(ve.index()));
        }
      };
    }

    @Override
    public int size() {
      return AbstractHugeMap.this.size();
    }
  }

  private class AHMEntrySet extends AbstractSet<Entry<K, V>> {
    @Override
    public Iterator<Entry<K, V>> iterator() {
      return new EntryIterator();
    }

    @Override
    public int size() {
      return AbstractHugeMap.this.size();
    }

    class EntryIterator implements Iterator<Entry<K, V>>, Entry<K, V> {
      final KE ke = acquireKeyElement(-1);
      final VE ve = acquireValueElement(-1);

      @Override
      public boolean hasNext() {
        return ve.index() < longSize - 1;
      }

      @Override
      public Entry<K, V> next() {
        final long idx = ke.index() + 1;
        ke.index(idx);
        ve.index(idx);
        return this;
      }

      @Override
      public void remove() {
        AbstractHugeMap.this.remove(ke);
      }

      @Override
      public K getKey() {
        return (K) ke;
      }

      @Override
      public V getValue() {
        return (V) ve;
      }

      @Override
      public V setValue(V value) {
        ve.copyOf(value);
        return null;
      }
    }
  }
}
