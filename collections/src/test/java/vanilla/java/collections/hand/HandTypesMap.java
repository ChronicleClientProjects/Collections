package vanilla.java.collections.hand;

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

import vanilla.java.collections.HugeMapBuilder;
import vanilla.java.collections.api.HugeAllocation;
import vanilla.java.collections.impl.AbstractHugeMap;
import vanilla.java.collections.impl.MappedFileChannel;
import vanilla.java.collections.model.Enum8FieldModel;
import vanilla.java.collections.model.Enumerated16FieldModel;

import java.lang.annotation.ElementType;

public class HandTypesMap extends AbstractHugeMap<HandTypesKey, HandTypesKeyElement, HandTypes, HandTypesValueElement, HandTypesAllocation> {
  final Enum8FieldModel<ElementType> elementTypeFieldModel
      = new Enum8FieldModel<ElementType>("elementType", 10, ElementType.class, ElementType.values());
  final Enumerated16FieldModel<String> stringEnumerated16FieldModel
      = new Enumerated16FieldModel<String>("text", 11, String.class);

  public HandTypesMap(HugeMapBuilder<HandTypesKey, HandTypes> hmb) {
    super(hmb);
  }

  @Override
  protected HandTypesAllocation createAllocation(MappedFileChannel mfc) {
    return new HandTypesAllocation(allocationSize, mfc);
  }

  @Override
  protected HandTypesKeyElement createKeyElement(long n) {
    return new HandTypesKeyElement(this, n);
  }

  @Override
  protected HandTypesValueElement createValueElement(long n) {
    return new HandTypesValueElement(this, n);
  }

  @Override
  protected HandTypesKey createKeyImpl() {
    return new HandTypesKeyImpl();
  }

  @Override
  protected HandTypes createValueImpl() {
    return new HandTypesImpl();
  }

  protected void compactStart() {
    stringEnumerated16FieldModel.compactStart();
  }

  protected void compactOnAllocation0(HugeAllocation allocation, long thisSize) {
    compactOnAllocation((HandTypesAllocation) allocation, thisSize);
  }

  protected void compactOnAllocation(HandTypesAllocation allocation, long thisSize) {
    stringEnumerated16FieldModel.compactScan(allocation.m_string, thisSize);
  }

  protected void compactEnd() {
    stringEnumerated16FieldModel.compactEnd();
  }

  @Override
  public void clear() {
    super.clear();
    elementTypeFieldModel.clear();
    stringEnumerated16FieldModel.clear();
  }
}
