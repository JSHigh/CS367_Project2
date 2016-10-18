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
    		
    		inputFileScanner.close();
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
            catch (EmptyLoopException e) {
				System.out.println("Trying to loop on empty loop");
			}
        }
        
        stdin.close();
	}

	/**
	 * Given a command string, perform command on input doubly linked loop.
	 * @param lLoopImage the doubly linked loop on which to perform the command
	 * @param command the command to perform
	 * @throws InvalidCommandException
	 * @throws EmptyLoopException 
	 */
    private static Boolean processOneCommand(LinkedLoop<Image> lLoopImage, String command) throws InvalidCommandException, EmptyLoopException {
    	// only do something if the user enters at least one character
        if (command.length() > 0) {
            char choice = command.charAt(0);
            //String sChoice = Character.toString(choice).toLowerCase(); // strip off option character
            String remainder = "";  // used to hold the remainder of input
            Image imageToAdd;
            if (command.length() > 1) {
                // trim off any leading or trailing spaces
                remainder = command.substring(1).trim(); 
            }
            switch (choice) {    
            	// commands with a filename argument (Save, Load, Add After, Insert Before)
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
								saveOS.write(lLoopIter.next().toString());
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
                	File loadFromFile = new File(remainder);
                	//check for existence and readability
                	if ((loadFromFile.exists() == false) || (loadFromFile.canWrite()))
                	{
                		System.out.println("unable to load");
                	}
                	else
                	{
                		try {
                			Scanner loadFromFileScanner = new Scanner(loadFromFile);
                			while (loadFromFileScanner.hasNextLine())
                			{
                				String line = loadFromFileScanner.nextLine();
                				//need new Image object created from the input
                				//lLoopImage is the doubly linked loop to use
                				//parse out filename, duration, and titleloadFromFileScanner
                				//if "filename" is not in the "images" folder, show warning
                				String delims = "[]+";
                				String[] tokens = line.split(delims);
                				String filename = tokens[0];
                				int duration = Integer.parseInt(tokens[1]);
                				String title = tokens[2];
                				
                				//TODO now check if image exists in /images/ folder
                				String directory = "/images/";
                				boolean check = new File(directory, filename).exists();
                				
                				if (check) {
                					System.out.println("Warning: " + filename + " is not in images folder");
                				}
                				
                				Image lineImage = new Image(filename, title, duration);
                				lLoopImage.add(lineImage);
                				
                				//since we add before the current item, the next should always be the first we added
                				lLoopImage.next();
                			}
                			loadFromFileScanner.close();
                		}
            			catch (Exception e)
            			{
            				System.out.println("problem with input file");
                		}
                	}
                	
                    break;
	
                case 'a':
                	CheckRemainder(remainder);
                	// TODO: bugs: does not set current image to the new image, is one ahead
                	// TODO: need to do better validation on image filenames
                	//add AFTER the current image, so move forward one position first
                	imageToAdd = new Image(remainder);
                	if (lLoopImage.isEmpty()) {
                		lLoopImage.add(imageToAdd);
                	}
                	else {
                		lLoopImage.next();
                		lLoopImage.add(imageToAdd);
                	}
                	DisplayCurrentContext(lLoopImage);
                	break;

                case 'i':
                	CheckRemainder(remainder);
                	imageToAdd = new Image(remainder);
                	lLoopImage.add(imageToAdd);
                	DisplayCurrentContext(lLoopImage);
                    break;
                
                // search command
                case 'c':
                	if (lLoopImage.isEmpty()){
                		System.out.println("no images");
                	}
                	else {
                    	//TODO check for quotes on the string?
                		//TODO make sure remainder is exactly as the user entered
                    	CheckRemainder(remainder);
                    	String search = remainder;
                    	LinkedLoopIterator<Image> searchIter = lLoopImage.iterator();
                    	boolean found = false;
                    	while (searchIter.hasNext()) {
                    		Image searchImg = lLoopImage.getCurrent();
                    		String imgTitle = searchImg.getTitle();
                    		if (imgTitle == search){
                    			found = true;
                    			break;
                    		}
                    		searchIter.next();
                    	}
                    	
                    	if (found == true) {
                    		DisplayCurrentContext(lLoopImage);
                    	}
                    	else {
                    		System.out.println("not found");
                    	}
                	}
                    break;
                    
                // display commands (Display, Picture, Test) 
                case 'd':
                	if (lLoopImage.isEmpty()){
                		System.out.println("no images");
                	}
                	else {
                		//get an iterator, loop through, use toString
                		LinkedLoopIterator<Image> dispIter = lLoopImage.iterator();
                		while (dispIter.hasNext()) {
                			Image display = lLoopImage.getCurrent();
                			System.out.println(display.toString());
                			dispIter.next();
                		}
                	}
                	break;
                	
                case 'p':
                	//TODO: bugs: images are really tiny, may not actually be getting found
                	if (lLoopImage.isEmpty()){
                		System.out.println("no images");
                	}
                	else {
                		Image photo = lLoopImage.getCurrent();
                		try {
							photo.displayImage();
						} catch (InterruptedException e) {
							System.out.println("Image lost while displaying");
						}
                	}
                	break;
                	
                case 't':
                	if (lLoopImage.isEmpty()){
                		System.out.println("no images");
                	}
                	else {
                		LinkedLoopIterator<Image> testIter = lLoopImage.iterator();
                		while (testIter.hasNext()) {
                			Image displayTest = lLoopImage.getCurrent();
                			try {
                				displayTest.displayImage();
                			} catch (InterruptedException e) {
                				System.out.println("Image lost while displaying");
                			}
                			testIter.next();
                		}
                	}
                	break;
                	
                //movement commands (Forward, Backward, Jump)
                case 'f':
                	if (lLoopImage.isEmpty()){
                		System.out.println("no images");
                	}
                	else {
                		lLoopImage.next();
                		DisplayCurrentContext(lLoopImage);
                	}
                	break;
                	
                case 'b':
                	if (lLoopImage.isEmpty()){
                		System.out.println("no images");
                	}
                	else {
                		lLoopImage.previous();
                		DisplayCurrentContext(lLoopImage);
                	}
                	break;
                
                case 'j':
                	if (lLoopImage.isEmpty()){
                		System.out.println("no images");
                	}
                	else {
                		CheckRemainder(remainder);
                		int spaces = Integer.parseInt(remainder);
                		int i;
                		
                		//number is negative, loop backwards
                		if (spaces < 0) {
                			for (i = 0; i > spaces; i--) {
                				lLoopImage.previous();
                			}
                		}
                		
                		//number is positive, loop forwards
                		if (spaces > 0) {
                			for (i = 0; i < spaces; i++) {
                				lLoopImage.next();
                			}
                		}
                		
                		//always display context after looping
                		DisplayCurrentContext(lLoopImage);
                	}
                	break;
                	
                //Edit commands (Remove, Update, Retitle)
                case 'r':
                	if (lLoopImage.isEmpty()){
                		System.out.println("no images");
                	}
                	else {
                		lLoopImage.removeCurrent();
                		DisplayCurrentContext(lLoopImage);
                	}
                	break;
                	
                case 'u':
                	if (lLoopImage.isEmpty()){
                		System.out.println("no images");
                	}
                	else {
                		CheckRemainder(remainder);
                		int duration = Integer.parseInt(remainder);
                		Image durationImg = lLoopImage.getCurrent();
                		durationImg.setDuration(duration);
                		DisplayCurrentContext(lLoopImage);
                	}
                	break;
                	
                case 'e':
                	if (lLoopImage.isEmpty()){
                		System.out.println("no images");
                	}
                	else {
                		CheckRemainder(remainder);
                		String title = remainder;
                		Image titleImg = lLoopImage.getCurrent();
                		titleImg.setTitle(title);
                		DisplayCurrentContext(lLoopImage);
                	}
                	break;
                
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
	
    private static boolean FileIsInImagesFolder(String fileName) {
		// String fileName = "images/"+getFile();
    	File fTemp = new File(fileName);
    	if (fTemp.getParent() == "test")
    	{
    		;
    	}
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
		String prevCont = lLoopImage.getCurrent().toString();
		lLoopImage.next();
		String currCont = lLoopImage.getCurrent().toString();
		lLoopImage.next();
		String nextCont = lLoopImage.getCurrent().toString();
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
	
//	private final static String GetOneContext(Image image) {
//		String ret = image.getFile() + " " + image.getDuration();
//		String fTitle = image.getTitle();
//		if (fTitle != "")
//		{
//			ret += " " + fTitle;
//		}
//		return ret;
//	}
}

