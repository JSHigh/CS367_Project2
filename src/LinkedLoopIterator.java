import java.util.Iterator;
import java.util.NoSuchElementException;

///////////////////////////////////////////////////////////////////////////////
//Main Class File:  ImageLoopEditor
//File:             LinkedLoopIterator
//Semester:         CS 367, Fall 2016
//
//Author1:          Justin High (jshigh@wisc.edu)
//CS Login:         high
//Author2:			Aaron Gordner (agordner@wisc.edu)
//CS Login:			gordner
//Lecturer's Name:  Charles Fischer
//Lab Section:      004
//
///////////////////////////////////////////////////////////////////////////////

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
	/** count of steps items we've looked at in the loop */
	private int itemCount;
	/** reference to the loop we're iterating over */
	LinkedLoop<E> loop = new LinkedLoop<E>();
	
	/**
	 * Constructor: Creates a new iterator over a doubly linked loop
	 * @param lLoop the doubly linked loop to iterate over
	 */
	public LinkedLoopIterator(LinkedLoop<E> lLoop) {
		itemCount = 0;
		this.loop = lLoop;
	}
	
	/**
	 * Returns whether or not there is another node in the chain.
	 * @return true if there is another node; else false.
	 */
	@Override
	public boolean hasNext() {
		return itemCount < loop.size();
	}

	/**
	 * Returns the data value for the next node in the chain.
	 * @return the data held in the next node.
	 */
	@Override
	public E next() throws EmptyLoopException {
		if (!this.hasNext())
		{
			throw new NoSuchElementException();
		}
		itemCount++;
		E dataReturn = loop.getCurrent();
		loop.next();
		return dataReturn;
	}
	
	public void remove(E item){
		throw new UnsupportedOperationException();
	}

}
