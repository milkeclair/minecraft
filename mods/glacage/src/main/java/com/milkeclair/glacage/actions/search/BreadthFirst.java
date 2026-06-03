package com.milkeclair.glacage.actions.search;

import com.milkeclair.glacage.actions.search.breadthFirst.Node;
import com.milkeclair.glacage.actions.search.breadthFirst.OverflowPolicy;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/** BFSのアルゴリズム。 */
public class BreadthFirst<T> {
  private final Collection<Node<T>> starts;
  private final Predicate<Node<T>> isInsideArea;
  private final Predicate<Node<T>> isCollectable;
  private final Function<Node<T>, Collection<T>> neighbors;
  private final int maxNodes;
  private final OverflowPolicy overflowPolicy;

  public BreadthFirst(
      Collection<Node<T>> starts,
      Predicate<Node<T>> isInsideArea,
      Predicate<Node<T>> isCollectable,
      Function<Node<T>, Collection<T>> neighbors,
      int maxNodes,
      OverflowPolicy overflowPolicy) {
    if (maxNodes < 1) {
      throw new IllegalArgumentException("maxNodes must be greater than 0");
    }

    this.starts = starts;
    this.isInsideArea = isInsideArea;
    this.isCollectable = isCollectable;
    this.neighbors = neighbors;
    this.maxNodes = maxNodes;
    this.overflowPolicy = overflowPolicy;
  }

  /** ノードの収集。 */
  public LinkedHashSet<Node<T>> collect() {
    return search(node -> false).nodes();
  }

  /** 経路の取得。 valueがtargetと一致するノードのルートを探す。 */
  public Optional<List<T>> routeTo(T target) {
    return routeTo(node -> Objects.equals(node.value(), target));
  }

  /** 経路の取得。 isTargetなノードのルートを探す。 */
  public Optional<List<T>> routeTo(Predicate<Node<T>> isTarget) {
    return search(isTarget).route();
  }

  private Result<T> search(Predicate<Node<T>> isTarget) {
    var collected = new LinkedHashSet<Node<T>>();
    var visited = new HashSet<T>();
    var queue = new ArrayDeque<Node<T>>();

    for (var start : starts) {
      enqueue(start, visited, queue);
    }

    while (!queue.isEmpty()) {
      if (collected.size() == maxNodes) {
        return new Result<>(overflow(collected), Optional.empty());
      }

      var current = queue.removeFirst();
      collected.add(current);
      if (isTarget.test(current)) {
        return new Result<>(collected, Optional.of(current.path()));
      }

      for (var neighbor : neighbors.apply(current)) {
        enqueue(current.next(neighbor), visited, queue);
      }
    }

    return new Result<>(collected, Optional.empty());
  }

  private void enqueue(Node<T> node, HashSet<T> visited, ArrayDeque<Node<T>> queue) {
    if (visited.contains(node.value()) || !isInsideArea.test(node)) {
      return;
    }

    if (!isCollectable.test(node)) {
      return;
    }

    visited.add(node.value());
    queue.add(node);
  }

  private LinkedHashSet<Node<T>> overflow(LinkedHashSet<Node<T>> collected) {
    if (overflowPolicy == OverflowPolicy.EMPTY) {
      return new LinkedHashSet<>();
    }

    return collected;
  }

  private record Result<T>(LinkedHashSet<Node<T>> nodes, Optional<List<T>> route) {}
}
