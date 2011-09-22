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

import java.io.Serializable;

public interface ObjectTypes {
  public void setA(A a);

  public A getA();

  public void setB(B b);

  public B getB();

  public void setC(C c);

  public C getC();

  public void setD(D d);

  public D getD();

  public class A implements Serializable {
  }

  public class B {
  }

  public class C {
  }

  public class D {
  }
}
