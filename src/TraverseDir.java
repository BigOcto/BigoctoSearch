
/*
 * TraverseDir.java
 *
 * Xiannong Meng
 * CSCI 335 Fall 2006
 *
 */

import java.io.*;
import java.util.*;

/**
 * Visits all the files following a given starting file.
 * 
 */
public class TraverseDir {
    
    private ArrayList<String> fileToVisit;


    /**
     * Actually visit directories from a given starting file<p>
     *
     * @param thisFile starting file
     *
     * Precondition:  The starting file is given.<p>
     * Postcondition: All files and directories from this file are visited.<p>
     */
    static public int visit(File thisFile)    {

	int count = 0;
	LinkedList<File> fileList = new LinkedList<File>();

	String pathname = thisFile.getAbsolutePath();
    
	fileList.add(thisFile);

	while (fileList.size() > 0) {

	    File file = fileList.removeFirst();

	    pathname = file.getAbsolutePath();
	    System.out.println("path name " + pathname);
	    if (file.isFile()) {
		System.out.println(file.getName());
		count ++;
	    } else { // must be a directory
		System.out.println("---Entering directory: " + file.getName());
		String[] fileNameList = file.list();
		for (int i = 0; i < fileNameList.length; i++) {
		    File nextFile = new File(pathname + "/" + fileNameList[i]);
		    String FileName=nextFile.toString();
		    if(FileName.endsWith(".html")){
		    	fileList.add(nextFile);
		    }
		}
	    } // if-then-else
	} // while

	return count;
    }




    /*
     * A simple test drive program. <p>
     *
     */
    static public void main(String[] argv)
    {

	/*
	 * If a command-line argument is used as starting directory (or file)
	 * make sure it is an absolute path!
	 */

//	if (argv.length != 1) {
//	    System.err.println("usage: java TraverseDir starting-dir");
//	    System.exit(1);
//	}

	/*
	  File f = new File("/home/accounts/COURSES/csci335/2006-fall/faculty/code");
	*/

	File f = new File("D:/2013-spring");

	int count = visit(f);
	System.out.println("A total of " + count + " files are visited.");
    }
}