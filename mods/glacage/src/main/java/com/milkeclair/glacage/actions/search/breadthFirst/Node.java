package com.milkeclair.glacage.actions.search.breadthFirst;

import java.util.ArrayList;
import java.util.List;

/** BFSのノード。 */
public record Node<T>(T value, int distance, List<T> path) {
  public Node(T value) {
    this(value, 0, List.of(value));
  }

  public Node(T value, int distance) {
    this(value, distance, List.of(value));
  }

  public Node {
    // deep copy.
    path = List.copyOf(path);
  }

  /** 次のノードを作成する。 */
  public Node<T> next(T value) {
    var nextPath = new ArrayList<>(path);
    nextPath.add(value);

    return new Node<>(value, distance + 1, nextPath);
  }
}
