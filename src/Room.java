import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class Room {
	
	public int distance;
	public int weight = 0;
	public int weighedDistance = 0;
	public int connections = 0;
	public HashMap<String, Room> paths = new HashMap<String, Room>();
	
	public Room(){
		paths.put("north", null);
		paths.put("east", null);
		paths.put("south", null);
		paths.put("west", null);
		
		weight = new Random().nextInt(30) + 10;
	}
	
	public void randomDungeon(int levels, Room[][] coords)
	{
		if(coords == null)
		{
			coords = new Room[30][30];
			coords[15][15] = this;
		}
		if(levels > 0)
		{
			for(Map.Entry<String, Room> roomEntry : paths.entrySet())
			{
				String dir = roomEntry.getKey();
				Room r = roomEntry.getValue();
				if(r != null) r.randomDungeon(levels - 1, coords);
				else{
					int[] dircoords = coordsFromDir(dir);
					if(new Random().nextInt(3) == 2){
					r = new Room();
					setPath(dir, r);
					r.randomDungeon(levels-1, coords);
					}
				}
			}
		}
	}
	
	public Room getNorth() {
		return paths.get("north");
	}
	public void setNorth(Room north) {
		setPath("north", north);
	}
	public Room getEast() {
		return paths.get("east");
	}
	public void setEast(Room east) {
		setPath("east", east);
	}
	public Room getSouth() {
		return paths.get("south");
	}
	public void setSouth(Room south) {
		setPath("south", south);
	}
	public Room getWest() {
		return paths.get("west");
	}
	public void setWest(Room west) {
		setPath("west", west);
	}
	
	public void setPath(String key, Room target)
	{
		if(paths.containsKey(key))
			paths.replace(key, target);
		else
		{
			paths.put(key, target);
		}
		if(target.paths.containsKey(this))
			target.paths.replace(counterPath(key), this);
		else
			target.paths.put(counterPath(key), this);
		connections++;target.connections++;
	}
	
	public void destroyPath(String dir)
	{
		Room counter = getPath(dir);
		String counterDir = counterPath(dir);
		if(counter != null)
		{
			paths.replace(dir, null);
			connections--;
			counter.paths.replace(counterDir, null);
			counter.connections--;
		}
	}
	
	public Room getPath(String dir)
	{
		return paths.get(dir);
	}
	
	public String counterPath(String path)
	{
		switch(path)
		{
			case "north":
				return "south";
			case "east":
				return "west";
			case "south":
				return "north";
			case "west":
				return "east";
		}
		System.out.println("fail " + path);
		return "";
	}
	
	public int[] coordsFromDir(String path)
	{
		switch(path)
		{
			case "north":
				return new int[]{0,1};
			case "east":
				return new int[]{1,0};
			case "south":
				return new int[]{0,-1};
			case "west":
				return new int[]{-1,0};
		}
		return new int[]{0,0};
	}
	public void print()
	{
		String[][] map = print(new ArrayList<Room>(), new String[30][30], 15, 15);
		for (int y = map.length - 1; y > 0; y--) {
			String line = "";
			for (int x = 0; x < map[y].length; x++) {
				if(map[x][y] == null)
					map[x][y] = "   ";
				line = line + map[x][y];
			}
			System.out.println(line);
			
		}
	}
	
	public String[][] print(ArrayList<Room> visited, String[][] map, int x, int y)
	{
		visited.add(this);
		if(getNorth() != null)
		{
			if(!visited.contains(getNorth()))
			{
				map = getNorth().print(visited, map, x, y+1);
			}
		}
		if(getWest() != null)
		{
			if(!visited.contains(getWest()))
				map = getWest().print(visited, map, x-1, y);
		}
		if(getEast() != null)
		{
			if(!visited.contains(getEast()))
				map = getEast().print(visited, map, x+1, y);
		}
		if(getSouth() != null)
		{
			if(!visited.contains(getSouth()))
			{
				getSouth().print(visited, map, x, y-1);
			}
		}
		if(this instanceof TargetRoom)
			map[x][y] = "t" + weight;
		else
			map[x][y] = "x" + weight;
		return map;
	}
	
	public int BreadthFirstTarget()
	{
		Queue<Room> queue = new LinkedList<Room>();
		Set<Room> visited = new HashSet<Room>();
		
		queue.add(this);
		this.distance = 0;
		Room next;
		while((next = queue.poll()) != null)
		{
			//System.out.println(next.distance);
			visited.add(next);
			for(Room adjacent : next.paths.values())
			{
				if(!visited.contains(adjacent) && !queue.contains(adjacent) && adjacent != null)
				{
					adjacent.distance = next.distance + 1;
					queue.add(adjacent);
					if(adjacent instanceof TargetRoom)
						return adjacent.distance;
				}
			}
		}
		return -1;
	}
	
	public Stack<String> Dijkstra()
	{
		Stack<String> path = new Stack<String>();
		Set<Room> visited = new HashSet<Room>();
		Queue<Room> queue = new LinkedList<Room>();
		queue.add(this);
		weighedDistance = weight;
		Room next;
		Room end = null;
		while((next = queue.poll()) != null)
		{
			Room minAdjacent = null;
			visited.add(next);
			for(Map.Entry<String, Room> adjacent : next.paths.entrySet())
			{
				if(adjacent.getValue() != null && !visited.contains(adjacent.getValue()))
				{
					if(adjacent.getValue().weighedDistance < adjacent.getValue().weight + next.weighedDistance)
						adjacent.getValue().weighedDistance = adjacent.getValue().weight + next.weighedDistance;
					if(adjacent.getValue() instanceof TargetRoom)
					{
						queue.clear();
						end = adjacent.getValue();
						break;
					}
					if(minAdjacent == null) 
						minAdjacent = adjacent.getValue();
					else if(adjacent.getValue().weighedDistance < minAdjacent.weighedDistance)
						minAdjacent = adjacent.getValue();
				}
			}
			if(!visited.contains(minAdjacent))
				if(!queue.contains(minAdjacent)) queue.add(minAdjacent);
		}
		
		Stack<Room> pathSimple = new Stack<Room>();
		
		Room cur = end;
		String dir = "";
		while(cur != null)
		{
			path.push(dir);
			pathSimple.push(cur);
			if(cur == this) break;
			Room smallestPath = null;
			for(Map.Entry<String, Room> set : cur.paths.entrySet())
			{
				if(set.getValue() != null)
					if(smallestPath == null){ 
						smallestPath = set.getValue();
						dir = counterPath(set.getKey());
					}
					else if(set.getValue().weighedDistance < smallestPath.weighedDistance) 
					{
						smallestPath = set.getValue();
						dir = counterPath(set.getKey());
					}
			}
			cur = smallestPath;
		}
		
		while (!path.isEmpty())
			System.out.println(path.pop());
		while (!pathSimple.isEmpty())
			System.out.println(pathSimple.pop().weight);
		
		return path;
	}
	
	public HashSet<Path> minimumTree()
	{
		HashSet<Path> tree = new HashSet<Path>();
		Set<Room> visited = new HashSet<Room>();
		Queue<Room> queue = new LinkedList<Room>();
		queue.add(this);

		Room next;
		while((next = queue.poll()) != null)
		{
			for(Map.Entry<String, Room> adjacent : next.paths.entrySet())
			{
				if(!visited.contains(adjacent.getValue()) && !queue.contains(adjacent.getValue()) && adjacent.getValue() != null)
				{
					tree.add(new Path(next, adjacent.getKey()));
					visited.add(adjacent.getValue());
					queue.add(adjacent.getValue());
				}
			}
		}
		
		return tree;
	}
	
	public boolean minTreeContains(HashSet<Path> tree, Room r, String dir)
	{
		System.out.println("checking " + r.weight + " " + dir);
		for (Path path : tree) {
			if(path.room == r && path.direction == dir)
				return true;
			if(path.room == r.getPath(dir) && path.direction == counterPath(dir))
				return true;
		}
		return false;
	}
	
	public void holyHandgrenade()
	{
		HashSet<Path> tree = minimumTree();
		Queue<Room> queue = new LinkedList<Room>();
		HashSet<Room> visited = new HashSet<Room>();
		HashSet<Path> targets = new HashSet<Path>();
		int toCollapse = 15;
		boolean canCollapse = true;

		queue.add(this);

		Room next;
		while((next = queue.poll()) != null)
		{
			if(toCollapse > 0 && canCollapse && !visited.contains(next))
			{
				for(Map.Entry<String, Room> adjacent : next.paths.entrySet())
				{
					if(adjacent.getValue() != null)
					{
						if(!minTreeContains(tree, next, adjacent.getKey()))
						{
							targets.add(new Path(next, adjacent.getKey()));
							System.out.println("adding " + next.weight + " " + adjacent.getValue().weight + " " + adjacent.getKey());
						}
						queue.add(adjacent.getValue());
					}
				}
			}
			visited.add(next);
		}
		if(targets.size() > 30)
		{
			for(Path p : targets)
			{
				//p.room.setPath(p.direction, null);
				p.room.destroyPath(p.direction);
				System.out.println("can blow up " + p.room.weight + " " + p.direction);
			}
		}
	}
	
	public Set<Room> roomList()
	{
		return roomList(new HashSet<Room>());
	}
	
	public Set<Room> roomList(Set<Room> rooms)
	{
		rooms.add(this);
		for(Room r : paths.values())
			if(r != null && !rooms.contains(r))
				rooms = r.roomList(rooms);
		return rooms;
	}
	
	class Path
	{
		public Room room;
		public String direction;
		public Path(Room r, String dir)
		{
			room = r;
			direction = dir;
		}
	}
}
