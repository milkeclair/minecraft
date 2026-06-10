package com.milkeclair.glacage.actions.search.breadthFirst;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("BreadthFirst")
class BreadthFirstTest {
  @Nested
  @DisplayName("#collect")
  class Collect {
    @Nested
    @DisplayName("辿れるノードがある場合")
    class ReachableNodes {
      @Test
      @DisplayName("幅優先順に返す")
      void returnsNodesInBreadthFirstOrder() {
        var search =
            new BreadthFirst<>(
                List.of(new Node<>("A")),
                node -> true,
                node -> true,
                node ->
                    switch (node.value()) {
                      case "A" -> List.of("B", "C");
                      case "B" -> List.of("D");
                      default -> List.of();
                    },
                10,
                OverflowPolicy.ELLIPSIS);

        var nodes = search.collect();

        assertThat(nodes).extracting(Node::value).containsExactly("A", "B", "C", "D");
      }
    }

    @Nested
    @DisplayName("同じノードに複数経路で到達できる場合")
    class DuplicatedNode {
      @Test
      @DisplayName("最初に見つけた経路だけを返す")
      void returnsFirstPathOnly() {
        var search =
            new BreadthFirst<>(
                List.of(new Node<>("A")),
                node -> true,
                node -> true,
                node ->
                    switch (node.value()) {
                      case "A" -> List.of("B", "C");
                      case "B", "C" -> List.of("D");
                      default -> List.of();
                    },
                10,
                OverflowPolicy.ELLIPSIS);

        var nodes = search.collect();

        assertThat(nodes).extracting(Node::value).containsExactly("A", "B", "C", "D");
        assertThat(nodes.getLast().path()).containsExactly("A", "B", "D");
      }
    }

    @Nested
    @DisplayName("探索範囲外のノードがある場合")
    class OutsideArea {
      @Test
      @DisplayName("探索範囲内のノードだけを返す")
      void returnsInsideNodesOnly() {
        var search =
            new BreadthFirst<>(
                List.of(new Node<>("A")),
                node -> !node.value().equals("C"),
                node -> true,
                node ->
                    switch (node.value()) {
                      case "A" -> List.of("B", "C");
                      case "C" -> List.of("D");
                      default -> List.of();
                    },
                10,
                OverflowPolicy.ELLIPSIS);

        var nodes = search.collect();

        assertThat(nodes).extracting(Node::value).containsExactly("A", "B");
      }
    }

    @Nested
    @DisplayName("収集対象ではないノードがある場合")
    class NotCollectable {
      @Test
      @DisplayName("収集対象のノードだけを返す")
      void returnsCollectableNodesOnly() {
        var search =
            new BreadthFirst<>(
                List.of(new Node<>("A")),
                node -> true,
                node -> !node.value().equals("C"),
                node ->
                    switch (node.value()) {
                      case "A" -> List.of("B", "C");
                      case "C" -> List.of("D");
                      default -> List.of();
                    },
                10,
                OverflowPolicy.ELLIPSIS);

        var nodes = search.collect();

        assertThat(nodes).extracting(Node::value).containsExactly("A", "B");
      }
    }

    @Nested
    @DisplayName("overflowPolicyがELLIPSISの場合")
    class Ellipsis {
      @Test
      @DisplayName("最大件数まで返す")
      void returnsUpToMaxNodes() {
        var search =
            new BreadthFirst<>(
                List.of(new Node<>("A")),
                node -> true,
                node -> true,
                node ->
                    switch (node.value()) {
                      case "A" -> List.of("B");
                      case "B" -> List.of("C");
                      default -> List.of();
                    },
                2,
                OverflowPolicy.ELLIPSIS);

        var nodes = search.collect();

        assertThat(nodes).extracting(Node::value).containsExactly("A", "B");
      }
    }

