package com.matt.nocom.server.util.kdtree;

import com.google.common.collect.Lists;
import com.matt.nocom.server.model.shared.data.VectorXZ;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;

/**
 * 2D implementation only of k-d tree
 */
public class KdTree<T extends VectorXZ> implements Iterable<KdNode<T>> {
  private static final int K = 2;

  private KdNode<T> root;
  private int size = 0;
  
  public KdTree(List<T> vectors) {
    this.root = insertAll(vectors);
  }

  /**
   * Insert by median
   */
  private KdNode<T> insertAll(List<T> vectors, int depth) {
    if(vectors == null || vectors.isEmpty())
      return null;

    // don't really need to sort the list every time
    switch (depth % K) {
      case 0:
        vectors.sort(Comparator.comparingInt(VectorXZ::getX).thenComparing(VectorXZ::getZ));
        break;
      case 1:
        vectors.sort(Comparator.comparingInt(VectorXZ::getZ).thenComparing(VectorXZ::getX));
        break;
        default: throw new IllegalArgumentException("dimension must be 0 or 1");
    }

    final int median = vectors.size() / 2; // right bias
    T at = vectors.get(median);
    KdNode<T> node = createNode(at);

    if(median > 0) {
      // the index the left side ends at
      int leftIndex = median - 1;
      // the index the right side starts at
      int rightIndex = median + 1;
      // store the object returned from get
      T e;

      // go down the list and add duplicates from the left and right

      while(leftIndex >= 0 && at.isVectorEq(e = vectors.get(leftIndex))) {
        node.addReference(e);
        --leftIndex;
      }
      while(rightIndex < vectors.size() && at.isVectorEq(e = vectors.get(rightIndex))) {
        node.addReference(e);
        ++rightIndex;
      }

      if(leftIndex >= 0)
        node.setLeft(insertAll(vectors.subList(0, leftIndex + 1), depth + 1));

      if(rightIndex < vectors.size())
        node.setRight(insertAll(vectors.subList(rightIndex, vectors.size()), depth + 1));
    }

    return node;
  }
  private KdNode<T> insertAll(List<T> vectors) {
    return insertAll(vectors, 0);
  }

  private KdNode<T> remove(KdNode<T> start, VectorXZ vector, final int depth) {
    if(start == null)
      return null;

    final int d = depth % K;

    if(start.isVectorEq(vector)) {
      if(start.isLeaf()) {
        size--;
        start.delete();
        if(start == getRoot()) root = null; // epic edge case
        return null;
      } else if (start.getRight() != null) {
        KdNode<T> min = min(start.getRight(), d, depth + 1);
        min.move(start);
        return remove(start.getRight(), min, depth + 1);
      } else if(start.getLeft() != null) {
        KdNode<T> max = max(start.getLeft(), d, depth + 1);
        max.move(start);
        return remove(start.getLeft(), max, depth + 1);
      }
      return start;
    }

    double dx = dx(start, vector, d);

    KdNode<T> near, far;
    if(dx > 0) {
      near = start.getLeft();
      far = start.getRight();
    } else {
      near = start.getRight();
      far = start.getLeft();
    }

    KdNode<T> del = remove(near, vector, depth + 1);

    if(del == null)
      return remove(far, vector, depth + 1);

    return del;
  }
  public KdNode<T> remove(KdNode<T> start, VectorXZ vec) {
    return remove(start, vec, 0);
  }
  public KdNode<T> remove(VectorXZ vec) {
    return remove(root, vec);
  }
  public KdNode<T> removeNode(KdNode<T> start) {
    return remove(start, start, start.getDepth());
  }

  private static <T extends VectorXZ> KdNode<T> minimumOf(KdNode<T> arg1, KdNode<T> arg2, KdNode<T> arg3, final int d) {
    KdNode<T> best = arg1;
    if(arg2 != null && dx(best, arg2, d) > 0) best = arg2;
    if(arg3 != null && dx(best, arg3, d) > 0) best = arg3;
    return best;
  }

  private static <T extends VectorXZ> KdNode<T> maximumOf(KdNode<T> arg1, KdNode<T> arg2, KdNode<T> arg3, final int d) {
    KdNode<T> best = arg1;
    if(arg2 != null && dx(best, arg2, d) < 0) best = arg2;
    if(arg3 != null && dx(best, arg3, d) < 0) best = arg3;
    return best;
  }

  private KdNode<T> min(KdNode<T> start, final int dim, int depth) {
    if(start == null)
      return null;

    if(depth % K == dim) {
      if(start.getLeft() == null)
        return start;
      else
        return min(start.getLeft(), dim, depth + 1);
    }

    return minimumOf(start,
        min(start.getLeft(), dim, depth + 1),
        min(start.getRight(), dim, depth + 1),
        dim);
  }

  private KdNode<T> max(KdNode<T> start, final int dim, int depth) {
    if(start == null)
      return null;

    if(depth % K == dim) {
      if(start.getRight() == null)
        return start;
      else
        return max(start.getRight(), dim, depth + 1);
    }

    return maximumOf(start,
        max(start.getLeft(), dim, depth + 1),
        max(start.getRight(), dim, depth + 1),
        dim);
  }

