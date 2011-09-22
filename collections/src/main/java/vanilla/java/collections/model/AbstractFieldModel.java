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

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class AbstractFieldModel<T> implements FieldModel<T> {
  private final String fieldName;
  /* package local */ final int fieldNumber;
  private Method setter;
  private Method getter;
  private String baseDirectory;

  protected AbstractFieldModel(String fieldName, int fieldNumber) {
    this.fieldName = fieldName;
    this.fieldNumber = fieldNumber;
  }

  @Override
  public void setter(Method setter) {
    this.setter = setter;
  }

  @Override
  public void getter(Method getter) {
    this.getter = getter;
  }

  @Override
  public int fieldNumber() {
    return fieldNumber;
  }

  @Override
  public Method setter() {
    return setter;
  }

  @Override
  public Method getter() {
    return getter;
  }

  @Override
  public String fieldName() {
    return fieldName;
  }

  @Override
  public String titleFieldName() {
    return Character.toUpperCase(fieldName().charAt(0)) + fieldName().substring(1);
  }

  @Override
  public String bcStoreType() {
    return storeType().getName().replace('.', '/');
  }

  @Override
  public String bcLStoreType() {
    return "L" + bcStoreType() + ";";
  }


  public String bcModelType() {
    return getClass().getName().replace('.', '/');
  }

  @Override
  public String bcLModelType() {
    return "L" + bcModelType() + ";";
  }

  public String bcFieldType() {
    return type().getName().replace('.', '/');
  }

  @Override
  public String bcLStoredType() {
    return bcLFieldType();
  }

  @Override
  public String bcLFieldType() {
    return "L" + bcFieldType() + ';';
  }

  @Override
  public String bcLSetType() {
    return bcLFieldType();
  }

  @Override
  public int bcFieldSize() {
    return 1;
  }

  @Override
  public BCType bcType() {
    return BCType.Int;
  }

  @Override
  public boolean virtualGetSet() {
    return false;
  }

  @Override
  public boolean copySimpleValue() {
    return true;
  }

  @Override
  public boolean isCallsNotEquals() {
    return false;
  }

  @Override
  public boolean isCallsHashCode() {
    return isCallsNotEquals();
  }

  @Override
  public void clear() {
  }

  @Override
  public boolean isBufferStore() {
    return true;
  }

  @Override
  public boolean isCompacting() {
    return false;
  }

  public static ByteBuffer acquireByteBuffer(MappedFileChannel mfc, int capacity) {
    return mfc == null ? ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder()) : mfc.acquire(capacity);
  }

  @Override
  public void baseDirectory(String baseDirectory) throws IOException {
    this.baseDirectory = baseDirectory;
  }

  public String baseDirectory() {
    return baseDirectory;
  }

  @Override
  public void flush() throws IOException {
  }
}
