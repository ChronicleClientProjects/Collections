package vanilla.java.collections.impl;

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

import org.objectweb.asm.*;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;
import vanilla.java.collections.model.*;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public enum GenerateHugeArrays {
  ;
  private static final String collections = "vanilla/java/collections/";

  public static byte[] dumpArrayList(TypeModel tm) {
    ClassWriter cw = new ClassWriter(0);
    FieldVisitor fv;
    MethodVisitor mv;

    Class interfaceClass = tm.type();
    String name = interfaceClass.getName().replace('.', '/');

    cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, name + "ArrayList", "L" + collections + "impl/AbstractHugeArrayList<L" + name + ";L" + name + "Allocation;L" + name + "Element;>;", collections + "impl/AbstractHugeArrayList", null);

    cw.visitSource(tm.type().getSimpleName() + "ArrayList.java", null);

    for (FieldModel fm : tm.fields()) {
      if (fm instanceof Enum8FieldModel) {
        fv = cw.visitField(ACC_FINAL, fm.fieldName() + "FieldModel", "L" + collections + "model/Enum8FieldModel;", "L" + collections + "model/Enum8FieldModel<Ljava/lang/annotation/ElementType;>;", null);
        fv.visitEnd();
      } else if (fm instanceof Enumerated16FieldModel) {
        fv = cw.visitField(ACC_FINAL, fm.fieldName() + "FieldModel", "L" + collections + "model/Enumerated16FieldModel;", "L" + collections + "model/Enumerated16FieldModel<Ljava/lang/String;>;", null);
        fv.visitEnd();
      }
    }
    List<FieldModel> fields = new ArrayList<FieldModel>();
    {
      mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L" + collections + "HugeArrayBuilder;)V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(35, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKESPECIAL, collections + "impl/AbstractHugeArrayList", "<init>", "(L" + collections + "HugeArrayBuilder;)V");
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(29, l1);

      for (FieldModel fm : tm.fields()) {
        if (fm instanceof Enum8FieldModel) {
          mv.visitVarInsn(ALOAD, 0);
          mv.visitTypeInsn(NEW, fm.bcModelType());
          mv.visitInsn(DUP);
          mv.visitLdcInsn(fm.fieldName());
          mv.visitIntInsn(BIPUSH, fm.fieldNumber());
          mv.visitLdcInsn(Type.getType(fm.bcLFieldType()));
          mv.visitMethodInsn(INVOKESTATIC, fm.bcFieldType(), "values", "()[" + fm.bcLFieldType());
          mv.visitMethodInsn(INVOKESPECIAL, fm.bcModelType(), "<init>", "(Ljava/lang/String;ILjava/lang/Class;[Ljava/lang/Enum;)V");
          mv.visitFieldInsn(PUTFIELD, name + "ArrayList", fm.fieldName() + "FieldModel", fm.bcLModelType());
          mv.visitVarInsn(ALOAD, 0);
          mv.visitFieldInsn(GETFIELD, name + "ArrayList", fm.fieldName() + "FieldModel", fm.bcLModelType());
          mv.visitVarInsn(ALOAD, 1);
          mv.visitMethodInsn(INVOKEVIRTUAL, collections + "HugeArrayBuilder", "baseDirectory", "()Ljava/lang/String;");
          mv.visitMethodInsn(INVOKEVIRTUAL, fm.bcModelType(), "baseDirectory", "(Ljava/lang/String;)V");
          fields.add(fm);

        } else if (fm instanceof Enumerated16FieldModel) {
          mv.visitVarInsn(ALOAD, 0);
          mv.visitTypeInsn(NEW, fm.bcModelType());
          mv.visitInsn(DUP);
          mv.visitLdcInsn(fm.fieldName());
          mv.visitIntInsn(BIPUSH, fm.fieldNumber());
          mv.visitLdcInsn(Type.getType(fm.bcLFieldType()));
          mv.visitMethodInsn(INVOKESPECIAL, fm.bcModelType(), "<init>", "(Ljava/lang/String;ILjava/lang/Class;)V");
          mv.visitFieldInsn(PUTFIELD, name + "ArrayList", fm.fieldName() + "FieldModel", fm.bcLModelType());
          mv.visitVarInsn(ALOAD, 0);
          mv.visitFieldInsn(GETFIELD, name + "ArrayList", fm.fieldName() + "FieldModel", fm.bcLModelType());
          mv.visitVarInsn(ALOAD, 1);
          mv.visitMethodInsn(INVOKEVIRTUAL, collections + "HugeArrayBuilder", "baseDirectory", "()Ljava/lang/String;");
          mv.visitMethodInsn(INVOKEVIRTUAL, fm.bcModelType(), "baseDirectory", "(Ljava/lang/String;)V");
          fields.add(fm);
        }
      }

      mv.visitInsn(RETURN);
      Label l4 = new Label();
      mv.visitLabel(l4);
      mv.visitLocalVariable("this", "L" + name + "ArrayList;", null, l0, l4, 0);
      mv.visitLocalVariable("hab", "L" + collections + "HugeArrayBuilder;", null, l0, l4, 1);
      mv.visitMaxs(7, 2);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PROTECTED, "createAllocation", "(L" + collections + "impl/MappedFileChannel;)L" + name + "Allocation;", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(43, l0);
      mv.visitTypeInsn(NEW, name + "Allocation");
      mv.visitInsn(DUP);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, name + "ArrayList", "allocationSize", "I");
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKESPECIAL, name + "Allocation", "<init>", "(IL" + collections + "impl/MappedFileChannel;)V");
      mv.visitInsn(ARETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name + "ArrayList;", null, l0, l1, 0);
      mv.visitLocalVariable("mfc", "L" + collections + "impl/MappedFileChannel;", null, l0, l1, 1);
      mv.visitMaxs(4, 2);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PROTECTED, "createElement", "(J)L" + name + "Element;", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(48, l0);
      mv.visitTypeInsn(NEW, name + "Element");
      mv.visitInsn(DUP);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(LLOAD, 1);
      mv.visitMethodInsn(INVOKESPECIAL, name + "Element", "<init>", "(L" + collections + "impl/AbstractHugeArrayList;J)V");
      mv.visitInsn(ARETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name + "ArrayList;", null, l0, l1, 0);
      mv.visitLocalVariable("n", "J", null, l0, l1, 1);
      mv.visitMaxs(5, 3);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PROTECTED, "createImpl", "()L" + name + ";", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(53, l0);
      mv.visitTypeInsn(NEW, name + "Impl");
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, name + "Impl", "<init>", "()V");
      mv.visitInsn(ARETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name + "ArrayList;", null, l0, l1, 0);
      mv.visitMaxs(2, 1);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PROTECTED, "compactStart", "()V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(57, l0);
      for (FieldModel fm : fields) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, name + "ArrayList", fm.fieldName() + "FieldModel", fm.bcLModelType());
        mv.visitMethodInsn(INVOKEVIRTUAL, fm.bcModelType(), "compactStart", "()V");
      }
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(58, l1);
      mv.visitInsn(RETURN);
      Label l2 = new Label();
      mv.visitLabel(l2);
      mv.visitLocalVariable("this", "L" + name + "ArrayList;", null, l0, l2, 0);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PROTECTED, "compactOnAllocation", "(L" + name + "Allocation;J)V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(65, l0);
      for (FieldModel fm : fields) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, name + "ArrayList", fm.fieldName() + "FieldModel", fm.bcLModelType());
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(GETFIELD, name + "Allocation", "m_string", fm.bcLStoreType());
        mv.visitVarInsn(LLOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, fm.bcModelType(), "compactScan", "(" + fm.bcLStoreType() + "J)V");
      }
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(66, l1);
      mv.visitInsn(RETURN);
      Label l2 = new Label();
      mv.visitLabel(l2);
      mv.visitLocalVariable("this", "L" + name + "ArrayList;", null, l0, l2, 0);
      mv.visitLocalVariable("allocation", "L" + name + "Allocation;", null, l0, l2, 1);
      mv.visitLocalVariable("thisSize", "J", null, l0, l2, 2);
      mv.visitMaxs(4, 4);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PROTECTED, "compactEnd", "()V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(69, l0);
      for (FieldModel fm : fields) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, name + "ArrayList", fm.fieldName() + "FieldModel", fm.bcLModelType());
        mv.visitMethodInsn(INVOKEVIRTUAL, fm.bcModelType(), "compactEnd", "()V");
      }
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(70, l1);
      mv.visitInsn(RETURN);
      Label l2 = new Label();
      mv.visitLabel(l2);
      mv.visitLocalVariable("this", "L" + name + "ArrayList;", null, l0, l2, 0);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "clear", "()V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(74, l0);
      for (FieldModel fm : fields) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, name + "ArrayList", fm.fieldName() + "FieldModel", fm.bcLModelType());
        mv.visitMethodInsn(INVOKEVIRTUAL, fm.bcModelType(), "clear", "()V");
      }
      Label l3 = new Label();
      mv.visitLabel(l3);
      mv.visitLineNumber(77, l3);
      mv.visitInsn(RETURN);
      Label l4 = new Label();
      mv.visitLabel(l4);
      mv.visitLocalVariable("this", "L" + name + "ArrayList;", null, l0, l4, 0);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PROTECTED + ACC_BRIDGE + ACC_SYNTHETIC, "createImpl", "()Ljava/lang/Object;", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(29, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKEVIRTUAL, name + "ArrayList", "createImpl", "()L" + name + ";");
      mv.visitInsn(ARETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name + "ArrayList;", null, l0, l1, 0);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PROTECTED + ACC_BRIDGE + ACC_SYNTHETIC, "createElement", "(J)L" + collections + "impl/AbstractHugeElement;", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(29, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(LLOAD, 1);
      mv.visitMethodInsn(INVOKEVIRTUAL, name + "ArrayList", "createElement", "(J)L" + name + "Element;");
      mv.visitInsn(ARETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name + "ArrayList;", null, l0, l1, 0);
      mv.visitLocalVariable("x0", "J", null, l0, l1, 1);
      mv.visitMaxs(3, 3);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PROTECTED + ACC_BRIDGE + ACC_SYNTHETIC, "compactOnAllocation", "(L" + collections + "api/HugeAllocation;J)V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(29, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitTypeInsn(CHECKCAST, name + "Allocation");
      mv.visitVarInsn(LLOAD, 2);
      mv.visitMethodInsn(INVOKEVIRTUAL, name + "ArrayList", "compactOnAllocation", "(L" + name + "Allocation;J)V");
      mv.visitInsn(RETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name + "ArrayList;", null, l0, l1, 0);
      mv.visitLocalVariable("x0", "L" + collections + "api/HugeAllocation;", null, l0, l1, 1);
      mv.visitLocalVariable("x1", "J", null, l0, l1, 2);
      mv.visitMaxs(4, 4);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PROTECTED + ACC_BRIDGE + ACC_SYNTHETIC, "createAllocation", "(L" + collections + "impl/MappedFileChannel;)L" + collections + "api/HugeAllocation;", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(29, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKEVIRTUAL, name + "ArrayList", "createAllocation", "(L" + collections + "impl/MappedFileChannel;)L" + name + "Allocation;");
      mv.visitInsn(ARETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name + "ArrayList;", null, l0, l1, 0);
      mv.visitLocalVariable("x0", "L" + collections + "impl/MappedFileChannel;", null, l0, l1, 1);
      mv.visitMaxs(2, 2);
      mv.visitEnd();
    }
    cw.visitEnd();
    final byte[] bytes = cw.toByteArray();
