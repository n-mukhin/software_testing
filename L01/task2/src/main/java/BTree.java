import java.util.ArrayList;
import java.util.List;

public class BTree {

    private static final int T = 2;
    private static final int MAX_KEYS = 2 * T - 1;
    private static final int MIN_KEYS = T - 1;

    public static final class Node {
        boolean leaf;
        final ArrayList<Integer> keys = new ArrayList<>(MAX_KEYS);
        final ArrayList<Node> children = new ArrayList<>(MAX_KEYS + 1);

        Node(boolean leaf) { this.leaf = leaf; }
        int keyCount() { return keys.size(); }
    }

    private Node root = new Node(true);

    public Node getRoot() { return root; }

    public boolean contains(int key) { return search(root, key) != null; }

    public List<Integer> inOrder() {
        ArrayList<Integer> res = new ArrayList<>();
        inOrder(root, res);
        return res;
    }

    public void insert(int key) {
        if (contains(key)) return;

        Node r = root;
        if (r.keyCount() == MAX_KEYS) {
            Node s = new Node(false);
            s.children.add(r);
            root = s;
            splitChild(s, 0);
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
    }

    public void delete(int key) {
        if (!contains(key)) throw new IllegalArgumentException("Key not found");

        delete(root, key);
        if (root.keyCount() == 0 && !root.leaf) root = root.children.get(0);
    }


    private Node search(Node x, int k) {
        int i = 0;
        while (i < x.keyCount() && k > x.keys.get(i)) i++;
        if (i < x.keyCount() && k == x.keys.get(i)) return x;
        if (x.leaf) return null;
        return search(x.children.get(i), k);
    }

    private void inOrder(Node x, List<Integer> out) {
        if (x.leaf) { out.addAll(x.keys); return; }
        for (int i = 0; i < x.keyCount(); i++) {
            inOrder(x.children.get(i), out);
            out.add(x.keys.get(i));
        }
        inOrder(x.children.get(x.keyCount()), out);
    }

    private void insertNonFull(Node x, int k) {
        int i = x.keyCount() - 1;

        if (x.leaf) {
            x.keys.add(0);
            while (i >= 0 && k < x.keys.get(i)) {
                x.keys.set(i + 1, x.keys.get(i));
                i--;
            }
            x.keys.set(i + 1, k);
        } else {
            while (i >= 0 && k < x.keys.get(i)) i--;
            i++;

            Node child = x.children.get(i);
            if (child.keyCount() == MAX_KEYS) {
                splitChild(x, i);
                if (k > x.keys.get(i)) i++;
            }
            insertNonFull(x.children.get(i), k);
        }
    }

    private void splitChild(Node x, int i) {
        Node y = x.children.get(i);
        Node z = new Node(y.leaf);

        int median = y.keys.get(T - 1);

        for (int j = 0; j < T - 1; j++)
            z.keys.add(y.keys.get(j + T));

        if (!y.leaf)
            for (int j = 0; j < T; j++)
                z.children.add(y.children.get(j + T));

        while (y.keys.size() > T - 1)
            y.keys.remove(y.keys.size() - 1);

        if (!y.leaf)
            while (y.children.size() > T)
                y.children.remove(y.children.size() - 1);

        x.children.add(i + 1, z);
        x.keys.add(i, median);
    }

    private void delete(Node x, int k) {
        int idx = findKeyIndex(x, k);

        if (idx < x.keyCount() && x.keys.get(idx) == k) {
            if (x.leaf) {
                x.keys.remove(idx);
            } else {
                deleteFromInternal(x, idx);
            }
            return;
        }

        if (x.leaf) throw new IllegalArgumentException("Key not found");

        if (x.children.get(idx).keyCount() == MIN_KEYS) {
            idx = fixChildMinKeys(x, idx);
        }

        delete(x.children.get(idx), k);
    }

    private int findKeyIndex(Node x, int k) {
        int i = 0;
        while (i < x.keyCount() && x.keys.get(i) < k) i++;
        return i;
    }

    private void deleteFromInternal(Node x, int idx) {
        int k = x.keys.get(idx);
        Node left = x.children.get(idx);
        Node right = x.children.get(idx + 1);

        if (left.keyCount() >= T) {
            int pred = getPredecessor(left);
            x.keys.set(idx, pred);
            delete(left, pred);
        } else if (right.keyCount() >= T) {
            int succ = getSuccessor(right);
            x.keys.set(idx, succ);
            delete(right, succ);
        } else {
            Node merged = mergeChildren(x, idx);
            delete(merged, k);
        }
    }

    private int getPredecessor(Node x) {
        Node cur = x;
        while (!cur.leaf) cur = cur.children.get(cur.keyCount());
        return cur.keys.get(cur.keyCount() - 1);
    }

    private int getSuccessor(Node x) {
        Node cur = x;
        while (!cur.leaf) cur = cur.children.get(0);
        return cur.keys.get(0);
    }

    private int fixChildMinKeys(Node x, int idx) {
        if (idx > 0 && x.children.get(idx - 1).keyCount() >= T) {
            borrowFromPrev(x, idx);
            return idx;
        }

        if (idx < x.children.size() - 1 && x.children.get(idx + 1).keyCount() >= T) {
            borrowFromNext(x, idx);
            return idx;
        }

        if (idx < x.keyCount()) {
            mergeChildren(x, idx);
            return idx;
        } else {
            mergeChildren(x, idx - 1);
            return idx - 1;
        }
    }

    private void borrowFromPrev(Node x, int idx) {
        Node child = x.children.get(idx);
        Node sibling = x.children.get(idx - 1);

        child.keys.add(0, x.keys.get(idx - 1));
        if (!child.leaf)
            child.children.add(0, sibling.children.remove(sibling.children.size() - 1));

        x.keys.set(idx - 1, sibling.keys.remove(sibling.keyCount() - 1));
    }

    private void borrowFromNext(Node x, int idx) {
        Node child = x.children.get(idx);
        Node sibling = x.children.get(idx + 1);

        child.keys.add(x.keys.get(idx));
        if (!child.leaf)
            child.children.add(sibling.children.remove(0));

        x.keys.set(idx, sibling.keys.remove(0));
    }

    private Node mergeChildren(Node x, int idx) {
        Node left = x.children.get(idx);
        Node right = x.children.get(idx + 1);

        left.keys.add(x.keys.remove(idx));
        left.keys.addAll(right.keys);

        if (!left.leaf) left.children.addAll(right.children);

        x.children.remove(idx + 1);
        return left;
    }
}