    @Nested
    @DisplayName("overflowPolicyがEMPTYの場合")
    class Empty {
      @Test
      @DisplayName("空のSetを返す")
      void returnsEmptySet() {
        var search =
            new BreadthFirst<>(
                List.of(new Node<>("A")),
                node -> true,
                node -> true,
                node ->
                    switch (node.value()) {
                      case "A" -> List.of("B");
                      case "B" -> List.of("C");
                      default -> List.of();
                    },
                2,
                OverflowPolicy.EMPTY);

        var nodes = search.collect();

        assertThat(nodes).isEmpty();
      }
    }

    @Nested
    @DisplayName("最大件数が1未満の場合")
    class InvalidMaxNodes {
      @Test
      @DisplayName("例外を投げる")
      void raisesError() {
        assertThatThrownBy(
                () ->
                    new BreadthFirst<>(
                        List.of(new Node<>("A")),
                        node -> true,
                        node -> true,
                        node -> List.of(),
                        0,
                        OverflowPolicy.ELLIPSIS))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }
  }

  @Nested
  @DisplayName("#routeTo")
  class RouteTo {
    @Nested
    @DisplayName("対象に到達できる場合")
    class ReachableTarget {
      @Test
      @DisplayName("対象までの最短経路を返す")
      void returnsShortestPathToTarget() {
        var search =
            new BreadthFirst<>(
                List.of(new Node<>("A")),
                node -> true,
                node -> true,
                node ->
                    switch (node.value()) {
                      case "A" -> List.of("B", "C");
                      case "B" -> List.of("E");
                      case "C", "E" -> List.of("D");
                      default -> List.of();
                    },
                10,
                OverflowPolicy.ELLIPSIS);

        var route = search.routeTo("D");

        assertThat(route).contains(List.of("A", "C", "D"));
      }
    }

    @Nested
    @DisplayName("開始地点が対象の場合")
    class StartIsTarget {
      @Test
      @DisplayName("開始地点だけの経路を返す")
      void returnsStartPath() {
        var search =
            new BreadthFirst<>(
                List.of(new Node<>("A")),
                node -> true,
                node -> true,
                node -> List.of("B"),
                10,
                OverflowPolicy.ELLIPSIS);

        var route = search.routeTo("A");

        assertThat(route).contains(List.of("A"));
      }
    }

    @Nested
    @DisplayName("対象に到達できない場合")
    class UnreachableTarget {
      @Test
      @DisplayName("空を返す")
      void returnsEmpty() {
        var search =
            new BreadthFirst<>(
                List.of(new Node<>("A")),
                node -> true,
                node -> true,
                node ->
                    switch (node.value()) {
                      case "A" -> List.of("B");
                      default -> List.of();
                    },
                10,
                OverflowPolicy.ELLIPSIS);

        var route = search.routeTo("D");

        assertThat(route).isEmpty();
      }
    }

    @Nested
    @DisplayName("対象条件を渡す場合")
    class TargetPredicate {
      @Test
      @DisplayName("条件に一致するノードまでの経路を返す")
      void returnsPathToMatchingNode() {
        var search =
            new BreadthFirst<>(
                List.of(new Node<>("A")),
                node -> true,
                node -> true,
                node ->
                    switch (node.value()) {
                      case "A" -> List.of("B");
                      case "B" -> List.of("C");
                      default -> List.of();
                    },
                10,
                OverflowPolicy.ELLIPSIS);

        var route = search.routeTo(node -> node.distance() == 2);

        assertThat(route).contains(List.of("A", "B", "C"));
      }
    }

    @Nested
    @DisplayName("探索上限より先に対象がある場合")
    class OverMaxNodes {
      @Test
      @DisplayName("空を返す")
      void returnsEmpty() {
        var search =
            new BreadthFirst<>(
                List.of(new Node<>("A")),
                node -> true,
                node -> true,
                node ->
                    switch (node.value()) {
                      case "A" -> List.of("B");
                      case "B" -> List.of("C");
                      default -> List.of();
                    },
                2,
                OverflowPolicy.ELLIPSIS);

        var route = search.routeTo("C");

        assertThat(route).isEmpty();
      }
    }
  }
}
