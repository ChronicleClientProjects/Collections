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
import java.util.Map;

public class MethodModel<T> {
  private final Method method;
  private final FieldModel<T> fieldModel;
  private final MethodType methodType;

  public MethodModel(Method method, Map<String, FieldModel> fields) {
    this.method = method;
    String name = method.getName();
    Class<?>[] parameterTypes = method.getParameterTypes();
    Class fieldType = null;
    String fieldName;
    if (name.startsWith("set")
            && name.length() > 3 && Character.isUpperCase(name.charAt(3))
            && parameterTypes.length == 1) {
      fieldName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
      methodType = MethodType.SETTER;
      fieldType = parameterTypes[0];
    } else if (name.startsWith("get")
                   && name.length() > 3 && Character.isUpperCase(name.charAt(3))
                   && parameterTypes.length == 0 && method.getReturnType() != void.class) {
      fieldName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
      methodType = MethodType.GETTER;
      fieldType = method.getReturnType();
    } else if (parameterTypes.length == 1) {
      fieldName = name;
      methodType = MethodType.SETTER;
      fieldType = parameterTypes[0];
    } else if (parameterTypes.length == 0 && method.getReturnType() != void.class) {
      fieldName = name;
      methodType = MethodType.GETTER;
    } else {
      fieldName = null;
      methodType = MethodType.OTHER;
    }
    if (fieldName == null) {
      fieldModel = null;
    } else {
      FieldModel fieldModel = fields.get(fieldName);
      if (fieldModel == null) {
        int fieldNumber = fields.size();
        if (fieldType == boolean.class) {
          fieldModel = new BooleanFieldModel(fieldName, fieldNumber);
        } else if (fieldType == Boolean.class) {
          fieldModel = new Boolean2FieldModel(fieldName, fieldNumber);
        } else if (fieldType == byte.class) {
          fieldModel = new ByteFieldModel(fieldName, fieldNumber);
        } else if (fieldType == Byte.class) {
          fieldModel = new Byte2FieldModel(fieldName, fieldNumber);
        } else if (fieldType == char.class) {
          fieldModel = new CharFieldModel(fieldName, fieldNumber);
        } else if (fieldType == short.class) {
          fieldModel = new ShortFieldModel(fieldName, fieldNumber);
        } else if (fieldType == int.class) {
          fieldModel = new IntFieldModel(fieldName, fieldNumber);
        } else if (fieldType == float.class) {
          fieldModel = new FloatFieldModel(fieldName, fieldNumber);
        } else if (fieldType == long.class) {
          fieldModel = new LongFieldModel(fieldName, fieldNumber);
        } else if (fieldType == double.class) {
          fieldModel = new DoubleFieldModel(fieldName, fieldNumber);
        } else if (Enum.class.isAssignableFrom(fieldType)) {
          Enum[] values = (Enum[]) fieldType.getEnumConstants();
          if (values.length < 256)
            fieldModel = new Enum8FieldModel(fieldName, fieldNumber, fieldType, values);
          else
            fieldModel = new ObjectFieldModel<T>(fieldName, fieldNumber, fieldType);
        } else {
          if (Comparable.class.isAssignableFrom(fieldType))
            fieldModel = new Enumerated16FieldModel<T>(fieldName, fieldNumber, fieldType);
          else
            fieldModel = new ObjectFieldModel<T>(fieldName, fieldNumber, fieldType);
        }
        fields.put(fieldName, fieldModel);
      }
      switch (methodType) {
        case SETTER:
          fieldModel.setter(method);
          break;
        case GETTER:
          fieldModel.getter(method);
          break;
        default:
          throw new AssertionError();
      }
      this.fieldModel = fieldModel;
    }
  }

  public MethodType methodType() {
    return methodType;
  }

  public FieldModel<T> fieldModel() {
    return fieldModel;
  }

  public Method method() {
    return method;
  }
}
