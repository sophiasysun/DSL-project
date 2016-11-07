import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Verse {

	String name;
	ArrayList<String []> chords = new ArrayList<String []>();

	public Verse (String name, ArrayList<String []> chords) {
		this.name = name;
		this.chords = chords;

	}

	public String getName() {
		return this.name;
	}
	
	public ArrayList<String[]> getChords() {
		return this.chords;
	}


	public String toString (String name) {
		String s ="";
		String finalStr ="";

		for (int j=0; j<chords.size(); j++){
			for (int i=0; i<chords.get(j).length; i++){

				s+=chords.get(j)[i]+ " ";
			}
			System.out.println(s);
			System.out.println();
			finalStr+=s;
			s="";
		}
		return finalStr;
	}

}