//        ClassReader cr = new ClassReader(bytes);
//        cr.accept(new ASMifierClassVisitor(new PrintWriter(System.out)), 0);
    return bytes;
  }

  public static byte[] dumpAllocation(TypeModel tm) {

    ClassWriter cw = new ClassWriter(0);
    FieldVisitor fv;
    MethodVisitor mv;

    Class interfaceClass = tm.type();
    String name = interfaceClass.getName().replace('.', '/');

    cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, name + "Allocation", null, "java/lang/Object", new String[]{collections + "api/HugeAllocation"});

    cw.visitSource(tm.type().getSimpleName() + "Allocation.java", null);

    for (FieldModel fm : tm.fields()) {
      fv = cw.visitField(0, "m_" + fm.fieldName(), fm.bcLStoreType(), null, null);
      fv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(IL" + collections + "impl/MappedFileChannel;)V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(46, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");

      for (FieldModel fm : tm.fields()) {
        mv.visitVarInsn(ALOAD, 0);

        if (fm instanceof ObjectFieldModel) {
          mv.visitLdcInsn(Type.getType(fm.bcLFieldType()));
          mv.visitVarInsn(ILOAD, 1);
          mv.visitMethodInsn(INVOKESTATIC, fm.bcModelType(), "newArrayOfField", "(Ljava/lang/Class;I)[Ljava/lang/Object;");
          mv.visitTypeInsn(CHECKCAST, fm.bcLStoreType());
        } else {
          mv.visitVarInsn(ILOAD, 1);
          mv.visitVarInsn(ALOAD, 2);
          mv.visitMethodInsn(INVOKESTATIC, fm.bcModelType(), "newArrayOfField", "(IL" + collections + "impl/MappedFileChannel;)" + fm.bcLStoreType());
        }
        mv.visitFieldInsn(PUTFIELD, name + "Allocation", "m_" + fm.fieldName(), fm.bcLStoreType());
      }

      mv.visitInsn(RETURN);
      Label l14 = new Label();
      mv.visitLabel(l14);
      mv.visitLocalVariable("this", "L" + name + "Allocation;", null, l0, l14, 0);
      mv.visitLocalVariable("allocationSize", "I", null, l0, l14, 1);
      mv.visitLocalVariable("mfc", "L" + collections + "impl/MappedFileChannel;", null, l0, l14, 2);
      mv.visitMaxs(3, 3);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "clear", "()V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(62, l0);
      for (FieldModel fm : tm.fields()) {
        if (fm instanceof ObjectFieldModel) {
          mv.visitVarInsn(ALOAD, 0);
          mv.visitFieldInsn(GETFIELD, name + "Allocation", "m_" + fm.fieldName(), fm.bcLStoreType());
          mv.visitInsn(ACONST_NULL);
          mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "fill", "([Ljava/lang/Object;Ljava/lang/Object;)V");
        }
      }
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(63, l1);
      mv.visitInsn(RETURN);
      Label l2 = new Label();
      mv.visitLabel(l2);
      mv.visitLocalVariable("this", "L" + name + "Allocation;", null, l0, l2, 0);
      mv.visitMaxs(2, 1);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "destroy", "()V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(66, l0);
      for (FieldModel fm : tm.fields()) {
        if (!fm.isBufferStore()) continue;
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, name + "Allocation", "m_" + fm.fieldName(), fm.bcLStoreType());
        mv.visitTypeInsn(CHECKCAST, "java/nio/Buffer");
        mv.visitMethodInsn(INVOKESTATIC, GenerateHugeArrays.class.getName().replace('.', '/'), "clean", "(Ljava/nio/Buffer;)V");

      }
      Label l3 = new Label();
      mv.visitLabel(l3);
      mv.visitLineNumber(69, l3);
      mv.visitInsn(RETURN);
      Label l4 = new Label();
      mv.visitLabel(l4);
      mv.visitLocalVariable("this", "L" + name + "Allocation;", null, l0, l4, 0);
      mv.visitLocalVariable("m", "Ljava/lang/Object;", null, l3, l4, 1);
      mv.visitMaxs(1, 2);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PROTECTED, "finalize", "()V", null, new String[]{"java/lang/Throwable"});
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(76, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "finalize", "()V");
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(77, l1);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKEVIRTUAL, name + "Allocation", "destroy", "()V");
      Label l2 = new Label();
      mv.visitLabel(l2);
      mv.visitLineNumber(78, l2);
      mv.visitInsn(RETURN);
      Label l3 = new Label();
      mv.visitLabel(l3);
      mv.visitLocalVariable("this", "L" + name + "Allocation;", null, l0, l3, 0);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
    }

    cw.visitEnd();
    final byte[] bytes = cw.toByteArray();
