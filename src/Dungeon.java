import java.util.Set;

public class Dungeon {
	private Room entrance;
	
	public void print()
	{
		if(entrance != null) entrance.print();
	}

	public Room getEntrance() {
		return entrance;
	}

	public void setEntrance(Room entrance) {
		this.entrance = entrance;
	}
	
	public int TalismanTest(Room r)
	{
		return r.BreadthFirstTarget();
	}
}
