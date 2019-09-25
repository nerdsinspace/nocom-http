package com.matt.nocom.server.util.kdtree;

import static org.junit.Assert.*;

import com.google.common.collect.Sets;
import com.matt.nocom.server.model.shared.data.Vector;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class KdTreeTest {
  private static final int SAMPLE_SIZE  = 1_000;
  private static final int NUM_TESTS    = 100;
  private static final int RANGE        = 10_000;

  @Parameters
  public static Collection<Object[]> data() {
    return IntStream.range(0, NUM_TESTS)
        .mapToObj(i -> new Object[] {i % 2 == 0})
        .collect(Collectors.toList());
  }

  @Parameter
  public boolean createDuplicates;

  private List<Vector> vectors;

  @Before
  public void setupVectors() {
    vectors = IntStream.range(0, SAMPLE_SIZE)
        .mapToObj(i -> new Vector(random(RANGE), random(RANGE)))
        .collect(Collectors.toList());

    if(createDuplicates) {
      final int size = vectors.size();
      for(int i = 0; i < (size / 10) + 1; i++)
        vectors.set(randomInt(size - 1), vectors.get(randomInt(size - 1)));
    }

    Collections.shuffle(vectors);
  }

  @Test
  public void Behavior_Test() {
    Vector V2_3, V5_4, V9_6, V4_7, V8_1, V7_2;
    List<Vector> vectors = Arrays.asList(
        V2_3 = new Vector(2,3),
        V5_4 = new Vector(5,4),
        V9_6 = new Vector(9,6),
        V4_7 = new Vector(4,7),
        V8_1 = new Vector(8,1),
        V7_2 = new Vector(7,2)
    );
    KdTree<Vector> tree = new KdTree<>(vectors);

    assertEquals(
        new KdNode<>(V7_2,
            new KdNode<>(V5_4,
                new KdNode<>(V2_3),
                new KdNode<>(V4_7)),
            new KdNode<>(V9_6,
                new KdNode<>(V8_1, null, null),
                null)
        ),
        tree.getRoot()
    );
  }

  @Test
  public void Unique_Nodes_Test() {
    KdTree<Vector> tree = new KdTree<>(vectors);
    List<Vector> copy = vectors.stream()
        .distinct()
        .collect(Collectors.toList());

    for(KdNode<Vector> node : tree) {
      copy.remove(node.getReference());
    }

    assertEquals(Collections.emptyList(), copy);
  }

  @Test
  public void NearestNeighbor_Test() {
    KdTree<Vector> tree = new KdTree<>(vectors);
    Vector find = new Vector(random(RANGE), random(RANGE));
    Vector nearest = tree.nearest(find).getReference();

    assertEquals(
        vectors.stream()
            .min(Comparator.comparingDouble(find::distanceSqTo))
            .orElse(null),
        nearest
    );
  }

  @Test
  public void Radius_Test() {
    final double RAD_SQD = 1024*1024;

    KdTree<Vector> tree = new KdTree<>(vectors);
    Vector origin = new Vector(random(RANGE), random(RANGE));
    List<KdNode<Vector>> nodes = tree.radiusSq(origin, RAD_SQD);

    assertArrayEquals(
        Sets.newHashSet(vectors).stream()
            .filter(vec -> vec.distanceSqTo(origin) < RAD_SQD)
            .sorted(Comparator.comparingDouble(origin::distanceSqTo))
            .toArray(Vector[]::new),
        nodes.stream()
            .sorted(Comparator.comparingDouble(origin::distanceSqTo))
            .map(KdNode::getReference)
            .toArray(Vector[]::new)
    );
  }

  @Test
  public void Find_Test() {
    KdTree<Vector> tree = new KdTree<>(vectors);

    for(Vector vec : vectors)
      assertNotNull("Failed to find vector", tree.find(vec));
  }

  @Test
  public void Iterator_Test() {
    KdTree<Vector> tree = new KdTree<>(vectors);
    List<Vector> copy = Lists.newArrayList(vectors);

    for(KdNode<Vector> node : tree) {
      final int count = node.getRefCount();
      for(int i = 0; i < count; i++)
        assertTrue("Failed to remove element from the list", copy.remove(node.getReference()));
    }

    assertEquals("Iterator did not cover every element in the list", Collections.emptyList(), copy);
  }

  @Test
  public void Remove_Test() {
    KdTree<Vector> tree = new KdTree<>(vectors);

    List<Vector> randomized = Lists.newArrayList(vectors);
    Collections.shuffle(randomized);

    List<Vector> deletes = randomized.stream()
        .distinct()
        .limit((int)((float)SAMPLE_SIZE * 0.1f))
        .collect(Collectors.toList());

    // ensure all duplicates are removed
    while(randomized.removeAll(deletes));

    for(Vector vector : deletes) {
      tree.remove(vector);
    }

    for(Vector vector : randomized) {
      assertNotNull("Failed to find node or parse tree correctly", tree.find(vector));
    }

    for(Vector vector : deletes) {
      assertNull("Contains vector that should be deleted", tree.find(vector));
    }
  }

  private static int randomInt(int range) {
    return ThreadLocalRandom.current().nextInt(range);
  }
  private static int random(int range) {
    return randomInt(range * 2) - range;
  }
}
