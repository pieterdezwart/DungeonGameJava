import java.util.HashSet;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Dungeon d = new Dungeon();
		Room r1 = new Room();
		Room r2 = new Room();
		Room r3 = new Room();
		Room r4 = new Room();
		Room r5 = new Room();
		Room r6 = new Room();
		r6.setSouth(r2);
		r6.setEast(r5);
		
		d.setEntrance(r1);
		r1.setNorth(r2);
		r2.setWest(r3);
		r2.setEast(r4);
		r4.setNorth(r5);
		r5.setNorth(new TargetRoom());
		
		r1.Dijkstra();
		for(Room r : r1.roomList())
			System.out.println("w" + r.weighedDistance + " " + r.weight);
		
		HashSet<Room.Path> tree = r1.minimumTree();
		for(Room.Path path : tree)
			System.out.println("mintree " + path.room.weight + " " + path.direction);
		
		r1.holyHandgrenade();
		System.out.println("distance to target room: " + d.TalismanTest(r1));
		
		
		d.print();
	}

}