//        ClassReader cr = new ClassReader(bytes);
//        cr.accept(new ASMifierClassVisitor(new PrintWriter(System.out)), 0);
    return bytes;
  }

  public static byte[] dumpElement(TypeModel tm) {

    ClassWriter cw = new ClassWriter(0);
    FieldVisitor fv;
    MethodVisitor mv;

    String name = tm.bcType();

    cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, name + "Element", "L" + collections + "impl/AbstractHugeElement<L" + name + "Allocation;>;L" + name + ";Ljava/io/Externalizable;", collections + "impl/AbstractHugeElement", new String[]{name, "java/io/Externalizable"});

    cw.visitSource(tm.type().getSimpleName() + "Element.java", null);

    {
      fv = cw.visitField(0, "allocation", "L" + name + "Allocation;", null, null);
      fv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L" + collections + "impl/AbstractHugeArrayList;J)V", "(L" + collections + "impl/AbstractHugeArrayList<L" + name + ";L" + name + "Allocation;L" + name + "Element;>;J)V", null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(34, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitVarInsn(LLOAD, 2);
      mv.visitMethodInsn(INVOKESPECIAL, collections + "impl/AbstractHugeElement", "<init>", "(L" + collections + "impl/AbstractHugeContainer;J)V");
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(35, l1);
      mv.visitInsn(RETURN);
      Label l2 = new Label();
      mv.visitLabel(l2);
      mv.visitLocalVariable("this", "L" + name + "Element;", null, l0, l2, 0);
      mv.visitLocalVariable("list", "L" + collections + "impl/AbstractHugeArrayList;", "L" + collections + "impl/AbstractHugeArrayList<L" + name + ";L" + name + "Allocation;L" + name + "Element;>;", l0, l2, 1);
      mv.visitLocalVariable("n", "J", null, l0, l2, 2);
      mv.visitMaxs(4, 4);
      mv.visitEnd();
    }
    for (FieldModel fm : tm.fields()) {
      ///////// SETTER //////////

      int maxLocals = 2 + fm.bcFieldSize();

      mv = cw.visitMethod(ACC_PUBLIC, "set" + fm.titleFieldName(), "(" + fm.bcLFieldType() + ")V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(39, l0);

      int invoke = INVOKESTATIC;
      if (fm.virtualGetSet()) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, name + "Element", "container", "L" + collections + "impl/AbstractHugeContainer;");
        mv.visitTypeInsn(CHECKCAST, name + "ArrayList");
        mv.visitFieldInsn(GETFIELD, name + "ArrayList", fm.fieldName() + "FieldModel", fm.bcLModelType());
        invoke = INVOKEVIRTUAL;
        maxLocals++;
      }

      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, name + "Element", "allocation", "L" + name + "Allocation;");
      mv.visitFieldInsn(GETFIELD, name + "Allocation", "m_" + fm.fieldName(), fm.bcLStoreType());
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, name + "Element", "offset", "I");
      mv.visitVarInsn(loadFor(fm.bcType()), 1);

      mv.visitMethodInsn(invoke, fm.bcModelType(), "set", "(" + fm.bcLStoreType() + "I" + fm.bcLSetType() + ")V");
      mv.visitInsn(RETURN);
      Label l2 = new Label();
      mv.visitLabel(l2);
      mv.visitLocalVariable("this", "L" + name + "Element;", null, l0, l2, 0);
      mv.visitLocalVariable("elementType", "Ljava/lang/annotation/ElementType;", null, l0, l2, 1);
      mv.visitMaxs(maxLocals, 1 + fm.bcFieldSize());
      mv.visitEnd();

      ///////// GETTER //////////

      mv = cw.visitMethod(ACC_PUBLIC, "get" + fm.titleFieldName(), "()" + fm.bcLFieldType(), null, null);
      mv.visitCode();
      Label l3 = new Label();
      mv.visitLabel(l3);
      mv.visitLineNumber(144, l3);

      BCType bcType = fm.bcType();
      if (fm.virtualGetSet()) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, name + "Element", "container", "L" + collections + "impl/AbstractHugeContainer;");
        mv.visitTypeInsn(CHECKCAST, name + "ArrayList");
        mv.visitFieldInsn(GETFIELD, name + "ArrayList", fm.fieldName() + "FieldModel", fm.bcLModelType());
        bcType = BCType.Reference;
      }

      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, name + "Element", "allocation", "L" + name + "Allocation;");
      mv.visitFieldInsn(GETFIELD, name + "Allocation", "m_" + fm.fieldName(), fm.bcLStoreType());
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, name + "Element", "offset", "I");
      mv.visitMethodInsn(invoke, fm.bcModelType(), "get", "(" + fm.bcLStoreType() + "I)" + fm.bcLSetType());
      if (!fm.bcLSetType().equals(fm.bcLFieldType()))
        mv.visitTypeInsn(CHECKCAST, fm.bcFieldType());
      mv.visitInsn(returnFor(bcType));
      Label l4 = new Label();
      mv.visitLabel(l4);
      mv.visitLocalVariable("this", "L" + name + "Element;", null, l3, l4, 0);
      mv.visitMaxs(4, 2);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PROTECTED, "updateAllocation0", "(I)V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(170, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, name + "Element", "container", "L" + collections + "impl/AbstractHugeContainer;");
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, name + "Element", "index", "J");
      mv.visitMethodInsn(INVOKEVIRTUAL, collections + "impl/AbstractHugeContainer", "getAllocation", "(J)L" + collections + "api/HugeAllocation;");
      mv.visitTypeInsn(CHECKCAST, name + "Allocation");
      mv.visitFieldInsn(PUTFIELD, name + "Element", "allocation", "L" + name + "Allocation;");
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(171, l1);
      mv.visitInsn(RETURN);
      Label l2 = new Label();
      mv.visitLabel(l2);
      mv.visitLocalVariable("this", "L" + name + "Element;", null, l0, l2, 0);
      mv.visitLocalVariable("allocationSize", "I", null, l0, l2, 1);
      mv.visitMaxs(4, 2);
      mv.visitEnd();
    }
    appendToStringHashCodeEqualsCopyOf(tm, cw, name + "Element", true);
    cw.visitEnd();

    final byte[] bytes = cw.toByteArray();
