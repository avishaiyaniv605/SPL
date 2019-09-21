package bgu.spl.mics.accessories;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


/**
 * File Printer class is an accessory which prints a given object into a given file name
 */

public class FilePrinter {

    public static void printToFile (Object object, String fileName)
    {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream oos = null;

        try {
            fileOutputStream = new FileOutputStream(fileName);
            oos = new ObjectOutputStream(fileOutputStream);
            oos.writeObject(object);
            //Done writing file.
        } catch (Exception ex) {
            System.out.println("ERROR : " + fileName + " file cannot be written");
        } finally {     // we close all used objects
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
