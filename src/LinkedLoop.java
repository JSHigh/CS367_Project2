///////////////////////////////////////////////////////////////////////////////
// 
// Main Class File:  ImageLoopEditor
// File:             LinkedLoop
// Semester:         CS367 Fall 2016
//
// Author:           Justin High (jshigh@wisc.edu)
// CS Login:         high
// Lecturer's Name:  Charles Fischer
// Lab Section:      004
//
///////////////////////////////////////////////////////////////////////////////

import java.util.Iterator;

/**
 * A doubly-linked, circular loop of generic elements.
 *
 * <p>Bugs: None known
 *
 * @author	Justin High Copyright (2016)
 * @version	1.0
 */
public class LinkedLoop<E> implements LoopADT<E> {
	
	/** This loop's currently focused node. */
	private DblListnode<E> currentItemNode = null;
	/** A count of nodes in the loop. */
	private int itemCount;
	
	/**
	 * Constructor: Default. Takes no arguments and builds empty list.
	 */
	public LinkedLoop() {
		itemCount = 0;
	}
    /**
     * Adds the given item immediately <em>before</em> the current 
     * item.  After the new item has been added, the new item becomes the 
     * current item.
     * 
     * @param item the item to add
     */
	@Override
	public void add(E item) {
		
		DblListnode<E> itemNode = new DblListnode<E>(item);
		
		if (currentItemNode != null) // have at least one node
		{
			// if current is B and previous is A
			// get A
			DblListnode<E> currPrev = currentItemNode.getPrev();
			
			// update A's pointer to the new node
			currPrev.setNext(itemNode);
			
			// update new node's previous to A
			itemNode.setPrev(currPrev);
			// and next to B
			itemNode.setNext(currentItemNode);
			
			// and update B's previous to new node
			currentItemNode.setPrev(itemNode);
			
			// set newly inserted node as current
			currentItemNode = itemNode;
		}
		else // have empty chain
		{
			// setting pointer to self avoid null reference on next add
			currentItemNode = itemNode;
			currentItemNode.setPrev(currentItemNode);
			currentItemNode.setNext(currentItemNode);
		}
		itemCount++;
	}

    /**
     * Returns the current item.  If the Loop is empty, an 
     * <tt>EmptyLoopException</tt> is thrown.
     * 
     * @return the current item
     * @throws EmptyLoopException if the Loop is empty
     */
	@Override
	public E getCurrent() throws EmptyLoopException {
		if (currentItemNode == null)
		{
			throw new EmptyLoopException();
		}
		else
		{
			return currentItemNode.getData();
		}
	}

    /**
     * Removes and returns the current item.  The item immediately 
     * <em>after</em> the removed item then becomes the  current item.  
     * If the Loop is empty initially, an <tt>EmptyLoopException</tt> 
     * is thrown.
     * 
     * @return the removed item
     * @throws EmptyLoopException if the Loop is empty
     */
	@Override
	public E removeCurrent() throws EmptyLoopException {
		// case where chain is empty
		if (itemCount == 0)
		{
			throw new EmptyLoopException();
		}
		
		Boolean currHasPrev = (currentItemNode.getPrev() != currentItemNode);
		Boolean currHasNext = (currentItemNode.getNext() != currentItemNode);
		E itemDataVal = currentItemNode.getData();
		
		if (!currHasPrev && !currHasNext) // case where we're removing the last node
		{
			currentItemNode = null;
			itemCount = 0;
			return itemDataVal;
		}
		
		else // catchall case
		{
			// point previous node to current's next
			currentItemNode.getPrev().setNext(currentItemNode.getNext());
			// point next node to current's previous
			currentItemNode.getNext().setPrev(currentItemNode.getPrev());
			currentItemNode = currentItemNode.getNext();
		}
		itemCount--;
		return itemDataVal;
	}

    /**
     * Advances current forward one item resulting in the item that is 
     * immediately <em>after</em> the current item becoming the current item.
     */
	@Override
	public void next() {
		currentItemNode = currentItemNode.getNext();		
	}

    /**
     * Moves current backwards one item resulting in the item that is 
     * immediately <em>before</em> the current item becoming the current item.
     */
	@Override
	public void previous() {
		currentItemNode = currentItemNode.getPrev();
	}

    /**
     * Determines if this Loop is empty, i.e., contains no items.
     * @return true if the Loop is empty; false otherwise
     */
	@Override
	public boolean isEmpty() {
		return (itemCount == 0);
	}

    /**
     * Returns the number of items in this Loop.
     * @return the number of items in this Loop
     */
	@Override
	public int size() {
		return itemCount;
	}

    /**
     * Returns an iterator for this Loop.
     * @return an iterator for this Loop
     */
	@Override
	public LinkedLoopIterator<E> iterator() {
		return new LinkedLoopIterator<E>(this);
	}
}
