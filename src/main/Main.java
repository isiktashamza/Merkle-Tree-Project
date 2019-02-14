package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import project.MerkleTree;
import project.Node;
import util.HashGeneration;

public class Main {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException{

		MerkleTree m0 = new MerkleTree("data/9.txt");
		String hash = m0.getRoot().getLeft().getRight().getData();
		System.out.println(m0.getRoot().getLeft().getLeft().getData());
		System.out.println(hash);
		
		boolean valid = m0.checkAuthenticity("data/9meta.txt");
		System.out.println(valid);

		// The following just is an example for you to see the usage. 
		// Although there is none in reality, assume that there are two corrupt chunks in this example.
		ArrayList<Stack<String>> corrupts = m0.findCorruptChunks("data/9meta.txt");
//		System.out.println("Corrupt hash of first corrupt chunk is: " + corrupts.get(0).pop());
//		System.out.println("Corrupt hash of second corrupt chunk is: " + corrupts.get(1).pop());

		download("secondaryPart/data/download_from_trusted.txt");

	}

	public static void download(String path) throws IOException, NoSuchAlgorithmException {
		Scanner input = new Scanner(new File(path));
		while(input.hasNext()) {
			String meta=input.next();
			if(meta.equals("\n")) meta=input.next();
			File metaOfFile=createFile(meta, "secondaryPart/data/");  //4meta.txt

			ArrayList<File> firstFiles = new ArrayList<>();
			ArrayList<File> alterFiles = new ArrayList<>();
			ArrayList<String> metaHashes = new ArrayList<>();
			Scanner metas = new Scanner(new File("secondaryPart/data/"+meta.substring(getLastIndexOf(meta)+1)));
			while(metas.hasNext()) {
				metaHashes.add(metas.next());
			}

			String first = input.next();
			File firstOfFile = createFile(first, "secondaryPart/data/");  //4.txt
			firstFiles=readFromFile(first, "secondaryPart/data/", "first"); //4.txtden okuma ve 00larý olusturma
			Scanner scannerOfFirst = new Scanner(firstOfFile);


			String alter = input.next();
			File secondOfFile =createFile(alter, "secondaryPart/data/"); //4_alter.txt
			alterFiles=readFromFile(alter, "secondaryPart/data/", "alter" ); //4_alter.txtden okuma ve 00 larý olusturma
			Scanner scannerOfSecond = new Scanner(secondOfFile);

			int leaves = firstFiles.size();
			Files.createDirectories(Paths.get("secondaryPart","data","split"));
			for(int i=0; i<leaves; i++) {
				Files.createDirectories(Paths.get("secondaryPart","data","split",""+first.charAt(getLastIndexOf(first)+1)));

				if(HashGeneration.generateSHA256(firstFiles.get(i)).equals(metaHashes.get(metaHashes.size()+i-leaves))) {
					createFile(scannerOfFirst.next(), "secondaryPart/data/split/"+first.charAt(getLastIndexOf(first)+1));
					scannerOfSecond.next();
				}
				else {
					createFile(scannerOfSecond.next(), "secondaryPart/data/split/"+first.charAt(getLastIndexOf(first)+1));
					scannerOfFirst.next();
				}
			}
		}
	}
	public static File createFile(String name, String path) throws IOException {
		String fileName = name.substring(getLastIndexOf(name)+1, name.length());
		URL url = new URL(name);
		InputStream in = url.openStream();
		File f = new File(path+"/"+fileName);
		FileOutputStream fos = new FileOutputStream(f); // 4meta.txt

		int length = -1;
		byte[] buffer = new byte[1024];// buffer for portion of data from connection
		while ((length = in.read(buffer)) > -1) {
			fos.write(buffer, 0, length);
		}
		fos.close();
		in.close();
		return f;
	}

	public static ArrayList<File> readFromFile(String name, String path, String pathway) throws IOException, NoSuchAlgorithmException {
		String fileName = name.substring(getLastIndexOf(name)+1, name.length());
		ArrayList<File> files = new ArrayList<>();
		Scanner file1= new Scanner(new File(path+fileName));
		while(file1.hasNext()) {
			String x = file1.next();
			URL url3 = new URL(x);
			InputStream in3 = url3.openStream();
			Files.createDirectories(Paths.get("secondaryPart","data",pathway, "" + x.charAt(getLastIndexOf(x)-1)));
			String fileName3 = x.substring(getLastIndexOf(x)+1,x.length());
			File fil = new File("secondaryPart/data/"+pathway+"/"+x.charAt(getLastIndexOf(x)-1)+"/"+fileName3);
			FileOutputStream fos3 = new FileOutputStream(fil);
			int length3 = -1;
			byte[] buffer3 = new byte[1024];// buffer for portion of data from connection



			while ((length3 = in3.read(buffer3)) > -1) {
				fos3.write(buffer3, 0, length3);
			}
			files.add(fil);
		}
		return files;
	}
	public static int getLastIndexOf(String path) {
		int index=path.length()-1;
		while(true) {
			if(path.charAt(index)=='/') {
				return index;
			}
			index--;
		}
	}
}