  private KdNode<T> find(KdNode<T> start, VectorXZ vector, final int depth) {
    if(start == null)
      return null;
    else if(vector == null)
      return null;
    else if(start.isVectorEq(vector))
      return start;

    int d = depth % K;
    double dx = dx(start, vector, d);

    KdNode<T> near, far;
    if(dx > 0) {
      near = start.getLeft();
      far = start.getRight();
    } else {
      near = start.getRight();
      far = start.getLeft();
    }

    KdNode<T> find = find(near, vector, depth + 1);

    if(find == null) // crazy edge case when dx=0, can be either the near or the far. there is no way to know. luckily its rare
      return find(far, vector, depth + 1);

    return find;
  }
  public KdNode<T> find(VectorXZ search) {
    return find(root, search, 0);
  }

  private void nearest(KdNode<T> node, VectorXZ vector, Best<T> best, int depth) {
    if(node == null)
      return;

    double d = node.distanceSqTo(vector);
    if(d < best.distance) {
      best.node = node;
      best.distance = d;
    }

    double dx = dx(node, vector, depth % K);

    KdNode<T> near, far;
    if(dx > 0) {
      near = node.getLeft();
      far = node.getRight();
    } else {
      near = node.getRight();
      far = node.getLeft();
    }

    nearest(near, vector, best, depth + 1);

    if((dx*dx) < best.distance)
      nearest(far, vector, best, depth + 1);
  }
  public KdNode<T> nearest(VectorXZ vector) {
    Objects.requireNonNull(root, "root is null");
    Best<T> best = new Best<>(root, root.distanceSqTo(vector));
    nearest(root, vector, best, 0);
    return best.node;
  }

  private void radiusSq(KdNode<T> node, final VectorXZ origin, final double radiusSq, final List<KdNode<T>> found, int depth) {
    if(node == null)
      return;

    double d = node.distanceSqTo(origin);
    if(d < radiusSq)
      found.add(node);

    double dx = dx(node, origin, depth % K);

    KdNode<T> near, far;
    if(dx > 0) {
      near = node.getLeft();
      far = node.getRight();
    } else {
      near = node.getRight();
      far = node.getLeft();
    }

    radiusSq(near, origin, radiusSq, found, depth + 1);

    if((dx*dx) < radiusSq)
      radiusSq(far, origin, radiusSq, found, depth + 1);
  }
  public List<KdNode<T>> radiusSq(VectorXZ origin, double radiusSq) {
    List<KdNode<T>> nodes = Lists.newArrayList();
    radiusSq(root, origin, radiusSq, nodes, 0);
    return nodes;
  }
  public List<KdNode<T>> radiusSqNode(KdNode<T> origin, double radiusSq) {
    List<KdNode<T>> nodes = Lists.newArrayList();
    radiusSq(origin, origin, radiusSq, nodes, 0);
    return nodes;
  }

  public KdNode<T> getRoot() {
    return root;
  }

  public boolean isEmpty() {
    return root == null || size <= 0;
  }

  public int size() {
    return size;
  }
  
  public KdNode<T> getRightMost(KdNode<T> node) {
    if(node == null)
      return null;
    else if(node.isLeaf())
      return node;
    else if(node.getRight() != null)
      return getRightMost(node.getRight());
    else // left node must not be null
      return getRightMost(node.getLeft());
  }
  public KdNode<T> getRightMost() {
    return getRightMost(getRoot());
  }
  
  public KdNode<T> getLeftMost(KdNode<T> node) {
    if(node == null)
      return null;
    else if(node.isLeaf())
      return node;
    else if(node.getLeft() != null)
      return getLeftMost(node.getLeft());
    else // right node must not be null
      return getLeftMost(node.getRight());
  }
  public KdNode<T> getLeftMost() {
    return getLeftMost(getRoot());
  }

  private KdNode<T> createNode(T vec) {
    ++size;
    return new KdNode<>(vec);
  }

  public Stream<KdNode<T>> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  @Override
  public Spliterator<KdNode<T>> spliterator() {
    return Spliterators.spliterator(iterator(), size(), Spliterator.IMMUTABLE);
  }

  @Override
  public Iterator<KdNode<T>> iterator() {
    return new KdTreeIterator<>(getRoot());
  }

  //
  //
  //

  private static int axisOf(VectorXZ vec1, int d) {
    return d == 0 ? vec1.getX() : vec1.getZ();
  }

  private static int dx(VectorXZ node, VectorXZ vec, int d) {
    return axisOf(node, d) - axisOf(vec, d);
  }

  private static class KdTreeIterator<T extends VectorXZ> implements Iterator<KdNode<T>> {
    private KdNode<T> next;

    KdTreeIterator(KdNode<T> root) {
      this.next = root;
      while(next != null && next.getLeft() != null) next = next.getLeft();
    }

    @Override
    public boolean hasNext() {
      return next != null;
    }

    @Override
    public KdNode<T> next() {
      if(next == null)
        return null;
      KdNode<T> current = next;

      if(next.getRight() != null) {
        next = next.getRight();
        while(next.getLeft() != null) next = next.getLeft();
        return current;
      }

      for(;; next = next.getParent()) {
        if(next.getParent() == null) {
          next = null;
          break;
        } else if(next.getParent().getLeft() == next) {
          next = next.getParent();
          break;
        }
      }

      return current;
    }
  }

  @AllArgsConstructor
  private static class Best<E extends VectorXZ> {
    private KdNode<E> node;
    private double distance;
  }
}
