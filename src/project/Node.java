package project;


public class Node {
	protected Node left=null;
	protected Node right=null;
	protected Node parent=null; //add
	protected String hash;
	protected int index;  //add
	
	public Node(String hash, int index) {
		this.hash=hash;
		this.index=index;
	}
	
	public Node getLeft() {
		return this.left;
	}
	
	public Node getRight() {
		return this.right;
	}
	
	public String getData() {
		return this.hash;
	}
	
	public int getIndex() {
		return this.index;
	}
	public int getHeight(Node current) {
		if(current==null)
			return -1;
		return 1 + Math.max(getHeight(current.left), getHeight(current.right));
	}
	public int getLeafCount(Node root) {
		if(root==null) return 0;
		else if(root.left==null&&root.right==null) return 1;
		else {
			return getLeafCount(root.left)+getLeafCount(root.right);
		}
	}
}
