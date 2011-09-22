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

import vanilla.java.collections.api.HugeArrayList;
import vanilla.java.collections.api.HugeIterator;
import vanilla.java.collections.model.FieldModel;
import vanilla.java.collections.model.MethodModel;
import vanilla.java.collections.model.TypeModel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ColumnHugeArrayList<T> extends AbstractList<T> implements HugeArrayList<T> {
  private final TypeModel<T> type;
  private final int allocationSize;
  private final List<Object[]> allocations = new ArrayList();
  private final List<T> proxies = new ArrayList();
  private long longSize = 0;

  public ColumnHugeArrayList(TypeModel<T> type, int allocationSize, long capacity) {
    this.type = type;
    this.allocationSize = allocationSize;
    ensureCapacity(capacity);
  }

  public void ensureCapacity(long size) {
    long blocks = (size + allocationSize - 1) / allocationSize;
    int fieldCount = type.fields().length;
    while (blocks > allocations.size()) {
      Object[] allocation = new Object[fieldCount];
      for (int i = 0; i < fieldCount; i++)
        allocation[i] = type.arrayOfField(i, allocationSize);
      allocations.add(allocation);
    }
  }

  @Override
  public int size() {
    return longSize < Integer.MAX_VALUE ? (int) longSize : Integer.MAX_VALUE;
  }

  @Override
  public long longSize() {
    return longSize;
  }

  @Override
  public void setSize(long length) {
    ensureCapacity(length);
    longSize = length;
  }

  @Override
  public T get(final long n) throws IndexOutOfBoundsException {
    if (n < 0 || n >= longSize) throw new IndexOutOfBoundsException();
    return acquireProxy(n);
  }

  @Override
  public T set(long n, T t) throws IndexOutOfBoundsException {
    throw new UnsupportedOperationException();
  }

  @Override
  public HugeIterator<T> iterator() {
    return listIterator();
  }

  @Override
  public HugeIterator<T> listIterator() {
    return new MyHugeIterator();
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    HugeIterator<T> iterator = listIterator();
    iterator.index(index);
    return iterator;
  }

  @Override
  public void recycle(Object t) {
    if (!(t instanceof Proxy)) return;
    proxies.add((T) t);
  }

  @Override
  public T get(int index) {
    return get(index & 0xFFFFFFFFL);
  }

  @Override
  public T remove(long n) throws IndexOutOfBoundsException {
    throw new UnsupportedOperationException();
  }

  private T acquireProxy(long n) {
    if (proxies.isEmpty()) {
      MyInvocationHandler<T> h = new MyInvocationHandler<T>(this, n);
      T ret = (T) Proxy.newProxyInstance(type.classLoader(), new Class[]{type.type()}, h);
      h.proxy = ret;
      return ret;
    }
    return proxies.remove(proxies.size() - 1);
  }

  static class MyInvocationHandler<T> implements InvocationHandler {
    private final ColumnHugeArrayList array;
    private long n;
    T proxy;

    MyInvocationHandler(ColumnHugeArrayList array, long n) {
      this.array = array;
      this.n = n;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.getDeclaringClass() == Object.class) {
        if ("getClass".equals(method.getName()))
          return array.type.type();
        else
          return method.invoke(this, args);
      }
      MethodModel hm = array.type.method(method);
      switch (hm.methodType()) {
        case SETTER:
          array.set(n, hm.fieldModel(), args[0]);
          return proxy;
        case GETTER:
          return array.get(n, hm.fieldModel());
        default:
          throw new UnsupportedOperationException();
      }
    }
  }

  Object get(long index, FieldModel fieldModel) {
    int block = (int) (index / allocationSize);
    int portion = (int) (index % allocationSize);
    return fieldModel.getAllocation(allocations.get(block), portion);
  }

  void set(long index, FieldModel fieldModel, Object value) {
    int block = (int) (index / allocationSize);
    int portion = (int) (index % allocationSize);
    fieldModel.setAllocation(allocations.get(block), portion, value);
  }

  class MyHugeIterator implements HugeIterator<T> {
    private final T proxy;
    private final MyInvocationHandler<T> handler;

    MyHugeIterator() {
      proxy = acquireProxy(-1);
      handler = (MyInvocationHandler<T>) Proxy.getInvocationHandler(proxy);
    }

    @Override
    public HugeIterator<T> toStart() {
      handler.n = -1;
      return this;
    }

    @Override
    public HugeIterator<T> toEnd() {
      handler.n = longSize();
      return this;
    }

    @Override
    public HugeIterator<T> index(long n) {
      return this;
    }

    @Override
    public long previousLongIndex() {
      return handler.n - 1;
    }

    @Override
    public long nextLongIndex() {
      return handler.n + 1;
    }

    @Override
    public void recycle() {
    }

    @Override
    public boolean hasNext() {
      return handler.n < longSize() - 1;
    }

    @Override
    public T next() {
      handler.n++;
      return proxy;
    }

    @Override
    public boolean hasPrevious() {
      return false;
    }

    @Override
    public T previous() {
      handler.n--;
      return proxy;
    }

    @Override
    public int nextIndex() {
      return (int) nextLongIndex();
    }

    @Override
    public int previousIndex() {
      return (int) previousLongIndex();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(T t) {
      for (FieldModel field : type.fields()) {
        try {
          field.setter().invoke(proxy, field.getter().invoke(t));
        } catch (IllegalAccessException e) {
          throw new AssertionError(e);
        } catch (InvocationTargetException e) {
          throw new AssertionError(e.getCause());
        }
      }
    }

    @Override
    public void add(T t) {
      if (handler.n != longSize())
        throw new UnsupportedOperationException();
      ensureCapacity(++longSize);
      set(t);
      handler.n++;
    }
  }

  @Override
  public void flush() {
  }

  @Override
  public void close() {
  }

  @Override
  public void compact() {
    int allocationsNeeded = (int) (longSize() / allocationSize + 1);
    while (allocations.size() > allocationsNeeded) {
      allocations.remove(allocations.size() - 1);
    }
  }
}
