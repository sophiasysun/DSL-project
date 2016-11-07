import java.util.ArrayList;


import java.util.HashMap;
public class SectionBlock {
	
	SectionDeclaration sd = new SectionDeclaration();
	
	public HashMap<String, ArrayList<String>> sectionAndChords = new HashMap<String, ArrayList<String>> ();
	
	 public  SectionBlock (String sectionName) {
		 
		 sectionAndChords.put(sectionName, chords);
	}

}