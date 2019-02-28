package com.mapsoft.aftersale.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class MyFileReader extends InputStreamReader{
	   
    public MyFileReader(String fileName,String charSetName) throws FileNotFoundException, UnsupportedEncodingException {
        super(new FileInputStream(fileName),charSetName);
    }
}
