package example;

public final class Node<T> implements Tree<T> {
    private final T value;
    private final Tree<T> left, right;

    public Node(T value, Tree<T> left, Tree<T> right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return "Node("+value+", "+left+", "+right+")";
    }
}