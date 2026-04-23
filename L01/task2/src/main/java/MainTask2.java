import java.util.List;

public class MainTask2 {

    public static void main(String[] args) {
        BTree tree = new BTree();

        for (String arg : args) {
            if (arg.equals("print")) {
                printTree(tree);
                continue;
            }

            if (arg.startsWith("?")) {
                handleContains(tree, arg);
                continue;
            }

            if (arg.startsWith("-") && arg.length() > 1) {
                handleDelete(tree, arg);
                continue;
            }

            handleInsert(tree, arg);
        }

        printTree(tree);
    }

    private static void handleInsert(BTree tree, String arg) {
        try {
            int value = Integer.parseInt(arg);
            tree.insert(value);
        } catch (NumberFormatException e) {
            System.err.println("Incorrect Insert Argument " + arg);
        }
    }

    private static void handleDelete(BTree tree, String arg) {
        try {
            int value = Integer.parseInt(arg.substring(1));
            tree.delete(value);
        } catch (NumberFormatException e) {
            System.err.println("Incorrect argument for deletion: " + arg);
        } catch (IllegalArgumentException e) {
            System.err.println("Deletion error " + arg.substring(1) + ": " + e.getMessage());
        }
    }

    private static void handleContains(BTree tree, String arg) {
        try {
            int value = Integer.parseInt(arg.substring(1));
            System.out.println(tree.contains(value));
        } catch (NumberFormatException e) {
            System.err.println("Incorect argument for search: " + arg);
        }
    }

    private static void printTree(BTree tree) {
        List<Integer> values = tree.inOrder();
        if (values.isEmpty()) {
            System.out.println("[]");
            return;
        }
        System.out.println(values);
    }
}