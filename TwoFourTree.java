// Connor Rick
// COP 3503 Summer 2023

public class TwoFourTree {
    private class TwoFourTreeItem {
        int values = 1;
        int value1 = 0;                             // always exists.
        int value2 = 0;                             // exists iff the node is a 3-node or 4-node.
        int value3 = 0;                             // exists iff the node is a 4-node.
        boolean isLeaf = true;

        TwoFourTreeItem parent = null;              // parent exists iff the node is not root.
        TwoFourTreeItem leftChild = null;           // left and right child exist iff the note is a non-leaf.
        TwoFourTreeItem rightChild = null;          
        TwoFourTreeItem centerChild = null;         // center child exists iff the node is a non-leaf 3-node.
        TwoFourTreeItem centerLeftChild = null;     // center-left and center-right children exist iff the node is a non-leaf 4-node.
        TwoFourTreeItem centerRightChild = null;

        public boolean isTwoNode() {
            return (values == 1);
        }

        public boolean isThreeNode() {
            return (values == 2);
        }

        public boolean isFourNode() {
            return (values == 3);
        }

        public boolean isRoot() {
            return (parent == null);
        }

        // Check if a node contains 'value'
        public boolean contains(int value)
        {
            if (value == value1)
                return true;
            else if (values >= 2 && value == value2)
                return true;
            else if (isFourNode() && value == value3)
                return true;

            return false;
        }

        public boolean isLeaf()
        {
            if (leftChild == null && centerLeftChild == null && centerChild == null && 
                centerRightChild == null && rightChild == null)
                return true;
            return false;
        }

        public TwoFourTreeItem(int value1) {
            values = 1;
            this.value1 = value1;
        }

        public TwoFourTreeItem(int value1, int value2) {
            values = 2;
            this.value1 = value1;
            this.value2 = value2;
        }

        public TwoFourTreeItem(int value1, int value2, int value3) {
            values = 3;
            this.value1 = value1;
            this.value2 = value2;
            this.value3 = value3;
        }

        private void printIndents(int indent) {
            for(int i = 0; i < indent; i++) System.out.printf("  ");
        }

        public void printInOrder(int indent) {
            if(!isLeaf) leftChild.printInOrder(indent + 1);
            printIndents(indent);
            System.out.printf("%d\n", value1);
            if(isThreeNode()) {
                if(!isLeaf) centerChild.printInOrder(indent + 1);
                printIndents(indent);
                System.out.printf("%d\n", value2);
            } else if(isFourNode()) {
                if(!isLeaf) centerLeftChild.printInOrder(indent + 1);
                printIndents(indent);
                System.out.printf("%d\n", value2);
                if(!isLeaf) centerRightChild.printInOrder(indent + 1);
                printIndents(indent);
                System.out.printf("%d\n", value3);
            }
            if(!isLeaf) rightChild.printInOrder(indent + 1);
        }
    }

    TwoFourTreeItem root = null;

    // Check if value is in the tree
    public boolean hasValue(int value) {
        if (findNode(root, value) == null)
            return false;
        return true;
    }

    public boolean addValue(int value) {
        // Create a new 2-node if the tree is empty
        if (root == null)
        {
            root = new TwoFourTreeItem(value);
            return false;
        }

        // Return if the value is already in the tree
        if (hasValue(value))
            return true;

        // Split the root if it's a 4-node
        if (root.isFourNode())
            root = splitNode(root);
        
        TwoFourTreeItem insertionNode = findInsertionNode(value);
        // Add value to the insertion node
        if (insertionNode.isTwoNode())
        {
            if (value < insertionNode.value1)
            {
                insertionNode.value2 = insertionNode.value1;
                insertionNode.value1 = value;
            }
            else
                insertionNode.value2 = value;
        }
        else if (insertionNode.isThreeNode())
        {
            if (value < insertionNode.value1)
            {
                insertionNode.value3 = insertionNode.value2;
                insertionNode.value2 = insertionNode.value1;
                insertionNode.value1 = value;
            }
            else if (value < insertionNode.value2)
            {
                insertionNode.value3 = insertionNode.value2;
                insertionNode.value2 = value;
            }
            else
                insertionNode.value3 = value;
        }

        insertionNode.values++;
        return false;
    }

