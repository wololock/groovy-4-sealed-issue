package example;

public class Main {
    public static void main(String[] args) {
        Tree<Integer> tree = new Node<Integer>(42, new Node<>(0, new Empty(), new Empty()), new Empty());
        System.out.println(tree.toString());

        System.out.println("\nIT WORKS!");
    }
}
