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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TypeModel<T> {
  public static final Comparator<FieldModel> FIELD_MODEL_COMPARATOR = new Comparator<FieldModel>() {
    @Override
    public int compare(FieldModel o1, FieldModel o2) {
      return o1.fieldName().compareTo(o2.fieldName());
    }
  };
  private final Class<T> type;
  private final Map<Method, MethodModel> methodMap = new LinkedHashMap();
  private final FieldModel[] fields;

  public TypeModel(Class<T> type) {
    this.type = type;
    Map<String, FieldModel> fieldMap = new LinkedHashMap();
    for (Method method : type.getMethods()) {
      if (method.getDeclaringClass() == Object.class) continue;

      methodMap.put(method, new MethodModel(method, fieldMap));
    }
    fields = fieldMap.values().toArray(new FieldModel[fieldMap.size()]);
    Arrays.sort(fields, FIELD_MODEL_COMPARATOR);
  }

  public Class<T> type() {
    return type;
  }

  public ClassLoader classLoader() {
    return type().getClassLoader();
  }

  public FieldModel[] fields() {
    return fields;
  }

  public Object arrayOfField(int fieldNumber, int size) {
    return fields[fieldNumber].arrayOfField(size);
  }

  public MethodModel method(Method method) {
    return methodMap.get(method);
  }

  public String bcType() {
    return type().getName().replace('.', '/');
  }

  public int recordSize(int elements) {
    int recordSize = 0;
    for (FieldModel field : fields) {
      recordSize += field.sizeOf(elements);
    }
    return recordSize;
  }
}
