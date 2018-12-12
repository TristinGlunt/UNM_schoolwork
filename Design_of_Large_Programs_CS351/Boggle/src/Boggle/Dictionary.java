package Boggle;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Dictionary holds our already read in dictionary from the DictionaryFileReader
 * @author Tristin Glunt | tglunt@unm.edu | CS 351 Project 2 Boggle
 */
public class Dictionary
{
    private static HashSet<String> dictionary = new HashSet<>();

    /**
     * Dictionary:
     * creates our file reader object and sets the dictionary to the file readers
     * output hashset
     */
    public Dictionary()
    {
        DictionaryFileReader dfr = new DictionaryFileReader();
        dictionary = dfr.getDictionary();
    }

    /**
     * getDictionary:
     * @return the dictionary
     */
    public HashSet<String> getDictionary() { return dictionary; }

}
