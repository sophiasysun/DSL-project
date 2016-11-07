import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
	NoteProducer np;

	public ArrayList<Verse> song = new ArrayList<Verse> ();
	ArrayList<String []> currSectionChords = new ArrayList<String []>();
	
	public Parser () throws FileNotFoundException, IOException {
		ArrayList<String> txtFile = new ArrayList<String> ();
		try (BufferedReader br = new BufferedReader(new FileReader("sample.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					txtFile.add(line);
				}
			}
		}

		String firstLine = txtFile.get(0);
		String [] arr = firstLine.split(" ");
		
		String clef = arr[0].toLowerCase().substring(0, arr[0].length()-1);
		String root = arr[1].toLowerCase();
		String quality = arr[2].toLowerCase().substring(0, arr[2].length()-1);

		// wait am i doing this correct? note producer main calls this new object creation
		np = new NoteProducer(clef, root, quality, Integer.parseInt(arr[3]), Integer.parseInt(arr[4]));

		String currSectionName="";
		boolean inVerse = false;
	
		for (int i = 1; i<txtFile.size(); i++) {
			String line = txtFile.get(i);

			// if you're in a verse, you will either see a chord or a end bracket 
			if (inVerse) {
				if (line.contains("}")) {
					
					String verseName = new String(currSectionName);
					ArrayList<String []> verseChords = new ArrayList<String[]> (currSectionChords);
					Verse v = new Verse (verseName, verseChords);
					
					
				   // System.out.println("sec name: " + currSectionName + "  chords: " + v.toString(currSectionName));
					song.add(v);
		
					// after you create the verse, you're no longer in verse, so reset currSectionChords
					inVerse=false;
					
					// this is the issue
					currSectionChords.removeAll(currSectionChords);
				}

				// chord
				else {
				
					String [] chordArray = line.split(" ");
					String [] constructChord = new String [4];
					constructChord[0]=chordArray[0]; 
					constructChord[1]=chordArray[1].substring(0, chordArray[1].length()-1); 

					// the third argument is duration
					if (!chordArray[2].contains(",")) {
						// make sure duration is an int 
						constructChord[2]=null;
						constructChord[3]=chordArray[2];
					}

					// if argument at index 2 is a letter or int, copy the inv and duration
					else  {  
						constructChord[2]=chordArray[2].substring (0, 1); ;
						constructChord[3]=chordArray[3];
					}
					// add the constructed chord to the array of chords in this section 
					currSectionChords.add(constructChord);
				}
			}

			// if you're in a verse you will never see {
			else if (line.contains("{")) { // mark the index 
				
				inVerse=true;
				line = line.trim();
				currSectionName= line.substring(0, line.indexOf("{"));
			}
			
			// if not in verse, and not starting a verse, you must be calling a verse 
			else {
				
				/// THIS DOES NOT WORK YET
				line = line.trim();
				for (int v=0; v<song.size(); v++) {
				
					// if the called verse already exists, which it should
					if (song.get(i).getName().equals(line)) {
					
						String verseName = new String(song.get(i).getName());
						ArrayList<String []> verseChords = new ArrayList<String[]> (song.get(i).getChords());
						Verse calledVerse = new Verse (verseName, verseChords); 
						song.add(calledVerse);
					}
					else {
						System.out.println("verse doesn't exist yet");
					}
				}
				
			}
		}
		System.out.println("song size right before calling return song: " + song.size());
		
		System.out.println();
		returnSong();
	}
	
	public NoteProducer returnSongHeader (){
		return np;
	}
	
	public ArrayList<Verse> returnSong () {
	for (int i=0; i<song.size(); i++) {
		Verse v = song.get(i);
			
		// why is verse 1 printed twice, and verse 2 also printed twice??
			 System.out.println("return song, sec name " + v.getName() + " : " + v.toString(song.get(i).getName()));
		}
		return song;
	}
}