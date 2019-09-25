package com.matt.nocom.server.util.kdtree;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.matt.nocom.server.model.shared.data.VectorXZ;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KdNode<T extends VectorXZ> implements VectorXZ {
  private List<T> references = Lists.newArrayList();

  private KdNode<T> parent;
  private KdNode<T> left;
  private KdNode<T> right;

  public KdNode(T first, KdNode<T> parent, KdNode<T> left, KdNode<T> right) {
    Objects.requireNonNull(first, "needs to be assigned a vector object");
    references.add(first);
    setParent(parent);
    setLeft(left);
    setRight(right);
  }
  public KdNode(T first, KdNode<T> left, KdNode<T> right) {
    this(first, null, left, right);
  }
  public KdNode(T first) {
    this(first, null, null, null);
  }

  public KdNode<T> addReference(T duplicate) {
    references.add(duplicate);
    return this;
  }

  public int getRefCount() {
    return references.size();
  }

  public T getReference() {
    return references.get(0);
  }

  public void setLeft(KdNode<T> left) {
    // if a node already exists, update its parent to be null
    if(getLeft() != null)
      getLeft().setParent(null);

    this.left = left;

    // update the new node's parent to be this node
    if(getLeft() != null)
      getLeft().setParent(this);
  }

  public void setRight(KdNode<T> right) {
    // if a node already exists, update its parent to be null
    if(getRight() != null)
      getRight().setParent(null);

    this.right = right;

    // update the new node's parent to be this node
    if(getRight() != null)
      getRight().setParent(this);
  }

  public boolean isLeftChild() {
    return getParent() != null && getParent().getLeft() == this;
  }

  public boolean isRightChild() {
    return getParent() != null && getParent().getRight() == this;
  }

  public boolean isLeaf() {
    return getLeft() == null && getRight() == null;
  }
  
  public int getDepth() {
    int count = 0;
    KdNode<T> next = getParent();
    while(next != null) {
      count++;
      next = next.getParent();
    }
    return count;
  }

  public void delete() {
    // remove this node from parent
    if(isLeftChild())
      getParent().setLeft(null);
    else if(isRightChild())
      getParent().setRight(null);

    // remove parent from this node
    setParent(null);

    // remove this nodes children
    if(getLeft() != null) {
      getLeft().setParent(null);
      setLeft(null);
    }
    if(getRight() != null) {
      getRight().setParent(null);
      setRight(null);
    }
  }

  public void move(KdNode<T> into) {
    into.setReferences(getReferences());
  }

  public void swap(KdNode<T> other) {
    // get field references before swap
    List<T> _refs = other.getReferences();
    KdNode<T> _parent = other.getParent(),
        _left = other.getLeft(),
        _right = other.getRight();

    // set the parents left/right child for the other node
    if(other.isLeftChild())
      other.getParent().setLeft(this);
    else if(other.isRightChild())
      other.getParent().setRight(this);

    // move this nodes data into the other
    other.setReferences(getReferences());
    other.setParent(getParent());
    other.setRight(getRight());
    other.setLeft(getLeft());

    // set the parents left/right child for this node
    if(isLeftChild())
      getParent().setLeft(other);
    else if(isRightChild())
      getParent().setRight(other);

    // move other node data into this
    setReferences(_refs);
    setParent(_parent);
    setLeft(_left);
    setRight(_right);
  }

  private void buildStringTree(StringBuilder builder, int tabs) {
    builder.append("pos(")
        .append(getX())
        .append(", ")
        .append(getZ())
        .append(") count=")
        .append(getRefCount())
        .append('\n');
    
    if(getLeft() != null) {
      builder.append(Strings.repeat("\t", tabs)).append("-> [L] ");
      getLeft().buildStringTree(builder, tabs + 1);
    }
    if(getRight() != null) {
      builder.append(Strings.repeat("\t", tabs)).append("-> [R] ");
      getRight().buildStringTree(builder, tabs + 1);
    }
  }

  @Override
  public int getX() {
    return getReference().getX();
  }

  @Override
  public int getZ() {
    return getReference().getZ();
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj)
      return true;
    else if(obj instanceof KdNode) {
      KdNode<? extends VectorXZ> other = (KdNode<?>) obj;
      return getX() == other.getX()
          && getZ() == other.getZ()
          // do not compare parents, not necessary
          && Objects.equals(getLeft(), other.getLeft())
          && Objects.equals(getRight(), other.getRight());
    } else return false;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    buildStringTree(builder, 0);
    return builder.toString();
  }
}