    // Removes 'value' from the tree
    public boolean deleteValue(int value)
    {
        // Return if the value is not found
        if (!hasValue(value))
            return false;

        // Handle edge cases where the root is the only node
        if (root.isLeaf)
        {
            if (root.isTwoNode())
                root = null;
            else 
                removeFromNode(root, value);
            return true;
        }

        // Merge the root if the root and children are 2-nodes
        if (root.isTwoNode() && root.leftChild.isTwoNode() && root.rightChild.isTwoNode())
            root = mergeRoot();

        TwoFourTreeItem current = root;
        TwoFourTreeItem leftSib = null;
        TwoFourTreeItem rightSib = null;
        TwoFourTreeItem parent = null;

        boolean flag = false;
        boolean successorFound = false;
        int successor = 0;
        int oldValue = 0;

        while (flag == false)
        {
            if (current.isTwoNode() && current != root)
            {
                // Rotate a value into current from the right sibling
                if (rightSib != null && !rightSib.isTwoNode())
                    current = leftRotate(current, rightSib);
                // Rotate a value into current from the left sibling
                else if (leftSib != null && !leftSib.isTwoNode())
                    current = rightRotate(current, leftSib);
                // Merge current with the right sibling
                else if (rightSib != null)
                    current = mergeNodes(current, rightSib);
                // Merge current with the left sibling
                else
                    current = mergeNodes(leftSib, current);
            }
            else
            {
                if (current.contains(value))
                {
                    // Remove value if value is in a leaf
                    if (current.isLeaf)
                    {
                        current = removeFromNode(current, value);
                        flag = true;
                        
                        // If the successor was removed, replace the original
                        // value with the successor
                        if (successorFound)
                        {
                            TwoFourTreeItem oldValNode = findNode(root, oldValue);
                            if (oldValNode.value1 == oldValue)
                                oldValNode.value1 = successor;
                            else if (oldValNode.value2 == oldValue)
                                oldValNode.value2 = successor;
                            else
                                oldValNode.value3 = successor;
                        }
                    }
                    // Remove value's successor
                    else
                    {
                        TwoFourTreeItem snode = findSuccessorNode(current, value);
                        successor = snode.value1;

                        oldValue = value;
                        successorFound = true;
                        value = successor;
                    }
                }
                else
                {
                    // Should be called in the case that the root is a 2-node
                    if (current.isTwoNode())
                    {
                        if (value < current.value1)
                        {
                            leftSib = null;
                            rightSib = current.rightChild;
                            current = current.leftChild;
                        }
                        else
                        {
                            leftSib = current.leftChild;
                            rightSib = null;
                            current = current.rightChild;
                        }
                    }
                    else if (current.isThreeNode()) // 3-node traversal
                    {
                        if (value < current.value1)
                        {
                            leftSib = null;
                            rightSib = current.centerChild;
                            current = current.leftChild;
                        }
                        else if (value < current.value2)
                        {
                            leftSib = current.leftChild;
                            rightSib = current.rightChild;
                            current = current.centerChild;
                        }
                        else
                        {
                            leftSib = current.centerChild;
                            rightSib = null;
                            current = current.rightChild;
                        }
                        parent = current;
                    }
                    else // 4-node traversal
                    {
                        if (value < current.value1)
                        {
                            leftSib = null;
                            rightSib = current.centerLeftChild;
                            current = current.leftChild;
                        }
                        else if (value < current.value2)
                        {
                            leftSib = current.leftChild;
                            rightSib = current.centerRightChild;
                            current = current.centerLeftChild;
                        }
                        else if (value < current.value3)              
                        {
                            leftSib = current.centerLeftChild;
                            rightSib = current.rightChild;
                            current = current.centerRightChild;
                        }
                        else
                        {
                            leftSib = current.centerRightChild;
                            rightSib = null;
                            current = current.rightChild;
                        }
                        parent = current;
                    }
                }
            }
        }
        return flag;
    }

