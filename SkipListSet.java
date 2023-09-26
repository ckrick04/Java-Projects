import java.util.*;
import java.lang.Math;
import java.lang.UnsupportedOperationException;

public class SkipListSet<T extends Comparable<T>> implements SortedSet<T>
{
    private final int MIN_LEVELS = 4;
    private int size;
    private int numLevels;
    // Topmost skiplist level (entry)
    private SkipListSetItem<T> top;
    // Bottommost skiplist level (contains all values)
    private SkipListSetItem<T> bottom;
    private T last;
	
    public SkipListSet()
    {
        this(null);
    }

    public SkipListSet(Collection<? extends T> c)
    {
        size = 0;
        numLevels = MIN_LEVELS;
        last = null;
        top = initializeSkipList(numLevels);
		bottom = traverseToBottom(top);
        this.addAll(c);
    }

	// Create a new SkipList with levelCount levels
    private SkipListSetItem<T> initializeSkipList(int levelCount)
    {
        // Use 1 level for improper values
        if (levelCount < 1)
            levelCount = 1;

        SkipListSetItem<T> temp = new SkipListSetItem<T>();
		SkipListSetItem<T> topLevel = temp;
        for (int i = 1; i < levelCount; i++)
		{
			temp.down = new SkipListSetItem<T>();
			temp = temp.down;
		}
		
		return topLevel;
    }
	
    // Returns the bottommost head pointer of the list
	private SkipListSetItem<T> traverseToBottom(SkipListSetItem<T> head)
	{
		SkipListSetItem<T> temp = head;
		while (temp.down != null)
			temp = temp.down;
		return temp;
	}
	
    // Methods for displaying small skip lists
	public void printList()
	{
		SkipListSetItem<T> head = top;
		while (head != null)
		{
			printLevel(head);
			head = head.down;
		}
	}
	
    public void printLevel(SkipListSetItem<T> head)
    {
        SkipListSetItem<T> level = head.right;
		SkipListSetItem<T> bot = bottom.right;
        System.out.print("[");
        for (int i = 0; i < size; i++)
		{
			if (level == null || !level.item.equals(bot.item))
                System.out.print(" , ");
            else
            {
                System.out.print(level.item + ", ");
                level = level.right;
            }
            bot = bot.right;
		}
        System.out.println("]");
    }

    public boolean add(T e) {
        // Check if a new level is necessary
        if (size >= (1 << numLevels) && !this.contains(e))
            addLevel();

        // Holds the last item in each level before moving down
        // This becomes necessary when adding up the levels
        ArrayList<SkipListSetItem<T>> prev = new ArrayList<>(numLevels);

        // Traverses to the item previous to the item to be added in the bottom level
        SkipListSetItem<T> levelEnd = top;
        for (int i = 0; i < numLevels; i++)
        {
            while (levelEnd.right != null && e.compareTo(levelEnd.right.item) >= 0)
                levelEnd = levelEnd.right;

            // Return false if e is an item in the set
            if (e.equals(levelEnd.item))
                return false;

            // Add reference to the previous item
            prev.add(levelEnd);
            if (i != numLevels - 1)
                levelEnd = levelEnd.down;
        }

        // Add item into the lowest level
        SkipListSetItem<T> itemToAdd = new SkipListSetItem<T>(e);
        itemToAdd.right = levelEnd.right;
        levelEnd.right = itemToAdd;
        itemToAdd.left = levelEnd;

        // Changes 'last' if the item to be added is the last item
        if (itemToAdd.right == null)
            last = e;
        else
            itemToAdd.right.left = itemToAdd;

        // Randomize adding upwards, 50% chance to add to next level
        for (int i = numLevels - 2; i >= 0; i--)
        {
            int coinToss = (int)(Math.random() * 2);
            // Once the coin toss hits 0, the loop breaks
            if (coinToss == 0)
                break;
            // Adds item to the given level
            SkipListSetItem<T> duplicate = new SkipListSetItem<T>(e);
            duplicate.right = prev.get(i).right;
            prev.get(i).right = duplicate;
            duplicate.left = prev.get(i);
            duplicate.down = itemToAdd;
            if (duplicate.right != null)
                duplicate.right.left = duplicate;
            itemToAdd = duplicate;
        }

        // Item was successfully added
        size++;
        return true;
    }

