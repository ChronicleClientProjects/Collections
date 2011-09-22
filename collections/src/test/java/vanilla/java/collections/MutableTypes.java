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

import vanilla.java.collections.api.Id;

import java.lang.annotation.ElementType;

public interface MutableTypes {
  public void setInt(int i);

  @Id
  public int getInt();

  public void setBoolean(boolean b);

  public boolean getBoolean();

  public void setBoolean2(Boolean b);

  public Boolean getBoolean2();

  public void setByte(byte b);

  public byte getByte();

  public void setByte2(Byte b);

  public Byte getByte2();

  public void setChar(char ch);

  public char getChar();

  public void setShort(short s);

  public short getShort();

  public void setFloat(float f);

  public float getFloat();

  public void setLong(long l);

  public long getLong();

  public void setDouble(double d);

  public double getDouble();

  public void setElementType(ElementType elementType);

  public ElementType getElementType();

  public void setString(String text);

  public String getString();
}
