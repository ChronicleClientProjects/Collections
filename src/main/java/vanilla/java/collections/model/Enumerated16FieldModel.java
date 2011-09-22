package vanilla.java.collections.model;

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

import vanilla.java.collections.impl.MappedFileChannel;

import java.io.*;
import java.nio.CharBuffer;
import java.util.*;

public class Enumerated16FieldModel<T> extends AbstractFieldModel<T> {
  private static final String NULL_STRING = "\0\0";
  private final Class<T> type;
  private final Map<T, Character> map = new LinkedHashMap<T, Character>();
  private final List<T> list = new ArrayList<T>();
  private int addPosition;

  public Enumerated16FieldModel(String fieldName, int fieldNumber, Class<T> type) {
    super(fieldName, fieldNumber);
    this.type = type;
    clear();
  }

  @Override
  public Object arrayOfField(int size) {
    return newArrayOfField(size, null);
  }

  @Override
  public int sizeOf(int elements) {
    return sizeOf0(elements);
  }

  private static int sizeOf0(int elements) {
    return elements * 2;
  }

  public static CharBuffer newArrayOfField(int size, MappedFileChannel mfc) {
    return acquireByteBuffer(mfc, sizeOf0(size)).asCharBuffer();
  }

  @Override
  public Class storeType() {
    return CharBuffer.class;
  }

  @Override
  public T getAllocation(Object[] arrays, int index) {
    CharBuffer array = (CharBuffer) arrays[fieldNumber];
    return get(array, index);
  }

  public T get(CharBuffer array, int index) {
    final char c = array.get(index);
    return list.get(c);
  }

  @Override
  public void setAllocation(Object[] arrays, int index, T value) {
    CharBuffer array = (CharBuffer) arrays[fieldNumber];
    set(array, index, value);
  }

  public void set(CharBuffer array, int index, T value) {
    Character ordinal = map.get(value);
    if (ordinal == null) {
      final int size = map.size();
      OUTER:
      do {
        for (; addPosition < map.size(); addPosition++) {
          if (list.get(addPosition) == null) {
            ordinal = addEnumValue(value, addPosition);
            break OUTER;
          }
        }
        ordinal = addEnumValue(value, size);
      } while (false);
      addPosition++;
    }
    array.put(index, ordinal);
  }

  private Character addEnumValue(T value, int position) {
    char ordinal = (char) position;
    if (ordinal != position)
      throw new IndexOutOfBoundsException("Too many values in Enumerated16 field, try calling compact()");
    map.put(value, ordinal);
    if (ordinal == list.size())
      list.add(ordinal, value);
    else
      list.set(ordinal, value);
    return ordinal;
  }

  @Override
  public Class<T> type() {
    return type;
  }

  @Override
  public BCType bcType() {
    return BCType.Reference;
  }

  @Override
  public String bcLSetType() {
    return "Ljava/lang/Object;";
  }

  @Override
  public String bcLStoredType() {
    return "C";
  }

  @Override
  public boolean virtualGetSet() {
    return true;
  }

  @Override
  public boolean isCallsNotEquals() {
    return true;
  }

  @UsedFromByteCode
  public static <T> boolean notEquals(T t1, T t2) {
    return t1 == null ? t2 != null : !t1.equals(t2);
  }

  @UsedFromByteCode
  public static <T> int hashCode(T elementType) {
    return elementType == null ? Integer.MIN_VALUE : elementType.hashCode();
  }

  public void clear() {
    map.clear();
    list.clear();
    map.put(null, (char) 0);
    list.add(null);
    addPosition = 1;
  }

  private final BitSet compactIndexUsed = new BitSet();


  public void compactStart() {
    compactIndexUsed.clear();
  }

  public void compactScan(CharBuffer charBuffer, long size) {
    for (int i = 0; i < size; i++) {
      final char ch = charBuffer.get(i);
      compactIndexUsed.set(ch);
    }
  }

  public void compactEnd() {
    final int compactSize = compactIndexUsed.cardinality();
    if (compactSize == map.size()) {
      return;
    }
    for (int i = 1; i < list.size(); i++) {
      if (compactIndexUsed.get(i)) continue;
      // to be removed
      T t = list.get(i);
      list.set(i, null);
      map.remove(t);
      if (addPosition > i)
        addPosition = i;
    }
  }

  public Map<T, Character> map() {
    return map;
  }

  public List<T> list() {
    return list;
  }

  @Override
  public boolean isCompacting() {
    return true;
  }

  @Override
  public short equalsPreference() {
    return 15;
  }

  public static <T> void write(ObjectOutput out, Class<T> clazz, T t) throws IOException {
    if (clazz == String.class) {
      String s = (String) t;
      if (s == null) s = "\0";
      out.writeUTF(s);
    } else
      out.writeObject(t);
  }

  public static <T> T read(ObjectInput in, Class<T> clazz) throws IOException, ClassNotFoundException {
    if (clazz == String.class) {
      final String s = in.readUTF();
      if (s.equals(NULL_STRING)) return null;
      return (T) s;
    } else
      return (T) in.readObject();
  }

  @Override
  public void flush() throws IOException {
    super.flush();
    if (baseDirectory() != null) {
      ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName())));
      oos.writeObject(list());
      oos.close();
    }
  }

  @Override
  public void baseDirectory(String baseDirectory) throws IOException {
    super.baseDirectory(baseDirectory);
    if (baseDirectory != null) {
      try {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName())));
        list().clear();
        list().addAll((List) ois.readObject());
        ois.close();
        map.clear();
        for (T t : list)
          map.put(t, (char) map().size());
      } catch (ClassNotFoundException cnfe) {
        throw new IOException("Unable to load class", cnfe);
      } catch (FileNotFoundException ignored) {
        // ignored
      }
    }
  }

  private String fileName() {
    return baseDirectory() + "/" + fieldName() + "-enum";
  }
}
