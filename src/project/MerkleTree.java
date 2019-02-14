package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

import util.HashGeneration;

public class MerkleTree {
	protected Node root=null;
	protected ArrayList<Node> nodesOfTree=new ArrayList<>();

	public MerkleTree(String path) throws NoSuchAlgorithmException, IOException{
		Scanner input = new Scanner(new File(path));
		Queue<Node> hashes = new LinkedList<>();
		int index=0; 
		while(input.hasNext()) {
			String line = input.next();
			File file = new File(line);
			String hash= HashGeneration.generateSHA256(file);
			Node nodeHash=new Node(hash, index);
			index++;      
			hashes.add(nodeHash);
			this.nodesOfTree.add(nodeHash);  
		}
		input.close();
		Queue<Node> hashes1= constructTree(hashes, index);
		this.root = hashes1.poll();
		

	}

	public Queue<Node> constructTree(Queue<Node> hashes, int index) throws NoSuchAlgorithmException, UnsupportedEncodingException{   //add
		int size = hashes.size();
		if(size<=1) return hashes;
		Queue<Node> hashes1=new LinkedList<>();
		for(int i=0; i<size/2; i++) {
			Node node1= hashes.poll();
			String line1= node1.getData();
			Node node2=hashes.poll();
			String line2=node2.getData();
			String hash3=HashGeneration.generateSHA256(line1.concat(line2));
			Node node3=new Node(hash3, index); 
			index++;   
			node3.left=node1;
			node3.right=node2;
			node1.parent=node3;  
			node2.parent=node3; 
			hashes1.add(node3);
			this.nodesOfTree.add(node3);  
		}		
		if(size%2==1) {
			Node node5=hashes.poll();
			String line3=node5.getData();
			String line4=HashGeneration.generateSHA256(line3.concat(""));
			Node node4= new Node(line4, index); 
			index++; 
 			node4.left=node5;
 			node5.parent=node4; 
			hashes1.add(node4);
			this.nodesOfTree.add(node4);  
		}
		return constructTree(hashes1, index);
	}

	public boolean checkAuthenticity(String path) throws FileNotFoundException {
		Scanner input = new Scanner(new File(path));
		String line =input.nextLine();
		input.close();
		return this.getRoot().getData().equals(line);
	}
	public ArrayList<Stack<String>> findCorruptChunks(String path) throws FileNotFoundException{
		ArrayList<String> realHashes = new ArrayList<>();
		Scanner input = new Scanner(new File(path));
		ArrayList<Stack<String>> corruption = new ArrayList<>();
		while(input.hasNext()) {
			realHashes.add(input.next());
		}
		input.close();
		int lastIndex=this.root.getLeafCount(this.root)-1;
		for(int i=0; i<=lastIndex; i++) {
			if(!this.nodesOfTree.get(i).getData().equals(realHashes.get(realHashes.size()+i-lastIndex-1))){
				Node leaf= this.nodesOfTree.get(i);
				Stack<String> pathway= new Stack<>();
				while(leaf!=null) {
					pathway.add(leaf.getData());
					leaf=leaf.parent;
				}
				Stack<String> result= new Stack<>();
				while(!pathway.isEmpty()) {
					result.push(pathway.pop());
				}
				corruption.add(result);
			}
		}
		return corruption;
	}
	

	public Node getRoot() {
		return this.root;	
	}
	
	public ArrayList<Node> getNodesOfTree(){  
		return this.nodesOfTree;
	}
}
