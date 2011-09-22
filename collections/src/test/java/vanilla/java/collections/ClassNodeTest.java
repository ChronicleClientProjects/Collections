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

import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifierClassVisitor;
import vanilla.java.collections.hand.HandTypesAllocation;
import vanilla.java.collections.hand.HandTypesArrayList;
import vanilla.java.collections.hand.HandTypesElement;
import vanilla.java.collections.hand.HandTypesImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ClassNodeTest {
  @Test
  @Ignore
  public void test() throws IOException {
    for (Class clazz : new Class[]{HandTypesArrayList.class, HandTypesAllocation.class, HandTypesElement.class, HandTypesImpl.class}) {
//        for (Class clazz : new Class[]{HandTypesArrayList.class}) {
      ClassReader cr = new ClassReader(clazz.getName());
      StringWriter sw = new StringWriter();
      ASMifierClassVisitor cv = new ASMifierClassVisitor(new PrintWriter(sw));
      cr.accept(cv, 0);
      String text = sw.toString();
      System.out.println(text
                             .replaceAll("\"vanilla/java/collections/hand/HandTypes", "name + \"")
                             .replaceAll("Lvanilla/java/collections/hand/HandTypes", "L\" + name + \"")
                             .replaceAll("\"vanilla/java/collections/HandTypes\"", "name")
                             .replaceAll("Lvanilla/java/collections/HandTypes;", "L\" + name + \";")
                             .replaceAll("\"vanilla/java/collections/", "collections + \"")
                             .replaceAll("Lvanilla/java/collections/", "L\" + collections + \""));
    }
  }
}
