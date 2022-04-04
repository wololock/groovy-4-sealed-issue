package example.more

Tree<Integer> tree = new Node<Integer>(42, new Node<>(0, Empty.instance, Empty.instance), Empty.instance)
assert tree.toString() == 'Node(42, Node(0, Empty, Empty), Empty)'

println "IT WORKS!"