package example.more

import groovy.transform.Canonical

@Canonical final class Node<T> implements Tree<T> {
    T value
    Tree<T> left, right

    String toString() {
        "Node($value, $left, $right)"
    }
}