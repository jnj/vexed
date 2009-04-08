package vexed;

public final class Objects {

  public static boolean areEqual(Object a, Object b) {
    return a == null ? b == null : a.equals(b);
  }
  
  public static int hash(Object o) {
    return o == null ? 0 : o.hashCode();
  }
}
