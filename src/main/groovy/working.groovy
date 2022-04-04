import groovy.transform.Canonical

sealed interface Tree<T> {}

@Singleton final class Empty implements Tree {
    String toString() { 'Empty' }
}

@Canonical final class Node<T> implements Tree<T> {
    T value
    Tree<T> left, right

    String toString() {
        "Node($value, $left, $right)"
    }
}

Tree<Integer> tree = new Node<Integer>(42, new Node<>(0, Empty.instance, Empty.instance), Empty.instance)
assert tree.toString() == 'Node(42, Node(0, Empty, Empty), Empty)'

println "IT WORKS!"