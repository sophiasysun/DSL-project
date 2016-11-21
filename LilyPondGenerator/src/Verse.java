import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Verse {

	String name;
	ArrayList<String []> chords = new ArrayList<String []>();

	/**
	 * Verse constructor
	 * @return
	 */
	public Verse (String name, ArrayList<String []> chords) {
		this.name = name;
		this.chords = chords;
	}

	/**
	 * Return the name of a verse
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Return the array of chords in a verse
	 * @return
	 */
	public ArrayList<String[]> getChords() {
		return this.chords;
	}

	/**
	 * Print the chords in one verse 
	 * @return
	 */
	public String toString () {
		String s ="";
		String finalStr ="";

		// for all the chords in this verse
		for (int j=0; j<chords.size(); j++){
			
			// print each element of the chord 
			for (int i=0; i<chords.get(j).length; i++){
				s+=chords.get(j)[i]+ "|";
			}
			finalStr+=s+ "\n ";
			s ="";
		}
		return finalStr;
	}

}