    // Find the node to insert value into, splitting accordingly
    // Assumes root exists and that 'value' is not in the tree
    public TwoFourTreeItem findInsertionNode(int value)
    {
        // Assume root exists, this is checked prior
        TwoFourTreeItem temp = root;
        // Keep traversing as long as temp isn't a leaf or temp is a four node
        while (!temp.isLeaf || temp.isFourNode())
        {
            if (temp.isTwoNode())
            {
                if (value < temp.value1)
                    temp = temp.leftChild;
                else
                    temp = temp.rightChild;
            }
            else if (temp.isThreeNode())
            {
                if (value < temp.value1)
                    temp = temp.leftChild;
                else if (value < temp.value2)
                    temp = temp.centerChild;
                else
                    temp = temp.rightChild;
            }
            else
            {
                temp = splitNode(temp);
                // Parent became a 4-node after the split
                if (temp.isFourNode())
                {
                    if (value < temp.value1)
                        temp = temp.leftChild;
                    else if (value < temp.value2)
                        temp = temp.centerLeftChild;
                    else if (value < temp.value3)
                        temp = temp.centerRightChild;
                    else
                        temp = temp.rightChild;
                }
            } 
        }
        return temp;
    }

    // Find the node which contains 'value'
    // Returns null if the value is not found
    public TwoFourTreeItem findNode(TwoFourTreeItem root, int value) {
        if (root == null)
            return null;
        
        if (root.isTwoNode())
        {
            // Go left if value is less than the root's low value
            if (value < root.value1)
                return findNode(root.leftChild, value);
            // Go right if value is greater than the root's high value
            else if (value > root.value1)
                return findNode(root.rightChild, value);
            // The root contains the value we're looking for
            else
                return root;
        }
        else if (root.isThreeNode())
        {
            // Go left if value is less than the root's low value
            if (value < root.value1)
                return findNode(root.leftChild, value);
            // Go center if value is between the root's two values
            else if (value > root.value1 && value < root.value2)
                return findNode(root.centerChild, value);
            // Go right if value is greater than the root's high value
            else if (value > root.value2)
                return findNode(root.rightChild, value);
            // The root contains the value we're looking for
            else
                return root;
        }
        else if (root.isFourNode())
        {
            // Go left if value is less than the root's low value
            if (value < root.value1)
                return findNode(root.leftChild, value);
            // Go center-left if value is between the root's low and middle values
            else if (value > root.value1 && value < root.value2)
                return findNode(root.centerLeftChild, value);
            // Go center-right if value is between the root's middle and high values
            else if (value > root.value2 && value < root.value3)
                return findNode(root.centerRightChild, value);
            // Go right if value is greater than the root's high value
            else if (value > root.value3)
                return findNode(root.rightChild, value);
            // The root contains the value we're looking for
            else
                return root;
        }
        return null;
    }

    // Finds the node containing the predecessor to value
    // Assumes 'node' contains 'value' and is not a leaf
    public TwoFourTreeItem findSuccessorNode(TwoFourTreeItem node, int value)
    {
        if (node.isLeaf)
            return null;

        TwoFourTreeItem temp;

        if (node.isTwoNode())
            temp = node.rightChild;
        // For 3 and 4-nodes, check where the value is placed in node and traverse
        // to the child directly to the right of the given value
        else if (node.isThreeNode())
        {
            if (value == node.value1)
                temp = node.centerChild;
            else
                temp = node.rightChild;
        }
        else
        {
            if (value == node.value1)
                temp = node.centerLeftChild;
            else if (value == node.value2)
                temp = node.centerRightChild;
            else
                temp = node.rightChild;
        }

        // Continue traversing all the way left
        while (temp.leftChild != null)
            temp = temp.leftChild;

        return temp;
    }
    
