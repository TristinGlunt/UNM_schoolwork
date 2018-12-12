package Boggle;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Per Dr.Roman's instructions this class is used as subordinate to Dictionary to
 * do the actual reading of the dictionary file, and then pass the dictionary
 * data structure to the dictionary object
 * @author Tristin Glunt | tglunt@unm.edu | CS 351 Project 2 Boggle
 */
public class DictionaryFileReader
{
    private static final HashSet<String> dictionary = new HashSet<>();

    public DictionaryFileReader()
    {
        readInFile();
    }

    /**
     * readinFile: read in file passed through our scanner @ dictionary.txt,
     * don't store words that are less than 3 letters
     */
    private void readInFile()
    {
        try
        {
            Scanner file = new Scanner(new File("resources/dictionary.txt"));
            while(file.hasNext())
            {
                String current = file.next().trim();
                if(current.length() < 3) continue; //no words in this dictionary are longer than 16 letters so need to checl
                dictionary.add(current);
            }
        }
        catch (IOException e)
        {
            System.err.println("Invalid filename");
        }
    }

    public HashSet<String> getDictionary() { return dictionary; }


}
