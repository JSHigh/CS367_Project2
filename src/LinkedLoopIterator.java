import java.util.Iterator;

/**
 * Represents an iterator on a doubly linked loop.
 *
 * Bugs: none known
 *
 * @author      Justin High Copyright (2016)
 * @version     1.0
 * @see 		LinkedLoop
 */
public class LinkedLoopIterator<E> implements Iterator<E> {
	/** the node in the linked loop that is the starting point. */
	DblListnode<E> startNode;
	/** the node in the linked loop that we're current on. */
	DblListnode<E> currNode;
	/** boolean to see if we've done at least one full loop. */
	Boolean didALoop;
	
	/**
	 * Constructor: Creates a new iterator over a doubly linked loop
	 * @param node a node in the doubly linked loop to start from
	 */
	public LinkedLoopIterator(DblListnode<E> node) {
		startNode = currNode = node;
		didALoop = false;
	}
	
	/**
	 * Returns whether or not there is another node in the chain.
	 * @return true if there is another node; else false.
	 */
	@Override
	public boolean hasNext() {
		if (currNode.getNext() == null || currNode.getNext() == startNode)
		{
			return false;
		}
		return true;
	}

	/**
	 * Returns the data value for the next node in the chain.
	 * @return the data held in the next node.
	 */
	@Override
	public E next() {
		E currVal = currNode.getData();
		currNode = currNode.getNext();
		if (currNode == startNode)
		{
			didALoop = true;
		}
		return currVal;
	}
	
	/**
	 * Returns whether or not the loop has been traversed at least once.
	 * @return boolean whether or not the loop has been traversed at least once.
	 */
	public Boolean fullLoop(){
		return didALoop;
	}
	
	public void remove(E item){
		throw new UnsupportedOperationException();
	}

}
