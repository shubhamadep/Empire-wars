package empire.wars;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.newdawn.slick.tiled.TiledMap;

import empire.wars.EmpireWars.TEAM;
import jig.Vector;

public class PathFinder 
{
	private ArrayList closed = new ArrayList();
	private PriorityList open = new PriorityList();
	private Node[][] nodes;
	private Node current;
	private int distance;
	private boolean[][] visited;
	public Stack pathStack;
	public Vector tilePosition;
	
	int wallLayer;
	int roadLayer;
	TiledMap map;
	final float CREEP_SPEED = 0.1f;
	
	public PathFinder(final float x, final float y, final TEAM in_team, final TiledMap in_map)
	{
		this.tilePosition = EmpireWars.getTileIdx(new Vector(x, y));
		this.map = in_map;
		this.wallLayer = map.getLayerIndex("walls");
		this.roadLayer = map.getLayerIndex("road");
		this.pathStack = new Stack<>();
		
		nodes = new Node[200][50];
		for (int tx=0;tx<200;tx++) {
			for (int ty=0;ty<50;ty++) {
				nodes[tx][ty] = new Node(tx,ty);
			}
		}

		visited = new boolean[200][50];
		for (int tx=0;tx<200;tx++) {
			for (int ty=0;ty<50;ty++) {
				visited[tx][ty] = false;
			}
		}
	
	}
	
	public float manhattanDistance(Vector start, Vector target)
	{
		float dx = Math.abs(target.getX()-start.getX());
		float dy = Math.abs(target.getY()-start.getY());
		return dx+dy;
	}
	
	public boolean isTileBlocked(int x, int y){
		boolean blocked = (x < 0) || (y < 0) || (x >= 200 || (y >= 50));
		if(map.getTileId(x, y, wallLayer) != 0 || map.getTileId(x, y, roadLayer) == 0){
			blocked = true;
		}
		return blocked;
	}
	
	
	public void findPath(Vector start, Vector target)
	{
		this.pathStack.clear();
		current = null;
		
		for (int x=0;x<200;x++) {
			for (int y=0;y<50;y++) {
				nodes[x][y].reset();
			}
		}
		
		Vector startInTiles = EmpireWars.getTileIdx(start);
		Vector targetInTiles = EmpireWars.getTileIdx(target);
		
		int sx = (int)startInTiles.getX();
		int sy = (int)startInTiles.getY();
		
		int tx = (int)targetInTiles.getX();
		int ty = (int)targetInTiles.getY();
		
		nodes[sx][sy].cost = 0;
		nodes[sx][sy].depth = 0;
		closed.clear();
		open.clear();
		nodes[sx][sy].setOpen(true);
		open.add(nodes[sx][sy]);
		
		nodes[tx][ty].parent = null;
		
		while(open.size()!=0)
		{			
			current = (Node) open.first();
			distance = current.depth;
			
			if(current == nodes[tx][ty]){
				break;
			}
			
			current.setOpen(false);
			open.remove(current);
			
			current.setClosed(true);
			closed.add(current);
			
			for (int x=-1;x<2;x++) 
			{
				for (int y=-1;y<2;y++) 
				{					
					//ignore current tile
					if ((x == 0) && (y == 0)) {
						continue;
					}
					
					//ignore diagonal tiles
					if ((x != 0) && (y != 0)) {
						continue;
					}
					
					int xp = x + current.x;
					int yp = y + current.y;
					
					if(xp < 0 || yp < 0 || xp >= 200 || yp >= 50 || isTileBlocked(xp, yp)){
						continue;
					}

					float nextStepCost = current.cost + 1;
					Node neighbour = nodes[xp][yp];
					visited[xp][yp] = true;
					
					//found a better way to reach a node
					if (nextStepCost < neighbour.cost)
					{
						if (neighbour.isOpen())
						{
							neighbour.setOpen(false);
							open.remove(neighbour);
						}
						if (neighbour.isClosed())
						{
							neighbour.setClosed(false);
							closed.remove(neighbour);
						}
					}

					if (!neighbour.isOpen() && !neighbour.isClosed())
					{
						neighbour.cost = nextStepCost;
						neighbour.heuristic = manhattanDistance(new Vector(xp, yp), new Vector(tx, ty));
						neighbour.setParent(current);
						neighbour.setOpen(true);
						open.add(neighbour);
					}
				}
			}
		}
		
		Node tmp = nodes[tx][ty];
		while (tmp != nodes[sx][sy] && tmp != null) {
			this.pathStack.push(tmp);
			tmp = tmp.parent;
		}
	}
	
	
	private class PriorityList {
		private List<Node> list = new LinkedList();
		
		public Object first() {
			return list.get(0);
		}
		
		public void clear() {
			list.clear();
		}
		
		public void add(Node o) {
			// float the new entry 
			for (int i=0;i<list.size();i++) {
				if (((Comparable) list.get(i)).compareTo(o) > 0) {
					list.add(i, o);
					break;
				}
			}
			if (!list.contains(o)) {
				list.add(o);
			}
		}
		
		public void remove(Object o) {
			list.remove(o);
		}

		public int size() {
			return list.size();
		}
		
		public boolean contains(Object o) {
			return list.contains(o);
		}
		
		public String toString() {
			String temp = "{";
			for (int i=0;i<size();i++) {
				temp += list.get(i).toString()+",";
			}
			temp += "}";
			
			return temp;
		}
	}
	
	
	
}