    // Link the old 4-node's children to the new left and right 2-nodes
    public void linkSplitNodeChildren(TwoFourTreeItem fournode, TwoFourTreeItem leftnode, 
                                    TwoFourTreeItem rightnode, TwoFourTreeItem parent)
    {
        // Set the children of the 2-nodes to the children of the old 4-node
        leftnode.leftChild = fournode.leftChild;
        leftnode.rightChild = fournode.centerLeftChild;
        rightnode.leftChild = fournode.centerRightChild;
        rightnode.rightChild = fournode.rightChild;
        // Set the parents of the 2-nodes
        leftnode.parent = parent;
        rightnode.parent = parent;
        // Set the parents of the 4-node's children to the 2-nodes
        if (leftnode.leftChild != null)
            leftnode.leftChild.parent = leftnode;
        if (leftnode.rightChild != null)
            leftnode.rightChild.parent = leftnode;
        if (rightnode.leftChild != null)
            rightnode.leftChild.parent = rightnode;
        if (rightnode.rightChild != null)
            rightnode.rightChild.parent = rightnode;
        // Take leaf values from fournode
        leftnode.isLeaf = fournode.isLeaf;
        rightnode.isLeaf = fournode.isLeaf;
    }

    // 'node' is the node being split, so it is guaranteed to be a 4-node
    // Returns the parent of the node being split
    public TwoFourTreeItem splitNode(TwoFourTreeItem node)
    {
        TwoFourTreeItem leftnode = new TwoFourTreeItem(node.value1);
        TwoFourTreeItem rightnode = new TwoFourTreeItem(node.value3);
        TwoFourTreeItem parent = node.parent;

        if (node.isRoot())
        {
            // Create the new root
            TwoFourTreeItem newRoot = new TwoFourTreeItem(node.value2);
            // Set the children of the new root
            newRoot.leftChild = leftnode;
            newRoot.rightChild = rightnode;
            // Set the parents of the new children
            leftnode.parent = newRoot;
            rightnode.parent = newRoot;
            // Link the old 4-node's children to the 2-nodes
            linkSplitNodeChildren(node, leftnode, rightnode, newRoot);
            // Set values for newRoot leaf
            newRoot.isLeaf = false;
            // Return the new root
            return newRoot;
        }
        else if (parent.isTwoNode())
        {
            // Node is the left child of parent
            if (node.value2 < parent.value1)
            {
                // Change values of parent node
                parent.value2 = parent.value1;
                parent.value1 = node.value2;
                // Set parent's children to the new 2-nodes
                parent.leftChild = leftnode;
                parent.centerChild = rightnode;
            }
            // Node is the right child of parent
            else
            {
                // Change values of parent node
                parent.value2 = node.value2;
                // Set parent's children to the new 2-nodes
                parent.centerChild = leftnode;
                parent.rightChild = rightnode;
            }
            // Set parents of child nodes
            leftnode.parent = parent;
            rightnode.parent = parent;
        }
        else if (parent.isThreeNode())
        {
            // Node is the left child of parent
            if (node.value2 < parent.value1)
            {
                // Place node's middle value in the left of the parent
                parent.value3 = parent.value2;
                parent.value2 = parent.value1;
                parent.value1 = node.value2;
                // Set parent's children to the new 2-nodes
                parent.leftChild = leftnode;
                parent.centerLeftChild = rightnode;
                parent.centerRightChild = parent.centerChild;
            }
            // Node is the middle child of the parent
            else if (node.value2 > parent.value1 && node.value2 < parent.value2)
            {
                // Place node's middle value in the middle of the parent
                parent.value3 = parent.value2;
                parent.value2 = node.value2;
                // Set parent's children to the new 2-nodes
                parent.centerLeftChild = leftnode;
                parent.centerRightChild = rightnode;
            }
            // Node is the right child of the parent
            else
            {
                // Place node's middle value in the right of the parent
                parent.value3 = node.value2;
                // Set parent's children to the new 2-nodes
                parent.centerRightChild = leftnode;
                parent.rightChild = rightnode;
                parent.centerLeftChild = parent.centerChild;
            }
            parent.centerChild = null;
        }
        parent.values++;
        // Link the old 4-node's children to the 2-nodes
        linkSplitNodeChildren(node, leftnode, rightnode, parent);

        return parent;
    }

