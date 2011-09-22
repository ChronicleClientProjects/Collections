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

import sun.nio.ch.DirectBuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class MappedFileChannel {
  private final RandomAccessFile raf;
  private final ByteBuffer buffer;

  public MappedFileChannel(RandomAccessFile raf, int length) throws IOException {
    this.raf = raf;
    buffer = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, length);
  }

  public void flush() throws IOException {
    raf.getChannel().force(false);
  }

  public void close() throws IOException {
    ((DirectBuffer) buffer).cleaner().clean();
    raf.close();
  }

  public ByteBuffer acquire(int size) {
    buffer.limit(buffer.position() + size);
    ByteBuffer bb = buffer.slice();
    buffer.position(buffer.limit());
    assert bb.remaining() == size;
    return bb;
  }
}
