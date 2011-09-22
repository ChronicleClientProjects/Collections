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

import java.io.IOException;
import java.lang.reflect.Method;

public interface FieldModel<T> {
  Object arrayOfField(int size);

  T getAllocation(Object[] arrays, int index);

  void setAllocation(Object[] arrays, int index, T value);

  void setter(Method setter);

  void getter(Method getter);

  int fieldNumber();

  Method setter();

  Method getter();

  Class<T> type();

  String fieldName();

  Class storeType();

  String titleFieldName();

  String bcModelType();

  String bcLModelType();

  String bcStoreType();

  String bcLStoreType();

  String bcFieldType();

  String bcLFieldType();

  String bcLSetType();

  String bcLStoredType();

  int bcFieldSize();

  BCType bcType();

  boolean virtualGetSet();

  boolean copySimpleValue();

  boolean isCallsNotEquals();

  boolean isCallsHashCode();

  public void clear();

  boolean isBufferStore();

  boolean isCompacting();

  short equalsPreference();

  int sizeOf(int elements);

  void baseDirectory(String baseDirectory) throws IOException;

  void flush() throws IOException;
}