    // Perform a left rotation to add a value to node
    // Assumes 'node' is a 2-node, 'right' is the right sibling of 'node'
    public TwoFourTreeItem leftRotate(TwoFourTreeItem node, TwoFourTreeItem right)
    {
        int leftFacingVal = right.value1;

        // Remove the leftmost value from the right sibling
        right.value1 = right.value2;
        right.value2 = right.value3;
        right.value3 = 0;

        int value1 = node.parent.value1;
        int value2 = node.parent.value2;
        int value3 = node.parent.value3;

        // Determines the value to be inserted into node
        // When the value is determined, it is inserted into node
        // leftFacingVal is then inserted in the parent
        if (value1 > node.value1 && value1 < leftFacingVal)
        {
            node.value2 = value1;
            node.parent.value1 = leftFacingVal;
        }
        else if (value2 > node.value1 && value2 < leftFacingVal)
        {
            node.value2 = value2;
            node.parent.value2 = leftFacingVal;
        }
        else
        {
            node.value2 = value3;
            node.parent.value3 = leftFacingVal;
        }
        node.values++;

        // Node's children are shifted left
        node.centerChild = node.rightChild;
        // Node is now 1 larger and the right sibling is 1 smaller
        // Node's right child becomes the sibling's left child
        node.rightChild = right.leftChild;
        if (node.rightChild != null)
            node.rightChild.parent = node;
        // Change the child for right
        if (right.isThreeNode())
        {
            right.leftChild = right.centerChild;
            right.centerChild = null;
        }
        else
        {
            right.leftChild = right.centerLeftChild;
            right.centerChild = right.centerRightChild;
            right.centerLeftChild = null;
            right.centerRightChild = null;
        }
        right.values--;
        return node;
    }

    // Perform a right rotation to add a value to node
    // Assumes 'node' is a 2-node, 'left' is the left sibling of 'node'
    public TwoFourTreeItem rightRotate(TwoFourTreeItem node, TwoFourTreeItem left)
    {
        // The left sibling is a 3-4 node
        int rightFacingVal;
        // Finds the rightmost value based on its values count
        if (left.isThreeNode())
        {
            rightFacingVal = left.value2;
            left.value2 = 0;
        }
        else
        {
            rightFacingVal = left.value3;
            left.value3 = 0;
        }

        int value1 = node.parent.value1;
        int value2 = node.parent.value2;
        int value3 = node.parent.value3;

        // Determines the value to be inserted into node
        // When the value is determined, it is inserted into node
        // rightFacingVal is then inserted in the parent
        node.value2 = node.value1;
        if (value1 < node.value1 && value1 > rightFacingVal)
        {
            node.value1 = value1;
            node.parent.value1 = rightFacingVal;
        }
        else if (value2 < node.value1 && value2 > rightFacingVal)
        {
            node.value1 = value2;
            node.parent.value2 = rightFacingVal;
        }
        else
        {
            node.value1 = value3;
            node.parent.value3 = rightFacingVal;
        }
        node.values++;

        // Take a child from the left sibling and give it to node
        node.centerChild = node.leftChild;
        // Take the right child from left and assign it to node
        node.leftChild = left.rightChild;
        if (node.leftChild != null)
            node.leftChild.parent = node;

        if (left.isThreeNode())
        {
            left.rightChild = left.centerChild;
            left.centerChild = null;
        }
        else 
        {
            left.centerChild = left.centerLeftChild;
            left.rightChild = left.centerRightChild;
            left.centerLeftChild = null;
            left.centerRightChild = null;
        }
        left.values--;
        return node;
    }

    // Merge 2-nodes 'node' and 'left' into a 4-node, node is not the root
    public TwoFourTreeItem mergeNodes(TwoFourTreeItem left, TwoFourTreeItem right)
    {
        TwoFourTreeItem mergedNode = null;

        int value1 = left.parent.value1;
        int value2 = left.parent.value2;
        int value3 = left.parent.value3;

        // Determine which value to use from the parent and
        // create a new node using that value
        if (value1 > left.value1 && value1 < right.value1)
            mergedNode = new TwoFourTreeItem(left.value1, value1, right.value1);
        else if (value2 > left.value1 && value2 < right.value1)
            mergedNode = new TwoFourTreeItem(left.value1, value2, right.value1);
        else
            mergedNode = new TwoFourTreeItem(left.value1, value3, right.value1);

        // Links mergedNode's children to the children of the left and right 2-nodes
        linkMergeNodeChildren(mergedNode, left, right);
        // Removes the middle value of mergedNode from the parent, fixes links
        dropMidValue(mergedNode, left, right);
        
        mergedNode.isLeaf = mergedNode.isLeaf();
        return mergedNode;
    }

