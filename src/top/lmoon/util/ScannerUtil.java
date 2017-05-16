package top.lmoon.util;

import java.util.Scanner;

public class ScannerUtil {
	
	public static void closeScanner(Scanner sc){
		if(sc!=null){
			sc.close();
		}
	}

}
