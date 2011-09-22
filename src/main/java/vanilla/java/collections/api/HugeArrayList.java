package vanilla.java.collections.api;

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

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface HugeArrayList<T> extends List<T>, Closeable, HugeContainer {
  public T get(long n) throws IndexOutOfBoundsException;

  public HugeIterator<T> iterator();

  public HugeIterator<T> listIterator();

  public T remove(long n) throws IndexOutOfBoundsException;

  public T set(long n, T t) throws IndexOutOfBoundsException;

  public void flush() throws IOException;

}