    // Links the children of the left and right nodes, as well as their merged node
    public void linkMergeNodeChildren(TwoFourTreeItem mergedNode, TwoFourTreeItem left, TwoFourTreeItem right)
    {
        // Merged node takes on the children of the left and right 2-nodes
        mergedNode.leftChild = left.leftChild;
        mergedNode.centerLeftChild = left.rightChild;
        mergedNode.centerRightChild = right.leftChild;
        mergedNode.rightChild = right.rightChild;

        // Changes the parents of left and right's children to mergedNode
        if (mergedNode.leftChild != null)
            mergedNode.leftChild.parent = mergedNode;
        if (mergedNode.centerLeftChild != null)
            mergedNode.centerLeftChild.parent = mergedNode;
        if (mergedNode.centerRightChild != null)
            mergedNode.centerRightChild.parent = mergedNode;
        if (mergedNode.rightChild != null)
            mergedNode.rightChild.parent = mergedNode;
    }

    // Removes the mergedNode's middle value from its parent
    public void dropMidValue(TwoFourTreeItem mergedNode, TwoFourTreeItem left, TwoFourTreeItem right)
    {
        // Set the parent of mergedNode
        TwoFourTreeItem parent = left.parent;
        mergedNode.parent = parent;

        int value1 = parent.value1;
        int value2 = parent.value2;

        // 3-node case, check value1 and value2
        if (parent.isThreeNode())
        {
            if (left.value1 < value1 && right.value1 > value1)
            {
                // Replace value1 in parent
                parent.value1 = parent.value2;
                // Set parent's child to mergedNode
                parent.leftChild = mergedNode;
            }
            else
            {
                parent.rightChild = mergedNode;
            }
            parent.centerChild = null;
            parent.value2 = 0;
        }
        // 4-node case, check all 3 values
        else 
        {
            if (left.value1 < value1 && right.value1 > value1)
            {
                // Move value2 and value3 over
                parent.value1 = parent.value2;
                parent.value2 = parent.value3;
                // Set parent's children
                parent.leftChild = mergedNode;
                parent.centerChild = parent.centerRightChild;
            }
            else if (left.value1 < value2 && right.value1 > value2)
            {
                // Move value3 into value2
                parent.value2 = parent.value3;
                // Set parent's children
                parent.centerChild = mergedNode;
            }
            else
            {
                parent.centerChild = parent.centerLeftChild;
                parent.rightChild = mergedNode;
            }
            // Get rid of unwanted values, reduce to 3-node
            parent.centerLeftChild = null;
            parent.centerRightChild = null;
            parent.value3 = 0;
        }
        parent.values--;
    }

    // Merge the root if the root and its children are 2-nodes
    public TwoFourTreeItem mergeRoot()
    {
        TwoFourTreeItem left = root.leftChild;
        TwoFourTreeItem right = root.rightChild;

        // Create a new root with the values in order
        TwoFourTreeItem newRoot = new TwoFourTreeItem(left.value1, root.value1, right.value1);

        // Link the old left and right nodes' children to the new root
        linkMergeNodeChildren(newRoot, left, right);
        // Determine if it's a leaf
        newRoot.isLeaf = newRoot.isLeaf();
        newRoot.parent = null;

        return newRoot;
    }

    // Remove a value from a specific node
    // Assumes that the value is in the node with no need to traverse further
    public TwoFourTreeItem removeFromNode(TwoFourTreeItem node, int value)
    {
        if (value != node.value1 && value != node.value2 && value != node.value3)
            return node;
        
        if (value == node.value1)
        {
            node.value1 = node.value2;
            node.value2 = node.value3;
        }
        else if (value == node.value2)
        {
            node.value2 = node.value3;
        }
        node.value3 = 0;
        node.values--;
        return node;
    }

    public void printInOrder() {
        if(root != null) root.printInOrder(0);
    }

    public TwoFourTree() {
        root = null;
    }
}
