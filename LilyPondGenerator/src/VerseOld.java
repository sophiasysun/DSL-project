import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class VerseOld {

	String name;
	ArrayList<String []> chords = new ArrayList<String []>();
	HashMap<String,ArrayList<String []>> verse = new HashMap<String,ArrayList<String []>>();

	public VerseOld (String name, ArrayList<String []> chords) {
		this.name = name;
		this.chords = chords;
		verse.put(name, chords);
	}

	public String getName() {
		return this.name;
	}
	
	public ArrayList<String []> getChords (String name) {
		return verse.get(name);
	}

	public Verse cloneVerse (String name) {
		return new Verse (name, verse.get(name));
	}

	public int size(){
		return verse.size();
	}

	public Set<String> getAllVerseNames () {
		return verse.keySet();
	}

	public String toString (String name) {
		String s ="";
		String finalStr ="";
		ArrayList<String []> theChord = verse.get(name);
		for (int i =0; i< theChord.size(); i++) {
			for (int j=0; j<theChord.get(i).length; j++){
				s+=theChord.get(i)[j]+ " |  ";
			}
			System.out.println(s);
			System.out.println();
			finalStr+=s;
			s="";
		}
		return finalStr;
	}
}
