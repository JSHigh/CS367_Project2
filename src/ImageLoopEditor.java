///////////////////////////////////////////////////////////////////////////////
//
// Title:			ImageLoopEditor
// Files:			ImageLoopEditor, LinkedLoop, DblListnode, 
//					EmptyLoopException, Image, LinkedLoopIterator, LoopADT
// Semester:        CS367 Fall 2016
//
// Author:          Justin High (jshigh@wisc.edu)
// CS Login:        high
// Lecturer's Name: Charles Fischer
// Lab Section:     004
//
///////////////////////////////////////////////////////////////////////////////

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Application for editing and viewing a loop of images.
 *
 * Bugs: none known
 *
 * @author		Justin High Copyright (2016)
 * @version		1.0
 * @see LinkedLoop, DblListnode, Image
 */
public final class ImageLoopEditor {

	/**
	 * Constructor: private for static class.
	 */
	private ImageLoopEditor() {}

	/**
	 * Entry point for image loop editor application. 
	 * @param args command line arguments
	 */
	public static void main(String [ ] args)
	{
		boolean Debug = true;	// TODO: set to false
    	
    	// Check whether one or zero command-line arguments are given.
		// If not, display "invalid command-line arguments" and quit.
    	int arglen = args.length; Boolean argisgood = ((arglen == 1) || (arglen == 0));
    	if (!argisgood) {
    		System.out.println("invalid command-line arguments.");
    		return;
    	}
    	
    	// construct a new list 
    	LinkedLoop<Image> lLoopImage = new LinkedLoop<Image>();
    	
    	// If command line file exists, check whether if it exists and is readable.
    	// If not, display "problem with input file" and quit.
    	if (arglen == 1)
    	{
    		String inputFileName = args[0];
    		File inputFile = new File(inputFileName);
    		Scanner inputFileScanner = null;
    		try {
    			inputFileScanner = new Scanner(inputFile);
    			while (inputFileScanner.hasNextLine())
    			{
    				String line = inputFileScanner.nextLine();
    				try {
    					if (processOneCommand(lLoopImage, line))
    					{
    						return;
    					}
    				}
    				catch (InvalidCommandException e){
    					System.out.println("invalid command");
    				}
    			}
    		}
			catch (Exception e)
			{
				if (Debug)
				{
					e.printStackTrace();
				}
				System.out.println("problem with input file");
				return;
    		}
    	}
    	
    	// no command file. start interactive mode
    	// open console stream
        Scanner stdin = new Scanner(System.in);  // for reading console input
        // display command options
        printOptions();
        // prompt
        boolean done = false;
        while (!done) {
            System.out.print("enter command (? for help)> ");
            String input = stdin.nextLine();
            try {
            	if (processOneCommand(lLoopImage, input))
            	{
            		return;
            	}
            }
            catch (InvalidCommandException e){
            	System.out.println("invalid command");
            }
        }
	}

	/**
	 * Given a command string, perform command on input doubly linked loop.
	 * @param lLoopImage the doubly linked loop on which to perform the command
	 * @param command the command to perform
	 * @throws InvalidCommandException
	 */
    private static Boolean processOneCommand(LinkedLoop<Image> lLoopImage, String command) throws InvalidCommandException {
    	// only do something if the user enters at least one character
        if (command.length() > 0) {
            char choice = command.charAt(0);
            String sChoice = Character.toString(choice).toLowerCase(); // strip off option character
            String remainder = "";  // used to hold the remainder of input
            if (command.length() > 1) {
                // trim off any leading or trailing spaces
                remainder = command.substring(1).trim();//.toLowerCase(); 
            }
            switch (choice) {    
            	// commands with a filename argument
                case 's':
                	CheckRemainder(remainder);
                	if (!loopIsEmpty(lLoopImage))
                	{
            			File saveToFile = new File(remainder);
            			if (saveToFile.exists())
            			{
            				System.out.println("warning: file already exists, will be overwritten");
            			}
            			try {
							FileWriter saveOS = new FileWriter(saveToFile);
							LinkedLoopIterator<Image> lLoopIter = lLoopImage.iterator();
							while (lLoopIter.hasNext())
							{
								saveOS.write(GetOneContext(lLoopIter.next()));
							}
							saveOS.close();
							// DisplayCurrentContext(lLoopImage);
						} catch (Exception e) {
							System.out.println("unable to save");
							e.printStackTrace();
						}
                	}
                	break;
                	
                case 'l':
                	CheckRemainder(remainder);
                    break;
	
                case 'a':
                	CheckRemainder(remainder);
                	// TODO: need to do better validation on image filenames
                	Image imageToAdd = new Image(remainder);
                	lLoopImage.add(imageToAdd);
                	break;

                case 'i':
                	CheckRemainder(remainder);
                    break;
                
                // search commands
                case 'c':
                    break;
                    
                // movement commands
                
                // help command
                case '?':
                	printOptions();
                	break;

                // exit command
                case 'x':
                	System.out.println("exit");
                    return true;

                default:  // ignore any unknown commands
                	throw new InvalidCommandException();
                }
            } else {throw new InvalidCommandException();}
        return false;
        }
	
    /**
     * Print options for commands.
     */
	private final static void printOptions(){
		System.out.println("s (save)      l (load)       d (display)        p (picture)");
		System.out.println("f (forward)   b (backward)   j (jump)           t (test)");
		System.out.println("r (remove)    a (add after)  i (insert before)  e (retitle)");
		System.out.println("c (contains)  u (update)     x (exit)");
	}
	
	/**
	 * Detect if linked loop is empty and alert
	 */
	private final static Boolean loopIsEmpty(LinkedLoop<Image> loop) {
		if (loop.isEmpty())
		{
			System.out.println("no images to save");
			return true;
		}
		return false;
	}
	
	/**
	 * Detect if string with remainder is null
	 */
	private final static void CheckRemainder(String rem) throws InvalidCommandException {
		if (rem == ""){
			throw new InvalidCommandException();
		}
	}
	
	private final static String[] GetCurrentContext(LinkedLoop<Image> lLoopImage) throws EmptyLoopException {
		String[] sContext = {"", "", ""};
		lLoopImage.previous();
		String prevCont = GetOneContext(lLoopImage.getCurrent());
		lLoopImage.next();
		String currCont = GetOneContext(lLoopImage.getCurrent());
		lLoopImage.next();
		String nextCont = GetOneContext(lLoopImage.getCurrent());
		lLoopImage.previous();
		if (prevCont == currCont && currCont != "")
		{
			prevCont = "";
		}
		if (nextCont == currCont && currCont != "")
		{
			nextCont = "";
		}
		sContext[0] = prevCont;
		sContext[1] = "--> " + currCont + " <--";
		sContext[2] = nextCont;
		return sContext;
	}
	private final static void DisplayCurrentContext(LinkedLoop<Image> lLoopImage) throws EmptyLoopException {
		String[] sContext = GetCurrentContext(lLoopImage);
		for (String s : sContext)
		{
			if (s != "")
			{
				System.out.println(s);
			}
		}
	}
	
	private final static String GetOneContext(Image image) {
		String ret = image.getFile() + " " + image.getDuration();
		String fTitle = image.getTitle();
		if (fTitle != "")
		{
			ret += " " + fTitle;
		}
		return ret;
	}
}

