/*******************************************************************************
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *  
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Johannes Spaeth - initial API and implementation
 *******************************************************************************/
package pathexpression;



public class RegEx<V> implements IRegEx<V> {
  private static class Union<V> implements IRegEx<V> {
    private IRegEx<V> b;
    private IRegEx<V> a;

    public Union(IRegEx<V> a, IRegEx<V> b) {
      assert a != null;
      assert b != null;
      this.a = a;
      this.b = b;
    }

    public String toString() {
      return "{" + a.toString() + " U " + b.toString() + "}";
    }

    public IRegEx<V> getFirst() {
      return a;
    }

    public IRegEx<V> getSecond() {
      return b;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (hashCode(a, b));
      return result;
    }

    private int hashCode(IRegEx<V> a, IRegEx<V> b) {
      if (a == null && b == null)
        return 1;
      if (a == null)
        return b.hashCode();
      if (b == null)
        return a.hashCode();
      return a.hashCode() + b.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Union other = (Union) obj;
      if (matches(a, other.a)) {
        return matches(b, other.b);
      }
      if (matches(a, other.b)) {
        return matches(b, other.a);
      }
      return false;
    }

    private boolean matches(IRegEx<V> a, IRegEx<V> b) {
      if (a == null) {
        if (b != null)
          return false;
        return true;
      }
      return a.equals(b);
    }

  }
  private static class Concatenate<V> implements IRegEx<V> {
    public IRegEx<V> b;
    public IRegEx<V> a;

    public Concatenate(IRegEx<V> a, IRegEx<V> b) {
      assert a != null;
      assert b != null;
      this.a = a;
      this.b = b;
    }

    public String toString() {
      return "(" + a.toString() + " \u00B7 " + b.toString() + ")";
    }

    public IRegEx<V> getFirst() {
      return a;
    }

    public IRegEx<V> getSecond() {
      return b;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((a == null) ? 0 : a.hashCode());
      result = prime * result + ((b == null) ? 0 : b.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Concatenate other = (Concatenate) obj;
      if (a == null) {
        if (other.a != null)
          return false;
      } else if (!a.equals(other.a))
        return false;
      if (b == null) {
        if (other.b != null)
          return false;
      } else if (!b.equals(other.b))
        return false;
      return true;
    }
  }
  private static class Star<V> implements IRegEx<V> {
    public IRegEx<V> a;

    public Star(IRegEx<V> a) {
      assert a != null;
      this.a = a;
    }

    public String toString() {
      return "[" + a.toString() + "]* ";
    }

    public IRegEx<V> getPlain() {
      return a;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((a == null) ? 0 : a.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Star other = (Star) obj;
      if (a == null) {
        if (other.a != null)
          return false;
      } else if (!a.equals(other.a))
        return false;
      return true;
    }
  }

  public static <V> IRegEx<V> union(IRegEx<V> a, IRegEx<V> b) {
    assert a != null;
    assert b != null;
    if (a instanceof EmptySet)
      return b;
    if (b instanceof EmptySet)
      return a;
    return simplify(new Union<V>(a, b));
  }

  public static <V> IRegEx<V> concatenate(IRegEx<V> a, IRegEx<V> b) {
    assert a != null;
    assert b != null;

    if (b instanceof EmptySet)
      return a;

    if (a instanceof EmptySet)
      return a;
    if (b instanceof Epsilon)
      return a;

    if (a instanceof Epsilon)
      return b;
    return simplify(new Concatenate<V>(a, b));
  }

  public static <V> boolean containsEpsilon(IRegEx<V> regex) {
    if (regex instanceof Union) {
      Union con = (Union) regex;
      if (containsEpsilon(con.getFirst()))
        return true;
      if (containsEpsilon(con.getSecond()))
        return true;
      return false;
    }
    return regex instanceof Epsilon;
  }

  public static <V> IRegEx<V> star(IRegEx<V> reg) {
    if (reg instanceof EmptySet || reg instanceof Epsilon)
      return reg;
    return simplify(new Star<V>(reg));
  }

  private static <V> IRegEx<V> simplify(IRegEx<V> in) {
    if (in instanceof Union) {
      Union<V> u = ((Union<V>) in);
      if (u.getFirst() instanceof EmptySet)
        return u.getSecond();
      if (u.getSecond() instanceof EmptySet)
        return u.getFirst();
      if (u.getFirst().equals(u.getSecond()))
        return u.getFirst();
    }
    if (in instanceof Concatenate) {
      Concatenate<V> c = (Concatenate<V>) in;
      IRegEx<V> first = c.getFirst();
      IRegEx<V> second = c.getSecond();
      if (first instanceof Epsilon)
        return c.getSecond();
      if (second instanceof Epsilon || second instanceof EmptySet)
        return c.getFirst();
    }

    if (in instanceof Star) {
      Star<V> star = (Star<V>) in;
      if (star.getPlain() instanceof EmptySet) {
        return star.getPlain();
      }
      if (star.getPlain() instanceof Epsilon)
        return star.getPlain();
    }

    return in;
  }

  public static class Plain<V> implements IRegEx<V> {
    public V v;

    public Plain(V v) {
      assert v != null;
      this.v = v;
    }

    public String toString() {
      return v.toString();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((v == null) ? 0 : v.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Plain other = (Plain) obj;
      if (v == null) {
        if (other.v != null)
          return false;
      } else if (!v.equals(other.v))
        return false;
      return true;
    }
  }

  public static class EmptySet<V> implements IRegEx<V> {
    public String toString() {
      return "EMPTY";
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof EmptySet;
    }
  }



}
