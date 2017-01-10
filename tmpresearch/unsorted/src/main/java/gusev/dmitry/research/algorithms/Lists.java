package gusev.dmitry.research.algorithms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Some usual algorithms with lists (linked, double linked, etc...)
 * @author Gusev Dmitry (vinnypuhh)
 * @version 1.0 (DATE: 07.11.2015)
 */

public class Lists {

    private static final Log log = LogFactory.getLog(Lists.class);

    /** List node. */
    public static class StrNode {
        private String  strValue;
        private StrNode nextNode;

        public StrNode(String strValue, StrNode nextNode) {
            this.strValue = strValue;
            this.nextNode = nextNode;
        }

        public String getStrValue() {
            return strValue;
        }

        public void setStrValue(String strValue) {
            this.strValue = strValue;
        }

        public StrNode getNextNode() {
            return nextNode;
        }

        public void setNextNode(StrNode nextNode) {
            this.nextNode = nextNode;
        }
    }

    /***/
    public static StrNode generateLinkedList(int length) {
        log.debug("Lists.generateLinkedList() working.");
        StrNode node = null;
        // generating linked list, from end to begin
        if (length > 0) {
            StrNode currentNode;
            for (int i = length - 1; i >= 0; i--) {
                if (i == length) { // last (end) node
                    currentNode = new StrNode(String.format("[Node #%s]", i), node);
                    node = currentNode;
                } else { // usual node
                    currentNode = new StrNode(String.format("[Node #%s]", i), node);
                    node = currentNode;
                }
            } // end of FOR - list generation loop
        }
        return node;
    }

    /**
     * Non-recursive method for building string view of list.
     * @param node StrNode head of list.
     * @return String string view of linked list.
     */
    public static String getListStrView(StrNode node) {
        log.debug("Lists.getListStrView() working.");
        StringBuilder result = null;
        if (node != null) {
            result = new StringBuilder().append(node.getStrValue()).append(" ");
            StrNode currentNode = node;
            while (currentNode.getNextNode() != null) {
                currentNode = currentNode.getNextNode();
                result.append(currentNode.getStrValue()).append(" ");
            }
        }
        return (result == null ? null : result.toString().trim());
    }

    /**
     * Recursive method for building string view of list.
     * @param node StrNode head of list.
     * @return String string view of linked list.
     */
    public static String getListStrViewRecursive(StrNode node) {
        if (node != null) {
            return node.getStrValue() +
                    (node.getNextNode() == null ? "" : " " + Lists.getListStrViewRecursive(node.getNextNode()));
        } else {
            return null;
        }
    }

    /***/
    public static StrNode inverseList(StrNode node) {
        log.debug("Lists.inverseList() working.");
        StrNode result = null;
        if (node != null) {
            // initial state
            StrNode prevNode    = null;
            StrNode currentNode = node;
            StrNode nextNode    = node.getNextNode();
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

    /***/
    public static void main(String[] args) {
        Log log = LogFactory.getLog(Lists.class);
        log.info("starting...");

        StrNode node = Lists.generateLinkedList(9);
        //StrNode node = null;
        log.info("non-recursive -> " + Lists.getListStrView(node));
        log.info("recursive     -> " + Lists.getListStrViewRecursive(node));
        log.info("-> " + Lists.getListStrView(Lists.inverseList(node)));
    }

}