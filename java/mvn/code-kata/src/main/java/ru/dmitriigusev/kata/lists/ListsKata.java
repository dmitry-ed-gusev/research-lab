package ru.dmitriigusev.kata.lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Some usual algorithms with lists (linked, double linked, etc...)
 * @author Gusev Dmitry (vinnypuhh)
 * @version 1.0 (DATE: 07.11.2015)
 */

@Slf4j
public final class ListsKata {

    private ListsKata() {}

    /***/
    public static StringListNode generateLinkedList(int length) {
        log.debug("Lists.generateLinkedList() working.");
        StringListNode node = null;
        // generating linked list, from end to begin
        if (length > 0) {
            StringListNode currentNode;
            for (int i = length - 1; i >= 0; i--) {
                if (i == length) { // last (end) node
                    currentNode = new StringListNode(String.format("[Node #%s]", i), node);
                    node = currentNode;
                } else { // usual node
                    currentNode = new StringListNode(String.format("[Node #%s]", i), node);
                    node = currentNode;
                }
            } // end of FOR - list generation loop
        }
        return node;
    }

    /**
     * Non-recursive method for building string view of list.
     * @param node StringListNode head of list.
     * @return String string view of linked list.
     */
    public static String getListStrView(StringListNode node) {
        log.debug("Lists.getListStrView() working.");
        StringBuilder result = null;
        if (node != null) {
            result = new StringBuilder().append(node.getStrValue()).append(" ");
            StringListNode currentNode = node;
            while (currentNode.getNextNode() != null) {
                currentNode = currentNode.getNextNode();
                result.append(currentNode.getStrValue()).append(" ");
            }
        }
        return (result == null ? null : result.toString().trim());
    }

    /**
     * Recursive method for building string view of list.
     * @param node StringListNode head of list.
     * @return String string view of linked list.
     */
    public static String getListStrViewRecursive(StringListNode node) {
        if (node != null) {
            return node.getStrValue() +
                    (node.getNextNode() == null ? "" : " " + ListsKata.getListStrViewRecursive(node.getNextNode()));
        } else {
            return null;
        }
    }

    /***/
    public static StringListNode inverseList(StringListNode node) {
        log.debug("Lists.inverseList() working.");
        StringListNode result = null;
        if (node != null) {
            // initial state
            StringListNode prevNode    = null;
            StringListNode currentNode = node;
            StringListNode nextNode    = node.getNextNode();
            while (nextNode != null) { // iterate over list and reverse it
                currentNode.setNextNode(prevNode);
                prevNode    = currentNode;
                currentNode = nextNode;
                nextNode    = nextNode.getNextNode();
            }
            currentNode.setNextNode(prevNode);
            result = currentNode;
        }
        return result;
    }

//    /***/
//    public static void main(String[] args) {
//        Log log = LogFactory.getLog(Lists.class);
//        log.info("starting...");
//
//        StringListNode node = Lists.generateLinkedList(9);
//        //StringListNode node = null;
//        log.info("non-recursive -> " + Lists.getListStrView(node));
//        log.info("recursive     -> " + Lists.getListStrViewRecursive(node));
//        log.info("-> " + Lists.getListStrView(Lists.inverseList(node)));
//    }

}