package empire.wars;

public class Node implements Comparable 
{
	public int x;		
	public int y;		
	public float cost;		
	public Node parent;		
	public float heuristic;		
	public int depth;		
	public boolean open;		
	public boolean closed;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int setParent(Node parent) {
		depth = parent.depth + 1;
		this.parent = parent;
		
		return depth;
	}
	
	public int compareTo(Object other) {
		Node o = (Node) other;
		
		float f = heuristic + cost;
		float of = o.heuristic + o.cost;
		
		if (f < of) {
			return -1;
		} else if (f > of) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public boolean isClosed() {
		return closed;
	}

	public void reset() {
		closed = false;
		open = false;
		cost = 0;
		depth = 0;
	}

	public String toString() {
		return "[Node "+x+","+y+"]";
	}
}