    // Return true if the provided object is in the list, false otherwise
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        T obj = (T)o;
        if (this.findHighestInstance(obj) == null)
            return false;
        return true;
    }

    @SuppressWarnings("unchecked")
    // Removes the provided object from the list, if it exists
    public boolean remove(Object o) {
        // Cast object and start at beginning of list
        T obj = (T)o;

        // Traverse to the item by comparing the items in the SkipList
        // Will be null if the item is not found
        SkipListSetItem<T> pointer = findHighestInstance(obj);
        // Item wasn't found
        if (pointer == null)
            return false;

        // Disconnect adjacent nodes from the pointer
        while (pointer != null)
        {
            // Update 'last' variable if the last item is being removed
            if (pointer.down == null && pointer.item.equals(last))
                last = pointer.left.item;

            pointer.left.right = pointer.right;
            if (pointer.right != null)
                pointer.right.left = pointer.left;
            pointer = pointer.down;
        }

        // Remove a level if there are too few elements
        size--;
        if (size < (1 << numLevels) && numLevels > MIN_LEVELS)
            removeLevel();
        
        return true;
    }

    // Re-randomize the height of the SkipList
    public void reBalance()
    {
        // Removes all levels of the SkipList except for the lowest level
        SkipListSetItem<T> temp = initializeSkipList(numLevels - 1);
        SkipListSetItem<T> bot = traverseToBottom(temp);
        bot.down = bottom;
        top = temp;

        // Store previous references for each level (for adding)
        ArrayList<SkipListSetItem<T>> prev = new ArrayList<>(numLevels - 1);
        for (int i = 0; i < numLevels - 1; i++)
        {
            prev.add(temp);
            temp = temp.down;
        }

        temp = bottom.right;

        while (temp != null)
        {
            int height = randomizeHeight();
            int i = prev.size() - 1;

            // Go down the list to add all necessary nodes
            while (height > 1)
            {
                // Create and link a new node
                SkipListSetItem<T> curItem = prev.get(i);
                SkipListSetItem<T> newItem = new SkipListSetItem<>(temp.item);
                curItem.right = newItem;
                newItem.left = curItem;
                // Traverse to the next level
                if (i == (prev.size() - 1))
                    newItem.down = temp;
                else
                    newItem.down = prev.get(i + 1);
                prev.set(i, newItem);
                height--;
                i--;
            }

            temp = temp.right;
        }
    }

    public Comparator<? super T> comparator() {
        return null;
    }

    // Return first item in the SkipListSet
    public T first() {
        if (size == 0)
            return null;
        return bottom.right.item;
    }

    // Returns last, which contains the last item in the list
    public T last() {
        return last;
    }

    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException();
    }

    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException();
    }

    
    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException();
    }

    // Helper method for adding a new level
    private void addLevel()
    {
        SkipListSetItem<T> topLevel = new SkipListSetItem<>();
        topLevel.down = top;
        SkipListSetItem<T> temp = topLevel;
		SkipListSetItem<T> temp2 = top.right;
        // Randomizes additional height for all elements in the previous top level
        while (temp2 != null)
        {
            int coinToss = (int)(Math.random() * 2);
            if (coinToss == 1)
            {
                temp.right = new SkipListSetItem<T>(temp2.item);
                temp.right.left = temp;
				temp = temp.right;
				temp.down = temp2;
            }
            temp2 = temp2.right;
        }
        numLevels++;
		top = topLevel;
    }

    // Adds all items within the provided collection
    // Returns true if add returns true for each item, false otherwise
    public boolean addAll(Collection<? extends T> c) {
        if (c == null)
            return false;
        boolean condition = true;
        for (T e : c)
            condition = add(e) && condition;
        return condition;
    }

    // Clears the list of all of its elements
    public void clear() {
        // Overwrite the list of levels
        // Resets the object's properties to the constructor's default
        size = 0;
        numLevels = MIN_LEVELS;
        last = null;
        top = initializeSkipList(numLevels);
		bottom = traverseToBottom(top);
    }

    public boolean containsAll(Collection<?> c) {
        if (c == null)
            return false;
        boolean condition = true;
        for (Object e : c)
            condition = contains(e) && condition;
        return condition;
    }

    // Checks if two lists are the same size and contain the same elements
    // Returns true if both conditions are met, false otherwise
    public boolean equals(Object o)
    {
        if (!(o instanceof SkipListSet<?>))
            return false;
        SkipListSet<?> compset = (SkipListSet<?>)o;
        if (size == compset.size())
        {
            Object[] arr1 = this.toArray();
            Object[] arr2 = compset.toArray();
            for (int i = 0; i < size; i++)
            {
                if (!arr1[i].equals(arr2[i]))
                    return false;
            }
            return true;
        }
        return false;
    }

    // Returns the highest node where 'e' is found.
    // Returns null if the object is not found.
    private SkipListSetItem<T> findHighestInstance(T e)
    {
        SkipListSetItem<T> pointer = top;
        while (pointer != null)
        {
            if (pointer.item != null && e.equals(pointer.item))
                return pointer;
            else if (pointer.right == null || e.compareTo(pointer.right.item) < 0)
                pointer = pointer.down;
            else
                pointer = pointer.right;
        }
        return null;
    }

    // Returns a hash value for the list based on its elements
    public int hashCode()
    {
        int val = 1728638839;
        for (T e : this)
        {
            val = val ^ e.hashCode();
            val *= 628570351;
        }
        return val;
    }

    // Returns true if there are no items in the list, false otherwise
    public boolean isEmpty() {
        return (size == 0);
    }

    // Returns a new iterator for the list
    public Iterator<T> iterator() {
        return new SkipListSetIterator<T>(bottom);
    }

    // Randomizes height, 50% chance to increase the height by 1
    private int randomizeHeight()
    {
        // Set up initial height and random values
        int height = 0;
        int rand = 1;

        while (rand == 1)
        {
            height++;
            if (height == numLevels)
                return height;
            rand = (int)(Math.random() * 2);
        }

        return height;
    }

    // Removes a level from the list
    private void removeLevel()
    {
        top = top.down;
        numLevels--;
    }

    // Removes all items in the provided collection, if they exist
    public boolean removeAll(Collection<?> c) {
        if (c == null)
            return false;
        boolean condition = true;
        for (Object o : c)
            condition = remove(o) && condition;
        return condition;
    }

    // Removes all items not contained in the provided collection
    public boolean retainAll(Collection<?> c) {
        if (c == null)
            return false;
        boolean condition = true;
        for (T e : this)
        {
            if (!c.contains(e))
                condition = remove(e) && condition;
        }
        return condition;
    }

    // Returns the size of the list
    public int size() {
        return size;
    }

    // Returns an Object array containing each item in the list
    public Object[] toArray() {
        Object[] array = new Object[size];
        SkipListSetItem<T> ptr = bottom;

        int i = 0;
        while (ptr.right != null)
        {
            ptr = ptr.right;
            array[i] = ptr.item;
            i++;
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        List<T> lis = new ArrayList<>(size);
        for (Object o : this)
            lis.add((T)o);
        return lis.toArray(a);
    }
    
    // Item wrapper for the list
    private class SkipListSetItem<T extends Comparable<T>>
    {
        // Contains the item's reference, with pointers right, left, and down
        T item;
        SkipListSetItem<T> down;
        SkipListSetItem<T> right;
        SkipListSetItem<T> left;

        SkipListSetItem()
        {
            this(null);
        }
        
        SkipListSetItem(T item)
        {
            this.item = item;
            down = null;
            right = null;
            left = null;
        }
    }

    private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T>
    {
        SkipListSetItem<T> currentNode;

        // Pass in the bottom level of the list
        SkipListSetIterator(SkipListSetItem<T> bottom)
        {
            currentNode = bottom;
        }

        // Return true if currentNode is not the last item, false otherwise
        public boolean hasNext()
        {
            if (currentNode.right == null)
                return false;
            return true;
        }

        // Goes to the next node and returns its item
        // Returns null if currentNode is the last node
        public T next()
        {
            if (hasNext())
            {
                currentNode = currentNode.right;
                return currentNode.item;
            }
            return null;
        }

        // Remove currentNode, go back to its previous node
        public void remove()
        {
            T rem = currentNode.item;
            if (rem == null)
                return;
            SkipListSet.this.remove(rem);
            currentNode = currentNode.left;
        }
    }
}