//        ClassReader cr = new ClassReader(bytes);
//        cr.accept(new ASMifierClassVisitor(new PrintWriter(System.out)), 0);
    return bytes;
  }

  private static void appendToStringHashCodeEqualsCopyOf(TypeModel tm, ClassWriter cw, String name2, boolean hasListField) {
    String name = tm.bcType();
    MethodVisitor mv;
    {
      mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(176, l0);
      mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V");
      mv.visitLdcInsn(tm.type().getSimpleName() + "{");
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");

      String sep = "";
      for (FieldModel fm : tm.fields()) {
        boolean text = CharSequence.class.isAssignableFrom(tm.type());

        mv.visitLdcInsn(sep + fm.fieldName() + "=" + (text ? "'" : ""));
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, name2, "get" + fm.titleFieldName(), "()" + fm.bcLFieldType());
        String appendType = "Ljava/lang/Object;";
        final Class fmType = fm.type();
        if (fmType.isPrimitive()) {
          if (fmType == byte.class || fmType == short.class)
            appendType = "I";
          else
            appendType = fm.bcLFieldType();
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(" + appendType + ")Ljava/lang/StringBuilder;");
        sep = text ? "', " : ", ";
      }
      if (sep.startsWith("'")) {
        mv.visitIntInsn(BIPUSH, 39);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
      }
      mv.visitIntInsn(BIPUSH, 125);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
      mv.visitInsn(ARETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name2 + ";", null, l0, l1, 0);
      mv.visitMaxs(3, 1);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "equals", "(Ljava/lang/Object;)Z", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(194, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      Label l1 = new Label();
      mv.visitJumpInsn(IF_ACMPNE, l1);
      mv.visitInsn(ICONST_1);
      mv.visitInsn(IRETURN);
      mv.visitLabel(l1);
      mv.visitLineNumber(195, l1);
      mv.visitVarInsn(ALOAD, 1);
      Label l2 = new Label();
      mv.visitJumpInsn(IFNULL, l2);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
      mv.visitVarInsn(ALOAD, 1);
      mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
      Label l3 = new Label();
      mv.visitJumpInsn(IF_ACMPEQ, l3);
      mv.visitLabel(l2);
      mv.visitInsn(ICONST_0);
      mv.visitInsn(IRETURN);
      mv.visitLabel(l3);
      mv.visitLineNumber(197, l3);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitTypeInsn(CHECKCAST, name2);
      mv.visitVarInsn(ASTORE, 2);
      Label l4 = new Label();
      mv.visitLabel(l4);
      mv.visitLineNumber(199, l4);
      final FieldModel[] fieldModels = tm.fields().clone();
      Arrays.sort(fieldModels, new Comparator<FieldModel>() {
        // reverse sort the preferences to optimise the
        @Override
        public int compare(FieldModel o1, FieldModel o2) {
          return o2.equalsPreference() - o1.equalsPreference();
        }
      });
      for (FieldModel fm : fieldModels) {
//                System.out.println(fm.fieldName());
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, name2, "get" + fm.titleFieldName(), "()" + fm.bcLFieldType());
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, name2, "get" + fm.titleFieldName(), "()" + fm.bcLFieldType());
        Label l5 = new Label();
        if (fm.isCallsNotEquals()) {
          mv.visitMethodInsn(INVOKESTATIC, fm.bcLModelType(), "notEquals", "(" + fm.bcLSetType() + fm.bcLSetType() + ")Z");
          mv.visitJumpInsn(IFEQ, l5);
        } else {
          mv.visitJumpInsn(IF_ICMPEQ, l5);
        }
        mv.visitInsn(ICONST_0);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l5);
      }

      mv.visitInsn(ICONST_1);
      mv.visitInsn(IRETURN);
      Label l17 = new Label();
      mv.visitLabel(l17);
      mv.visitLocalVariable("this", "L" + name2 + ";", null, l0, l17, 0);
      mv.visitLocalVariable("o", "Ljava/lang/Object;", null, l0, l17, 1);
      mv.visitLocalVariable("that", "L" + name2 + ";", null, l4, l17, 2);
      mv.visitMaxs(4, 3);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "hashCode", "()I", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      int count = 0;
      for (FieldModel fm : tm.fields()) {
//                if (count > 5) break;
//                System.out.println(fm.fieldName());
        if (count > 0) {
          mv.visitIntInsn(BIPUSH, 31);
          mv.visitInsn(IMUL);
        }
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, name2, "get" + fm.titleFieldName(), "()" + fm.bcLFieldType());
        if (fm.isCallsHashCode()) {
          mv.visitMethodInsn(INVOKESTATIC, fm.bcLModelType(), "hashCode", "(" + fm.bcLSetType() + ")I");
        }

        if (count > 0)
          mv.visitInsn(IADD);
        count++;
      }
      mv.visitInsn(IRETURN);
      Label l3 = new Label();
      mv.visitLabel(l3);
      mv.visitLocalVariable("this", "L" + name2 + ";", null, l0, l3, 0);
      mv.visitMaxs(6, 1);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "copyOf", "(L" + name + ";)V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(240, l0);

      boolean copySimpleValues = false;
      for (FieldModel fm : tm.fields()) {
        if (!fm.copySimpleValue() || !hasListField) {
//                if (true) {
          mv.visitVarInsn(ALOAD, 0);
          mv.visitVarInsn(ALOAD, 1);
          mv.visitMethodInsn(INVOKEINTERFACE, name, "get" + fm.titleFieldName(), "()" + fm.bcLFieldType());
          mv.visitMethodInsn(INVOKEVIRTUAL, name2, "set" + fm.titleFieldName(), "(" + fm.bcLFieldType() + ")V");
        } else {
          copySimpleValues = true;
        }
      }
      Label l4 = new Label();
      Label l6 = new Label();
      if (copySimpleValues) {
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLineNumber(243, l3);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(INSTANCEOF, name2);
        mv.visitJumpInsn(IFEQ, l4);
        Label l5 = new Label();
        mv.visitLabel(l5);
        mv.visitLineNumber(238, l5);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, name2);
        mv.visitVarInsn(ASTORE, 2);
        mv.visitLabel(l6);
        mv.visitLineNumber(239, l6);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitFieldInsn(GETFIELD, name2, "container", "L" + collections + "impl/AbstractHugeContainer;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, name2, "container", "L" + collections + "impl/AbstractHugeContainer;");
        mv.visitJumpInsn(IF_ACMPNE, l4);
        for (FieldModel fm : tm.fields()) {
          if (fm.copySimpleValue()) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, name2, "allocation", "L" + name + "Allocation;");
            mv.visitFieldInsn(GETFIELD, name + "Allocation", "m_" + fm.fieldName(), fm.bcLStoreType());
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, name2, "offset", "I");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitFieldInsn(GETFIELD, name2, "allocation", "L" + name + "Allocation;");
            mv.visitFieldInsn(GETFIELD, name + "Allocation", "m_" + fm.fieldName(), fm.bcLStoreType());
            mv.visitVarInsn(ALOAD, 2);
            mv.visitFieldInsn(GETFIELD, name2, "offset", "I");
            mv.visitMethodInsn(INVOKEVIRTUAL, fm.bcStoreType(), "get", "(I)" + fm.bcLStoredType());
            mv.visitMethodInsn(INVOKEVIRTUAL, fm.bcStoreType(), "put", "(I" + fm.bcLStoredType() + ")" + fm.bcLStoreType());
            mv.visitInsn(POP);
          }
        }
      }
      Label l16 = new Label();
      mv.visitLabel(l16);
      mv.visitLineNumber(238, l16);
      Label l17 = new Label();
      mv.visitJumpInsn(GOTO, l17);
      mv.visitLabel(l4);
      mv.visitLineNumber(241, l4);
      for (FieldModel fm : tm.fields()) {
        if (fm.copySimpleValue()) {
          mv.visitVarInsn(ALOAD, 0);
          mv.visitVarInsn(ALOAD, 1);
          mv.visitMethodInsn(INVOKEINTERFACE, name, "get" + fm.titleFieldName(), "()" + fm.bcLFieldType());
          mv.visitMethodInsn(INVOKEVIRTUAL, name2, "set" + fm.titleFieldName(), "(" + fm.bcLFieldType() + ")V");
        }
      }
      mv.visitLabel(l17);
      mv.visitLineNumber(251, l17);
      mv.visitInsn(RETURN);
      Label l27 = new Label();
      mv.visitLabel(l27);
      mv.visitLocalVariable("this", "L" + name2 + ";", null, l0, l27, 0);
      mv.visitLocalVariable("t", "L" + name + ";", null, l0, l27, 1);
      if (copySimpleValues) {
        mv.visitLocalVariable("mte", "L" + name + "Element;", null, l6, l4, 2);
      }
      mv.visitMaxs(4, 3);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "copyOf", "(Ljava/lang/Object;)V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(275, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitTypeInsn(CHECKCAST, name);
      mv.visitMethodInsn(INVOKEVIRTUAL, name2, "copyOf", "(L" + name + ";)V");
      mv.visitInsn(RETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name2 + ";", null, l0, l1, 0);
      mv.visitLocalVariable("x0", "Ljava/lang/Object;", null, l0, l1, 1);
      mv.visitMaxs(2, 2);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "writeExternal", "(Ljava/io/ObjectOutput;)V", null, new String[]{"java/io/IOException"});
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(185, l0);
      for (FieldModel fm : tm.fields()) {
        mv.visitVarInsn(ALOAD, 1);
        if (fm.bcLSetType().equals(fm.bcLFieldType())) {
          mv.visitVarInsn(ALOAD, 0);
          mv.visitMethodInsn(INVOKEVIRTUAL, name2, "get" + fm.titleFieldName(), "()" + fm.bcLFieldType());
          mv.visitMethodInsn(INVOKESTATIC, fm.bcModelType(), "write", "(Ljava/io/ObjectOutput;" + fm.bcLSetType() + ")V");
        } else {
          mv.visitLdcInsn(Type.getType(fm.bcLFieldType()));
          mv.visitVarInsn(ALOAD, 0);
          mv.visitMethodInsn(INVOKEVIRTUAL, name2, "get" + fm.titleFieldName(), "()" + fm.bcLFieldType());
          mv.visitMethodInsn(INVOKESTATIC, fm.bcModelType(), "write", "(Ljava/io/ObjectOutput;Ljava/lang/Class;" + fm.bcLSetType() + ")V");
        }
      }
      Label l13 = new Label();
      mv.visitLabel(l13);
      mv.visitLineNumber(288, l13);
      mv.visitInsn(RETURN);
      Label l14 = new Label();
      mv.visitLabel(l14);
      mv.visitLocalVariable("this", "L" + name2 + ';', null, l0, l14, 0);
      mv.visitLocalVariable("out", "Ljava/io/ObjectOutput;", null, l0, l14, 1);
      mv.visitMaxs(4, 2);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "readExternal", "(Ljava/io/ObjectInput;)V", null, new String[]{"java/io/IOException", "java/lang/ClassNotFoundException"});
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(287, l0);
      for (FieldModel fm : tm.fields()) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        if (fm.bcLSetType().equals(fm.bcLFieldType())) {
          mv.visitMethodInsn(INVOKESTATIC, fm.bcModelType(), "read", "(Ljava/io/ObjectInput;)" + fm.bcLSetType());
        } else {
          mv.visitLdcInsn(Type.getType(fm.bcLFieldType()));
          mv.visitMethodInsn(INVOKESTATIC, fm.bcModelType(), "read", "(Ljava/io/ObjectInput;Ljava/lang/Class;)" + fm.bcLSetType());
          mv.visitTypeInsn(CHECKCAST, fm.bcFieldType());
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, name2, "set" + fm.titleFieldName(), "(" + fm.bcLFieldType() + ")V");
      }
      Label l13 = new Label();
      mv.visitLabel(l13);
      mv.visitLineNumber(305, l13);
      mv.visitInsn(RETURN);
      Label l14 = new Label();
      mv.visitLabel(l14);
      mv.visitLocalVariable("this", "L" + name2 + ";", null, l0, l14, 0);
      mv.visitLocalVariable("in", "Ljava/io/ObjectInput;", null, l0, l14, 1);
      mv.visitMaxs(3, 2);
      mv.visitEnd();
    }
  }

  public static byte[] dumpImpl(TypeModel tm) {

    ClassWriter cw = new ClassWriter(0);
    FieldVisitor fv;
    MethodVisitor mv;

    String name = tm.bcType();

    cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, name + "Impl", "Ljava/lang/Object;L" + name + ";L" + collections + "api/HugeElement<L" + name + ";>;Ljava/io/Externalizable;", "java/lang/Object", new String[]{name, collections + "api/HugeElement", "java/io/Externalizable"});

    cw.visitSource(tm.getClass().getSimpleName() + "Impl.java", null);

    for (FieldModel fm : tm.fields()) {
      fv = cw.visitField(ACC_PRIVATE, "m_" + fm.fieldName(), fm.bcLFieldType(), null, null);
      fv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(30, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
      mv.visitInsn(RETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name + "Impl;", null, l0, l1, 0);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
    }
    for (FieldModel fm : tm.fields()) {
      mv = cw.visitMethod(ACC_PUBLIC, "set" + fm.titleFieldName(), "(" + fm.bcLFieldType() + ")V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(47, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(loadFor(fm.bcType()), 1);
      mv.visitFieldInsn(PUTFIELD, name + "Impl", "m_" + fm.fieldName(), fm.bcLFieldType());
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(48, l1);
      mv.visitInsn(RETURN);
      Label l2 = new Label();
      mv.visitLabel(l2);
      mv.visitLocalVariable("this", "L" + name + "Impl;", null, l0, l2, 0);
      mv.visitLocalVariable("b", fm.bcLFieldType(), null, l0, l2, 1);
      mv.visitMaxs(1 + fm.bcFieldSize(), 1 + fm.bcFieldSize());
      mv.visitEnd();

      mv = cw.visitMethod(ACC_PUBLIC, "get" + fm.titleFieldName(), "()" + fm.bcLFieldType(), null, null);
      mv.visitCode();
      Label l3 = new Label();
      mv.visitLabel(l3);
      mv.visitLineNumber(45, l3);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, name + "Impl", "m_" + fm.fieldName(), fm.bcLFieldType());
      mv.visitInsn(returnFor(fm.bcType()));
      Label l4 = new Label();
      mv.visitLabel(l4);
      mv.visitLocalVariable("this", "L" + name + "Impl;", null, l3, l4, 0);
      mv.visitMaxs(1 + fm.bcFieldSize(), 1);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "index", "(J)V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(175, l0);
      mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "()V");
      mv.visitInsn(ATHROW);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name + "Impl;", null, l0, l1, 0);
      mv.visitLocalVariable("n", "J", null, l0, l1, 1);
      mv.visitMaxs(2, 3);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "index", "()J", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(180, l0);
      mv.visitInsn(LCONST_0);
      mv.visitInsn(LRETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name + "Impl;", null, l0, l1, 0);
      mv.visitMaxs(2, 1);
      mv.visitEnd();
    }
    appendToStringHashCodeEqualsCopyOf(tm, cw, name + "Impl", false);

    {
      mv = cw.visitMethod(ACC_PUBLIC, "hugeElementType", "()L" + collections + "api/HugeElementType;", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(236, l0);
      mv.visitFieldInsn(GETSTATIC, collections + "api/HugeElementType", "BeanImpl", "L" + collections + "api/HugeElementType;");
      mv.visitInsn(ARETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "L" + name + "Impl;", null, l0, l1, 0);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
    }
    cw.visitEnd();

    final byte[] bytes = cw.toByteArray();
//        ClassReader cr = new ClassReader(bytes);
//        cr.accept(new ASMifierClassVisitor(new PrintWriter(System.out)), 0);

    return bytes;
  }


  private static final int[] returnForArray = {IRETURN, LRETURN, DRETURN, FRETURN, ARETURN};

  private static int returnFor(BCType bcType) {
    return returnForArray[bcType.ordinal()];
  }

  private static final int[] loadForArray = {ILOAD, LLOAD, DLOAD, FLOAD, ALOAD};

  private static int loadFor(BCType bcType) {
    return loadForArray[bcType.ordinal()];
  }

  private static final int[] storeForArray = {ISTORE, LSTORE, DSTORE, FSTORE, ASTORE};

  private static int storeFor(BCType bcType) {
    return storeForArray[bcType.ordinal()];
  }

  public static void clean(Buffer buffer) {
    final Cleaner cleaner = ((DirectBuffer) buffer).cleaner();
    if (cleaner != null)
      cleaner.clean();
  }
}
