package com.milkeclair.glacage.actions.search.breadthFirst;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Node")
class NodeTest {
  @Nested
  @DisplayName("new")
  class New {
    @Nested
    @DisplayName("値だけを渡した場合")
    class ValueOnly {
      @Test
      @DisplayName("距離0の開始ノードを作る")
      void createsStartNode() {
        var node = new Node<>("A");

        assertThat(node.value()).isEqualTo("A");
        assertThat(node.distance()).isZero();
        assertThat(node.path()).containsExactly("A");
      }
    }

    @Nested
    @DisplayName("経路を渡した場合")
    class GivenPath {
      @Test
      @DisplayName("経路をコピーして保持する")
      void copiesPath() {
        var path = new ArrayList<>(List.of("A", "B"));
        var node = new Node<>("B", 1, path);
        path.add("C");

        assertThat(node.path()).containsExactly("A", "B");
      }
    }
  }

  @Nested
  @DisplayName("#next")
  class Next {
    @Nested
    @DisplayName("次の値を渡した場合")
    class GivenValue {
      @Test
      @DisplayName("距離と経路を進めたノードを返す")
      void returnsNextNode() {
        var node = new Node<>("A");

        var next = node.next("B");

        assertThat(next.value()).isEqualTo("B");
        assertThat(next.distance()).isEqualTo(1);
        assertThat(next.path()).containsExactly("A", "B");
      }
    }
  }
}
