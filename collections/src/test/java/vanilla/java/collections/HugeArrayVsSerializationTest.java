package vanilla.java.collections;

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

import org.junit.Test;
import vanilla.java.collections.api.HugeArrayList;

import java.io.*;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;

import static vanilla.java.collections.HugeArrayBuilderTest.gcPrintUsed;
import static vanilla.java.collections.HugeArrayBuilderTest.printUsed;

public class HugeArrayVsSerializationTest {
  static final int length = 1000 * 1000;

  static {
    gcPrintUsed();
  }

  @Test
  public void testSearchAndUpdateCollection() {
    gcPrintUsed();
    int repeats = 1000; // five seconds.
    HugeArrayBuilder<MutableTypes> mtb = new HugeArrayBuilder<MutableTypes>() {
    };
    HugeArrayList<MutableTypes> mts = mtb.create();
    mts.setSize(length);
    for (MutableTypes mt : mts) {
      mt.setBoolean2(true);
      mt.setByte2((byte) 1);
    }
    gcPrintUsed();

    long start = System.nanoTime();
    for (int i = 0; i < repeats; i++) {
      for (MutableTypes mt : mts) {
        mt.setInt(mt.getInt() + 1);
      }
    }
    long time = System.nanoTime() - start;
    printUsed();
    System.out.printf("Huge Collection update one field, took an average %.1f ns.%n", (double) time / length / repeats);
  }

  @Test
  public void testSearchAndUpdateCollectionHeap() {
    gcPrintUsed();
    int repeats = 1000; // five seconds.
    int length = 1000 * 1000;
    HugeArrayBuilder<MutableTypes> mtb = new HugeArrayBuilder<MutableTypes>() {
    };
    final List<MutableTypes> mts = new ArrayList<MutableTypes>();
    for (int i = 0; i < length; i++) {
      final MutableTypes bean = mtb.createBean();
      bean.setBoolean2(true);
      bean.setByte2((byte) 1);
      mts.add(bean);
    }

    gcPrintUsed();

    long start = System.nanoTime();
    for (int i = 0; i < repeats; i++) {
      for (MutableTypes mt : mts) {
        mt.setInt(mt.getInt() + 1);
      }
    }
    long time = System.nanoTime() - start;
    printUsed();
    System.out.printf("List<JavaBean>, update one field took an average %.1f ns.%n", (double) time / length / repeats);
  }

  @Test
  public void testSearchAndUpdateCollectionSerialization() throws IOException, ClassNotFoundException {
    gcPrintUsed();
    int length = 1000 * 1000; // about 8 seconds
    List<byte[]> mts = new ArrayList<byte[]>();
    HugeArrayBuilder<MutableTypes> mtb = new HugeArrayBuilder<MutableTypes>() {
    };
    final MutableTypes bean = mtb.createBean();
    bean.setBoolean2(true);
    bean.setByte2((byte) 1);
    byte[] bytes = toBytes(bean);
    for (int i = 0; i < length; i++)
      mts.add(bytes.clone());
    System.out.println("Per object size is " + (4 + (bytes.length + 7 + 12) / 8 * 8));

    gcPrintUsed();
    long start = System.nanoTime();
    for (int i = 0, mtsSize = mts.size(); i < mtsSize; i++) {
      MutableTypes mt = (MutableTypes) fromBytes(mts.get(i));
      mt.setInt(mt.getInt() + 1);
      mts.set(i, toBytes(mt));
    }
    long time = System.nanoTime() - start;
    printUsed();
    System.out.printf("List<byte[]> with readObject/writeObject update one field took an average %,d ns.%n", time / length);
  }

  @Test
  public void testSearchAndUpdateCollectionSerialization2() throws IOException, ClassNotFoundException {
    gcPrintUsed();
    int length = 1000 * 1000; // about 8 seconds
    List<byte[]> mts = new ArrayList<byte[]>();
    final PlainMutableTypes bean = new PlainMutableTypes();
    bean.setBoolean2(true);
    bean.setByte2((byte) 1);
    byte[] bytes = toBytes(bean);
    for (int i = 0; i < length; i++)
      mts.add(bytes.clone());
    System.out.println("Per object size is " + (4 + (bytes.length + 7 + 12) / 8 * 8));

    gcPrintUsed();
    long start = System.nanoTime();
    for (int i = 0, mtsSize = mts.size(); i < mtsSize; i++) {
      MutableTypes mt = (MutableTypes) fromBytes(mts.get(i));
      mt.setInt(mt.getInt() + 1);
      mts.set(i, toBytes(mt));
    }
    long time = System.nanoTime() - start;
    printUsed();
    System.out.printf("List<byte[]> update one field took an average %,d ns.%n", time / length);
  }

  public static byte[] toBytes(Object obj) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(obj);
    oos.close();
    return baos.toByteArray();
  }

  public static Object fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
    return ois.readObject();
  }

  static class PlainMutableTypes implements MutableTypes, Serializable {
    private boolean m_boolean;
    private Boolean m_boolean2;
    private byte m_byte;
    private Byte m_byte2;
    private char m_char;
    private short m_short;
    private int m_int;
    private float m_float;
    private long m_long;
    private double m_double;
    private ElementType m_elementType;
    private String m_string;

    @Override
    public void setBoolean(boolean b) {
      this.m_boolean = b;
    }

    @Override
    public boolean getBoolean() {
      return m_boolean;
    }

    @Override
    public void setBoolean2(Boolean b) {
      this.m_boolean2 = b;
    }

    @Override
    public Boolean getBoolean2() {
      return m_boolean2;
    }

    @Override
    public void setByte(byte b) {
      this.m_byte = b;
    }

    @Override
    public byte getByte() {
      return m_byte;
    }

    @Override
    public void setByte2(Byte b) {
      this.m_byte2 = b;
    }

    @Override
    public Byte getByte2() {
      return m_byte2;
    }

    @Override
    public void setChar(char ch) {
      this.m_char = ch;
    }

    @Override
    public char getChar() {
      return m_char;
    }

    @Override
    public void setShort(short s) {
      this.m_short = s;
    }

    @Override
    public short getShort() {
      return m_short;
    }

    @Override
    public void setInt(int i) {
      this.m_int = i;
    }

    @Override
    public int getInt() {
      return m_int;
    }

    @Override
    public void setFloat(float f) {
      this.m_float = f;
    }

    @Override
    public float getFloat() {
      return m_float;
    }

    @Override
    public void setLong(long l) {
      this.m_long = l;
    }

    @Override
    public long getLong() {
      return m_long;
    }

    @Override
    public void setDouble(double d) {
      this.m_double = d;
    }

    @Override
    public double getDouble() {
      return m_double;
    }

    @Override
    public void setElementType(ElementType elementType) {
      this.m_elementType = elementType;
    }

    @Override
    public ElementType getElementType() {
      return m_elementType;
    }

    @Override
    public void setString(String text) {
      this.m_string = text;
    }

    @Override
    public String getString() {
      return m_string;
    }
  }
}
