package vanilla.java.collections.api;

public interface HugeContainer {
  public int size();

  public long longSize();

  public void setSize(long length);

  public void recycle(Object o);

  public void ensureCapacity(long capacity);

  public void compact();
}
