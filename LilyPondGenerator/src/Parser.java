import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
	NoteProducer np;
	Notes n = new Notes();
	ArrayList<String> sharps = n.getSharps();
	ArrayList<String> flats = n.getFlats();
	ArrayList<String> sharps_eb = n.getSharps_eb();
	ArrayList<String> flats_cf = n.getFlats_cf();
	
	StringManipulation str = new StringManipulation();

	ArrayList<Verse> song = new ArrayList<Verse> ();
	
	ArrayList<String []> currSectionChords = new ArrayList<String []>();
	HashMap<String, String> customNotes = new HashMap<String, String> ();
	HashMap<String, String []> customChordMap = new HashMap<String, String []> ();
	
	int offset = 0;
	boolean declaringNotesOnly = false;
	
	public Parser () throws FileNotFoundException, IOException {
		
		ArrayList<String> txtFile = new ArrayList<String> ();
		try (BufferedReader br = new BufferedReader(new FileReader("sample.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				// put all non-empty lines and non-comment lines into the String array of lines
				if (!line.equals("") && (!line.contains("//"))) { 
					txtFile.add(line);
				}
			}
		}

		/** parsing the first line **/
		// first line describes key signature and time signature of the song 
		String firstLine = txtFile.get(0); 
		String secondLine = txtFile.get(1); 

		/** PARSE TITLE IF THERE IS ONE **/
		String title=null;
		String composer = null;
		String [] arr;

		if (firstLine.contains("by")) {

			// title is the substring before the first space
			int endOfTitle = firstLine.indexOf(" ");
			title = firstLine.substring(0, endOfTitle);

			// composer is substring after last space
			int beforeComposer = firstLine.lastIndexOf(' ');
			composer = firstLine.substring(beforeComposer+1);

			arr = secondLine.split(" ");
		}

		else {
			arr = firstLine.split(" ");
		}

		/** OCTAVE **/

		String octave;
		String song_clef;

		// no octave specified
		//if ((arr[0].charAt(arr[0].length()-2)=='s' || arr[0].charAt(arr[0].length()-2)=='e')){
		if (arr[0].contains("bass") || arr[0].contains("treble")){
			octave = "";
			song_clef = arr[0].toLowerCase().substring(0, arr[0].length()-1);
		}

		//octave specified
		else {
			int begOfOct=0; // after clef is specified, check the number of octaves specified

			if (arr[0].contains("treble")) {
				begOfOct = 6;
			}
			else if (arr[0].contains("bass")) {
				begOfOct = 4;
			}
			octave = arr[0].toLowerCase().substring(begOfOct, arr[0].length()-1);
			song_clef = arr[0].toLowerCase().substring(0, begOfOct);
		}

		/** KEY SIGNATURE **/
		String song_root = "";
		String song_quality="";

		// no transpose
		if (arr.length==5) {
			song_root = str.convertSharpFlat(str.convertSharpFlat(arr[1].toLowerCase()));
			song_quality = arr[2].toLowerCase().substring(0, arr[2].length()-1);
			offset = 0;
			np = new NoteProducer(song_clef, octave, song_root, song_quality, Integer.parseInt(arr[3]), Integer.parseInt(arr[4]), offset, title, composer);
		}

		// transpose
		else if (arr.length==8) {
			if (arr[4].contains("up") || arr[4].contains("down")) {
				if (arr[4].contains("up")) {
					offset = Integer.valueOf(arr[5].substring(0, arr[5].indexOf(','))); 
				}

				else if (arr[4].contains("down")) {
					offset = -(Integer.valueOf(arr[5].substring(0, arr[5].indexOf(',')))); 
				}
				
				// original root and quality
				song_root = str.convertSharpFlat(str.convertSharpFlat(arr[1].toLowerCase()));
				arr[2] = arr[2].toLowerCase();
				song_quality = arr[2];
			}

			// if specifying original and new key 
			else{
				int oldK=0;
				int newK=0;
				String oldKey = arr[1];
				String newKey = arr[4];

				if (sharps.contains(oldKey) && sharps.contains(newKey)) {
					oldK = sharps.indexOf(oldKey);
					newK = sharps.indexOf(newKey);
				}

				else if (flats.contains(oldKey) && flats.contains(newKey)) {
					oldK = flats.indexOf(oldKey);
					newK = flats.indexOf(newKey);
				}
				
				else if (sharps_eb.contains(oldKey) && sharps_eb.contains(newKey)) {
					oldK = sharps_eb.indexOf(oldKey);
					newK = sharps_eb.indexOf(newKey);
				}
				
				else if (flats_cf.contains(oldKey) && flats_cf.contains(newKey)) {
					oldK = flats_cf.indexOf(oldKey);
					newK = flats_cf.indexOf(newKey);
				}
				
				offset = newK - oldK;
				song_root = str.convertSharpFlat(str.convertSharpFlat(arr[4].toLowerCase()));
				song_quality = arr[5].toLowerCase().substring(0, arr[5].length()-1); 
			}
			np = new NoteProducer(song_clef, octave, song_root, song_quality, Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), offset, title, composer);
		}

		/** parsing the remainder of the file **/
		String currSectionName=""; // name of verse whose chords you are looking at 
		boolean inVerse = false; // whether you are currently inside a verse

		for (int i = 1; i<txtFile.size(); i++) {
			String line = txtFile.get(i);
			declaringNotesOnly = false; // reset

			// if you're in a verse, you will either see a chord or a end bracket 
			if (inVerse) {
				if (line.contains("}")) {

					String verseName = new String(currSectionName.trim());
					ArrayList<String []> verseChords = new ArrayList<String[]> (currSectionChords);
					Verse v = new Verse (verseName, verseChords);
					song.add(v);
					// after you create the verse, you're no longer in verse, so reset currSectionChords
					inVerse=false;
				}

				// CHORDS!
				else {
					String [] constructChord = new String [5];

					if(customChordMap.containsKey(line)) {
						constructChord = customChordMap.get(line);
					}

					// NOT DECLARING NEW CUSTOM CHORD
					else if (!line.contains("=")) {
						// if the line contains the name of one of the custom chords
						// this is a reference to an existing custom chord

						// if the line does not contain the name of one of the custom chords, 
						// creating regular chord 
						String [] chordArray = line.split(" ");
						constructChord[0]=parseRoot(chordArray[0]);
						constructChord[1]=parseQuality(chordArray[1]);

						// argument at index 2 is duration
						if (!chordArray[2].contains(",")) {
							constructChord[2]=null; // no inversion 
							constructChord[3]=chordArray[2];
							constructChord[4]=null;
						}

						// if argument at index 2 is a letter or int, copy the inv and duration
						else {  
							constructChord[2] = parseInversion(chordArray[2]);
							constructChord[3]=chordArray[3]; // handle duration
							constructChord[4]=null;
						}
					}

					// DECLARING NEW CUSTOM CHORD
					else {
						String customName = line.substring(0, line.indexOf("="));
						customName=customName.trim();

						// if a custom chord with the same name already exists, don't create it
						if(customChordMap.containsKey(customName)) {
							System.out.println("Already created this custom chord! "+customName);
						}

						else {
							// DECLARING NOTES AND DURATION
							if (line.contains(",")) {
								String lineWithCustomChord = line.substring(line.indexOf('(')+1,line.indexOf(')'));
								String [] customChordInfo=lineWithCustomChord.split(",");
								String chordNotes = customChordInfo[0].trim();
								customChordInfo[1]= customChordInfo[1].trim();

								
								String firstElt =chordNotes;
								
								// if, within the parentheses, notes are referred to with the 
								// name of a group of customNotes, get notes stored at that name
								if (customNotes.containsKey(chordNotes)) {
									chordNotes = customNotes.get(chordNotes);
								}

								// INVERSION SPECIFIED
								if (customChordInfo.length==3) {
									constructChord[0]=null;
									constructChord[1]=null;
									constructChord[2]=customChordInfo[1]; // has inversion //parse inversion?
									constructChord[3]=customChordInfo[2].substring(1, 2); // duration
									constructChord[4]=chordNotes;
								}

								// for custom chord, make it a normal chord but make the root and quality null 
								// NO INVERSION SPECIFIED
								else if (customChordInfo.length==2) {
									constructChord[0]=null;
									constructChord[1]=null;
									constructChord[2]=null; // no inversion
									constructChord[3]=customChordInfo[1]; // duration
									constructChord[4]=chordNotes;
								}

								// add the constructed chord to the array of chords in this section 
								customChordMap.put(customName, constructChord);
							}

							// ONLY DECLARING NOTES nameOfCustomNoteGroup
							else {
								String notesOnly = line.substring(line.indexOf('(')+1,line.indexOf(')'));
								notesOnly = notesOnly.trim();
								customNotes.put(customName, notesOnly);
								declaringNotesOnly=true;
							}
						}
					}
					if (!declaringNotesOnly){
						// add the constructed chord to the array of chords in this section 
						currSectionChords.add(constructChord);
					}
				}

			}

			// if you're in a verse you will never see '{'
			else if (line.contains("{")) { // mark the index 
				currSectionChords.removeAll(currSectionChords);
				inVerse=true;
				line = line.trim();
				currSectionName= line.substring(0, line.indexOf("{"));
			}

			// if not in verse, and not starting a verse, you must be calling a verse 
			else 
			{
				for (int v=0, n = song.size(); v<n; v++) {
					// if the called verse already exists, which it should
					if (song.get(v).getName().equals(line)) {
						String verseName = new String(song.get(v).getName());
						ArrayList<String []> verseChords = (song.get(v).getChords());
						Verse calledVerse = new Verse (verseName, verseChords); 
						song.add(calledVerse);
					}
					else {
						System.out.println("verse doesn't exist yet");
					}
				}
			}
		}
		returnSong();
	}

	/**
	 * Parse chord root by converting to lower case and handling # and b 
	 * @param s
	 * @return
	 */
	public String parseRoot(String s) {
		s = s.toLowerCase();
		s = str.convertSharpFlat(s);
		return str.convertSharpFlat(s);
	}

	/**
	 * Parse chord root by converting diff possible ways to express quality and getting rid of comma 
	 * @param s
	 * @return
	 */
	public String parseQuality(String s) {
		s  = s.substring(0, s.length()-1);
		return convertChordQuality(s);
	}

	/**
	 * Parse chord root by converting diff possible ways to express quality and getting rid of comma 
	 * @param s
	 * @return
	 */
	public String parseInversion(String s) {
		s = s.toLowerCase();
		s = str.convertSharpFlat(s); // if inversion has "#" or "b", if so, convert to "sharp" or "flat"
		int commaLoc = s.indexOf(",");

		if (commaLoc !=-1) {
			return s.substring(0, commaLoc);
		}
		else return s;
	}

	/**
	 * clef, key signature, and time signature of the song 
	 * @return
	 */
	public NoteProducer returnSongHeader (){
		return np;
	}

	/**
	 * Anticipate possible user inputs and translate them into a string that the NoteProducer class can understand
	 * @param chord_quality
	 */
	public String convertChordQuality (String chord_quality) {
		if (chord_quality.equals("M") || chord_quality.equals("Major") || chord_quality.equals("maj")) {
			chord_quality = "major";
		}
		else if (chord_quality.equals("m") || chord_quality.equals("Minor") || chord_quality.equals("min")) {
			chord_quality = "minor";
		}
		else if (chord_quality.equals("d") || chord_quality.equals("Diminished")) {
			chord_quality = "diminished";
		}
		else if (chord_quality.equals("D7") || chord_quality.equals("Dominant7") ||  chord_quality.equals("Dom7")) {
			chord_quality = "dom7";
		}
		else if (chord_quality.equals("M7") || chord_quality.equals("Major7") || chord_quality.equals("Maj7")) {
			chord_quality = "maj7";
		}
		else if (chord_quality.equals("m7") || chord_quality.equals("Minor7") || chord_quality.equals("Min7")) {
			chord_quality = "min7";
		}
		else if (chord_quality.equals("d7") || chord_quality.equals("Diminished7") || chord_quality.equals("Dim7")) {
			chord_quality = "dim7";
		}
		return chord_quality;
	}
	
	/**
	 * Get all the verses and their respective chords  
	 * @return
	 */

	public ArrayList<Verse> returnSong (){
		for (int i=0; i<song.size(); i++) {
			Verse v = song.get(i);
			//System.out.println("sec name: " + v.getName() + "\n " + v.toString());
		}
		return song;
	}


}
