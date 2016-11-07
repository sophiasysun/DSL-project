import java.util.ArrayList;


import java.util.HashMap;
public class SectionDeclaration {
	
	// stores the different states in order 
	ArrayList<ArrayList<String[]>> song = new ArrayList<ArrayList<String[]>>();

	public HashMap<String, ArrayList<String>> sectionAndChords = new HashMap<String, ArrayList<String>> ();
	public SectionDeclaration() {
		
	}
	
	 public  SectionDeclaration  (String sectionName, ArrayList<String> chords) {
		 
		 sectionAndChords.put(sectionName, chords);
		 song.add(e)
	}

}
