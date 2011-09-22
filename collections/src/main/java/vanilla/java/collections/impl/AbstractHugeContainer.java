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

import vanilla.java.collections.api.HugeAllocation;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHugeContainer<T, TA extends HugeAllocation> {
  protected final int allocationSize;
  protected final int allocationByteSize;
  protected final boolean setRemoveReturnsNull;
  protected final String baseDirectory;

  protected final List<TA> allocations = new ArrayList<TA>();
  protected final List<MappedFileChannel> mfChannels = new ArrayList<MappedFileChannel>();
  protected long longSize;

  public AbstractHugeContainer(HugeCollectionBuilder<T> hab) {
    this.allocationSize = hab.allocationSize();
    this.setRemoveReturnsNull = hab.setRemoveReturnsNull();
    this.baseDirectory = hab.baseDirectory();
    if (baseDirectory != null) {
      this.allocationByteSize = hab.typeModel().recordSize(allocationSize);
      new File(baseDirectory).mkdirs();
    } else {
      this.allocationByteSize = 0;
    }
  }


  public void ensureCapacity(long size) {
    long blocks = (size + allocationSize - 1) / allocationSize;
    while (blocks > allocations.size()) {
      MappedFileChannel mfc = null;
      if (baseDirectory != null) {
        final String name = baseDirectory + "/alloc-" + allocations.size();
        RandomAccessFile raf = null;
        try {
          raf = new RandomAccessFile(name, "rw");
          mfChannels.add(mfc = new MappedFileChannel(raf, allocationByteSize));
        } catch (IOException e) {
          try {
            raf.close();
          } catch (IOException ignored) {
          }
          throw new IllegalStateException("Unable to create allocation " + name, e);
        }
      }
      allocations.add(createAllocation(mfc));
    }
  }

  protected abstract TA createAllocation(MappedFileChannel mfc);

  public int size() {
    return longSize < Integer.MAX_VALUE ? (int) longSize : Integer.MAX_VALUE;
  }

  public long longSize() {
    return longSize;
  }

  public boolean isEmpty() {
    return longSize == 0;
  }


  public void setSize(long length) {
    ensureCapacity(length);
    longSize = length;
  }

  public TA getAllocation(long index) {
    return allocations.get((int) (index / allocationSize));
  }

  public void clear() {
    for (TA allocation : allocations) {
      allocation.clear();
    }
    longSize = 0;
  }

  public void compact() {
    int allocationsNeeded = (int) (longSize() / allocationSize + 1);
    while (allocations.size() > allocationsNeeded) {
      allocations.remove(allocations.size() - 1).destroy();
    }
    compactStart();
    for (int i = 0, allocationsSize = allocations.size(); i < allocationsSize; i++) {
      compactOnAllocation(allocations.get(i), Math.min(longSize - i * allocationsSize, allocationSize));
    }
    compactEnd();
  }

  protected abstract void compactStart();

  protected abstract void compactOnAllocation(TA ta, long i);

  protected abstract void compactEnd();

}
