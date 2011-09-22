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

import vanilla.java.collections.HugeArrayBuilder;
import vanilla.java.collections.api.*;
import vanilla.java.collections.model.FieldModel;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public abstract class AbstractHugeArrayList<T, TA extends HugeAllocation, TE extends AbstractHugeElement<T, TA>> extends AbstractHugeContainer<T, TA> implements HugeArrayList<T> {
  protected final List<TE> elements = new ArrayList<TE>();
  protected final List<T> impls = new ArrayList<T>();

  public AbstractHugeArrayList(HugeArrayBuilder hab) {
    super(hab);
  }

  @Override
  public T get(long n) throws IndexOutOfBoundsException {
    return (T) acquireElement(n);
  }

  TE acquireElement(long n) {
    if (elements.isEmpty())
      return createElement(n);
    TE mte = elements.remove(elements.size() - 1);
    mte.index(n);
    return mte;
  }

  protected abstract TE createElement(long n);

  @Override
  public HugeIterator<T> iterator() {
    return listIterator();
  }

  @Override
  public HugeListIterator<T> listIterator() {
    return new HugeListIteratorImpl<T, TA, TE>(this);
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    HugeListIterator<T> iterator = listIterator();
    iterator.index(index);
    return iterator;
  }

  @Override
  public void recycle(Object t) {
    if (!(t instanceof HugeElement)) return;
    switch (((HugeElement) t).hugeElementType()) {
      case Element:
        if (elements.size() < allocationSize)
          elements.add((TE) t);
        break;
      case BeanImpl:
        if (impls.size() < allocationSize)
          impls.add((T) t);
        break;
    }
  }

  @Override
  public T get(int index) {
    return get(index & 0xFFFFFFFFL);
  }

  @Override
  public T set(int index, T element) {
    return set((long) index, element);
  }

  @Override
  public T set(long index, T element) throws IndexOutOfBoundsException {
    if (index > longSize) throw new IndexOutOfBoundsException();
    if (index == longSize) longSize++;
    ensureCapacity(longSize);
    if (setRemoveReturnsNull) {
      final T t = get(index);
      ((HugeElement<T>) t).copyOf(element);
      return null;
    }
    final T t0 = acquireImpl();
    final T t = get(index);
    ((HugeElement<T>) t0).copyOf(t);
    ((HugeElement<T>) t).copyOf(element);
    recycle(t);
    return t0;
  }

  @Override
  public boolean add(T t) {
    set(longSize(), t);
    return true;
  }

  @Override
  public void add(int index, T element) {
    add((long) index, element);
  }

  public void add(long index, T element) {
    if (index != size())
      throw new UnsupportedOperationException();
    set(index, element);
  }

  @Override
  public T remove(int index) {
    return remove((long) index);
  }

  @Override
  public T remove(long index) {
    if (index > longSize) throw new IndexOutOfBoundsException();
    if (setRemoveReturnsNull) {
      final T t = get(index);
      if (index < longSize - 1) {
        final T t2 = get(index);
        ((HugeElement) t).copyOf(t2);
        recycle(t2);
      }
      recycle(t);
      longSize--;
      return null;
    }
    T impl = acquireImpl();
    final T t = get(index);
    ((HugeElement<T>) impl).copyOf(t);
    if (index < longSize - 1) {
      final T t2 = get(index);
      ((HugeElement) t).copyOf(t2);
      recycle(t2);
    }
    recycle(t);
    longSize--;
    return impl;
  }

  protected T acquireImpl() {
    if (impls.isEmpty())
      return createImpl();
    return impls.remove(impls.size() - 1);
  }

  protected abstract T createImpl();

  public void flush() throws IOException {
    for (MappedFileChannel mfc : mfChannels) {
      try {
        mfc.flush();
      } catch (IOException ignored) {
      }
    }
    for (Field f : getClass().getDeclaredFields()) {
      if (!FieldModel.class.isAssignableFrom(f.getType())) continue;
      f.setAccessible(true);
      try {
        FieldModel fm = (FieldModel) f.get(this);
        fm.flush();
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }
  }

  public void close() throws IOException {
    flush();
    for (MappedFileChannel mfc : mfChannels) {
      try {
        mfc.close();
      } catch (IOException ignored) {
      }
    }
  }
  // imported from AbstractList

  public List<T> subList(int fromIndex, int toIndex) {
    return new SubList(fromIndex, toIndex);
  }

  @Override
  public boolean contains(Object o) {
    return indexOf(o) >= 0;
  }

  @Override
  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    final int index = indexOf(o);
    if (index >= 0) {
      remove(index);
      return true;
    }
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    List list = new ArrayList();
    for (Object o : c) {
      if (list.contains(o)) continue;
      list.add(o);
    }
    for (T t : this) {
      if (list.remove(t) && list.isEmpty()) return true;
    }
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    boolean ret = false;
    for (T t : c) {
      ret |= add(t);
    }
    return ret;
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    boolean b = false;
    for (Iterator<T> iterator = this.iterator(); iterator.hasNext(); ) {
      T t = iterator.next();
      if (c.contains(t)) {
        iterator.remove();
        b = true;
      }
    }
    return b;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    boolean b = false;
    for (Iterator<T> iterator = this.iterator(); iterator.hasNext(); ) {
      T t = iterator.next();
      if (!c.contains(t)) {
        iterator.remove();
        b = true;
      }
    }
    return b;
  }

  @Override
  public int indexOf(Object o) {
    TE te = acquireElement(0);
    try {
      for (long i = 0; i <= Integer.MAX_VALUE; i++) {
        te.index = i;
        if (te.equals(o)) return (int) i;
      }
      return -1;
    } finally {
      recycle((T) te);
    }
  }

  @Override
  public int lastIndexOf(Object o) {
    TE te = acquireElement(0);
    try {
      for (int i = (int) Math.min(Long.MAX_VALUE, longSize() - 1); i >= 0; i++) {
        te.index = i;
        if (te.equals(o)) return i;
      }
      return -1;
    } finally {
      recycle((T) te);
    }
  }

  class SubList extends AbstractList<T> {
    private int offset;
    private int size;

    SubList(int fromIndex, int toIndex) {
      if (fromIndex < 0)
        throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
      if (toIndex > size())
        throw new IndexOutOfBoundsException("toIndex = " + toIndex);
      if (fromIndex > toIndex)
        throw new IllegalArgumentException("fromIndex(" + fromIndex +
                                               ") > toIndex(" + toIndex + ")");
      offset = fromIndex;
      size = toIndex - fromIndex;
    }

    public T set(int index, T element) {
      rangeCheck(index);
      return AbstractHugeArrayList.this.set(index + offset, element);
    }

    public T get(int index) {
      rangeCheck(index);
      return AbstractHugeArrayList.this.get(index + offset);
    }

    public int size() {
      return size;
    }

    public void add(int index, T element) {
      if (index < 0 || index > size)
        throw new IndexOutOfBoundsException();
      AbstractHugeArrayList.this.add(index + offset, element);
      size++;
    }

    public T remove(int index) {
      rangeCheck(index);
      T result = AbstractHugeArrayList.this.remove(index + offset);
      size--;
      return result;
    }

    protected void removeRange(int fromIndex, int toIndex) {
      for (int i = fromIndex; i < toIndex; i++)
        AbstractHugeArrayList.this.remove(i);
      size -= (toIndex - fromIndex);
      modCount++;
    }

    public boolean addAll(Collection<? extends T> c) {
      return addAll(size, c);
    }

    public boolean addAll(int index, Collection<? extends T> c) {
      if (index < 0 || index > size)
        throw new IndexOutOfBoundsException(
                                               "Index: " + index + ", Size: " + size);
      int cSize = c.size();
      if (cSize == 0)
        return false;

      AbstractHugeArrayList.this.addAll(offset + index, c);
      size += cSize;
      modCount++;
      return true;
    }

    public Iterator<T> iterator() {
      return listIterator();
    }

    public ListIterator<T> listIterator(final int index) {
      if (index < 0 || index > size)
        throw new IndexOutOfBoundsException(
                                               "Index: " + index + ", Size: " + size);

      return new ListIterator<T>() {
        private ListIterator<T> i = AbstractHugeArrayList.this.listIterator(index + offset);

        public boolean hasNext() {
          return nextIndex() < size;
        }

        public T next() {
          if (hasNext())
            return i.next();
          else
            throw new NoSuchElementException();
        }

        public boolean hasPrevious() {
          return previousIndex() >= 0;
        }

        public T previous() {
          if (hasPrevious())
            return i.previous();
          else
            throw new NoSuchElementException();
        }

        public int nextIndex() {
          return i.nextIndex() - offset;
        }

        public int previousIndex() {
          return i.previousIndex() - offset;
        }

        public void remove() {
          i.remove();
          size--;
          modCount++;
        }

        public void set(T e) {
          i.set(e);
        }

        public void add(T e) {
          i.add(e);
          size++;
          modCount++;
        }
      };
    }

    public List<T> subList(int fromIndex, int toIndex) {
      return new SubList(fromIndex, toIndex);
    }

    void rangeCheck(int index) {
      if (index < 0 || index >= size)
        throw new IndexOutOfBoundsException("Index: " + index +
                                                ",Size: " + size);
    }
  }
}
