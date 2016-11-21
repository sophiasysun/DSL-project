import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class NoteProducer {

	private static String clef;
	private static String octave;
	private static String songKey;
	private static String songQuality;
	private static int tS_top;
	private static int tS_bot;
	private static final int STEPS=12;
	private int offset;
	public Notes n = new Notes();

	public QualityIntervals intervals  = new QualityIntervals ();

	// will be assigned according to the quality of the chord 
	// (diff quality => diff intervals between each note)
	public ArrayList<Integer> intervalArray = new ArrayList<Integer> ();

	public ArrayList<String> outputArray= new ArrayList<String> ();

	// maybe instead of declaring it like this you have to set it??? 
	public NoteProducer(String clef, String octave, String songKey, String songQuality, int tS_top, int tS_bot, int offset) {
		this.clef = clef;
		this.octave = octave;
		this.songKey = songKey;
		this.songQuality = songQuality;
		this.tS_top = tS_top;
		this.tS_bot = tS_bot;
		this.offset = offset;
	}

	public ArrayList<String> sharps;
	public ArrayList<String> flats;
	public ArrayList<Integer> majorTriadIntervals;
	public ArrayList<Integer> minorTriadIntervals;
	public ArrayList<Integer> dom7Intervals;
	public ArrayList<Integer>maj7Intervals;
	public ArrayList<Integer> min7Intervals;
	public ArrayList<Integer>dim7Intervals;
	public char [] adjNotes = new char [7];
	
	String [] eis_maj = {"eis", "gisis", "bis"}; 

	HashMap<String, String[]> dimMap = new HashMap<String, String[]>();

	/**
	 * Generate notes for every possible diminished triad 
	 * @return
	 */
	public HashMap<String, String[]> createDim() {

		// c sharp major is not taken care of
		String [] c_dim = {"c","ees","ges"};
		String [] cs_dim= {"cis","e","g"};
		String [] df_dim= {"des","fes","aeses"};
		String [] d_dim= {"d","f","aes"};
		String [] ds_dim= {"dis","fis","a"};
		String [] ef_dim= {"ees","ges","beses"};
		String [] e_dim= {"e","g","bes"};
		String [] f_dim= {"f","aes","ces"};
		String [] fs_dim= {"fis","a","c"};
		String [] gf_dim= {"ges","beses","deses"};
		String [] g_dim= {"g","bes","des"};
		String [] gs_dim= {"gis","b","d"};
		String [] af_dim= {"aes","ces","eeses"};
		String [] a_dim= {"a","c","ees"};
		String [] as_dim= {"ais","cis","e"};
		String [] bf_dim= {"bes","des","fes"};
		String [] b_dim= {"b","d","f"};
		////

		// changed from sharp to is
		dimMap.put("c", c_dim);
		dimMap.put("cis", cs_dim);
		dimMap.put("des", df_dim);
		dimMap.put("d", d_dim);
		dimMap.put("dis", ds_dim);
		dimMap.put("ees", ef_dim);
		dimMap.put("e", e_dim);
		dimMap.put("f", f_dim);
		dimMap.put("fis", fs_dim);
		dimMap.put("ges", gf_dim);
		dimMap.put("g", g_dim);
		dimMap.put("gis", gs_dim);
		dimMap.put("aes", af_dim);
		dimMap.put("a", a_dim);
		dimMap.put("ais", as_dim);
		dimMap.put("bes", bf_dim);
		dimMap.put("b", b_dim);
		
		
		
		return dimMap;
	}

	/**
	 * Arrays of notes, in increasing half steps
	 * @return
	 */
	public void assignSharpsFlats() {

		majorTriadIntervals = intervals.getMaj();
		minorTriadIntervals = intervals.getMaj();

		dom7Intervals = intervals.getDom7();
		maj7Intervals = intervals.getMaj7();
		min7Intervals = intervals.getMin7();
		dim7Intervals = intervals.getDim7();

		sharps = n.getSharps();
		flats = n.getFlats();
	}

	public void setAdjNotes () {
		adjNotes[0]='a';
		adjNotes[1]='b';
		adjNotes[2]='c';
		adjNotes[3]='d';
		adjNotes[4]='e';
		adjNotes[5]='f';
		adjNotes[6]='g';
	}
	
	/**
	 * If array contains String, return the index of that the String's appearance
	 * @param array
	 * @param s
	 * @return
	 */
	public int returnIndex(ArrayList<String> array, String s) {
		if (array == null) {
			return -1;
		}
		for (int i=0;i<array.size(); i++) {
			if(array.get(i).equals(s))  {
				return i;
			}
		}
		return -1;
	}

	/**
	 * If array contains char, return the index of that the char's appearance
	 * @param array
	 * @param s
	 * @return
	 */
	public int c_index(char [] array, char c) {
		if (array == null) {
			return -1;
		}
		for (int i=0;i<array.length; i++) {
			if(array[i]==c)  {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Shuffles the notes in an array to the right for inversion 
	 * @param array
	 * @param noteNumber
	 * @return
	 */
	public String [] shuffleArrayIndex(ArrayList<String> array, int noteNumber) {
		String[] shuffled = new String[array.size()];

		for (int i = 0;i <array.size(); i++) {
			shuffled[i] = array.get((i+noteNumber) % array.size());
		}
		String [] result = shuffled;
		return result;
	}

	/** 
	 * Checks if adjacent notes in a chord have adjacent note names 
	 * @param array
	 * @param noteNumber
	 * @return
	 */
	public boolean adjacentNoteNames() {

		char note1=outputArray.get(0).charAt(0);
		char note2=outputArray.get(1).charAt(0);
		char note3=outputArray.get(2).charAt(0);

		// check if the first two and the second two are adjacent
		if ((Math.abs(c_index(adjNotes, note2) - c_index(adjNotes, note1)) == 1) ||
				(Math.abs(c_index(adjNotes, note2) - c_index(adjNotes, note1)) == 6) ||
				(Math.abs(c_index(adjNotes, note3) - c_index(adjNotes, note2)) == 1) ||
				(Math.abs(c_index(adjNotes, note3) - c_index(adjNotes, note2)) == 6)) {
			return true;
		}
		return false;
	}

	/**
	 * Assign intervalArray based on specified chord quality
	 * @param quality
	 */
	public void intArray(String quality) {

		if (quality.equals("major")){
			intervalArray = majorTriadIntervals;
		}
		else if (quality.equals("minor")){
			intervalArray = minorTriadIntervals;
		}
		else if (quality.equals("dom7")){
			intervalArray = dom7Intervals;
		}
		else if (quality.equals("maj7")){
			intervalArray = maj7Intervals;
		}
		else if (quality.equals("min7")){
			intervalArray = min7Intervals;
		}
		else if (quality.equals("dim7")){
			intervalArray = dim7Intervals;
		}
	}

	/**
	 * Produce the LilyPond syntax
	 * @param root
	 * @param quality
	 * @param inv
	 * @param key
	 * @param dur
	 * @return
	 */
	public String singleChordString (String root, String quality, String inv, String notes) {
		
		
		ArrayList<String> qualityArrayUsed= new ArrayList<String> ();

		String noteString="";
		// if inverse not specified, don't shuffle order 
		int rootNoteIndex = 0;

		if (notes!=null) {
			String[] notesInCustomChord = notes.split(" ");
			for (int i =0; i<notesInCustomChord.length; i++) {
				outputArray.add(convertSharpFlat(notesInCustomChord[i]));
			}
		}

		else {
			
			root = root.trim();
			quality = quality.trim();
			// default assign to sharps (all roots except for flat roots)
			int rootIndex = returnIndex(sharps, root.trim());

			qualityArrayUsed=sharps;

			// if root not in sharps, go to flats
			if (rootIndex==-1) {
				rootIndex = returnIndex(flats, root.trim());
				qualityArrayUsed=flats;
				rootIndex = returnIndex(flats, root);
			}

			quality = quality.trim();
			intArray(quality); 

			// diminished chords 
			if (quality.equals("diminished")) {
				for (int i=0; i<dimMap.get(root).length; i++ ){
					outputArray.add(dimMap.get(root)[i]);
				}
			}
			
			// why is this not working omg
			else if (root=="eis" && quality=="major") {
				for (int i=0; i<eis_maj.length; i++) {
					outputArray.add(eis_maj[i]);
				}
			}
			
			// default use sharps array; if adjacent, use flats array
			else if (intervalArray!=null) {
				
				for (int i=0;i<intervalArray.size();i++) {
					outputArray.add(qualityArrayUsed.get((rootIndex+ intervalArray.get(i) + offset) % STEPS)); //handle offset 
				}

				if (adjacentNoteNames()) {
					if (qualityArrayUsed.equals(sharps)) {
						qualityArrayUsed = flats;
					}
					else {
						qualityArrayUsed = sharps;
					}
					for (int i=0;i<intervalArray.size();i++) {
						int intervalNextNote = rootIndex+ intervalArray.get(i);
						outputArray.set((i+1), qualityArrayUsed.get(intervalNextNote % STEPS)); //f major, a, 4 isn't working correctly here, prob bc mod 
					}
				}
			}
		}

		if (inv!=null) {

			// if inversion specified by a root note name 
			if (inv.matches("[A-Za-z]")) {
				if (intervalArray!=null) {

					int rootInTransposedKey=0;
					if (qualityArrayUsed.contains(inv)) {
						rootInTransposedKey = (qualityArrayUsed.indexOf(inv) + offset) % STEPS;
						inv = qualityArrayUsed.get(rootInTransposedKey);
					}

					rootNoteIndex = returnIndex(outputArray, inv);
					if (rootNoteIndex==-1){
						//throw new ArrayIndexOutOfBoundsException();
						System.out.println("This inversion is not possible!");
					}
				}
			}
			else {
				// if inversion is represented by a number
				// only let diminished do a 3rd inversion
				if (inv.equals("1")||inv.equals("2")||inv.equals("3")) {
					rootNoteIndex = Integer.parseInt(inv);
				}
			}
		}

		// shuffle the array and add each element to noteString 
		String [] result=shuffleArrayIndex(outputArray, rootNoteIndex);

		// lily pond uses "is" for sharp and "es" for flat
		for (int i=0;i<result.length;i++) {
			if(result[i].contains("flat")){
				result[i] = result[i].substring(0, 1) + "es";
			}
			else if(result[i].contains("sharp")){
				result[i] = result[i].substring(0, 1) + "is";
			}
			noteString+=result[i]+ " ";
		}
		return noteString;
	}

	

	
	/**
	 * Log the message in a txt file
	 * @author sophiasysun
	 *
	 */
	public static class Logger {
		public static void log(String message) throws IOException { 
			PrintWriter out = new PrintWriter(new FileWriter("testingParser.txt", true), true);
			out.write(message);
			out.close();
		}
	}

	

	/**
	 * WHY DOES THIS NOT WORK
	 * @param note
	 * @return
	 */
	public String convertSharpFlat(String note){
		if (note.length()==1) {
			System.out.println("length1");
			return note;
		}
		else if (note.contains("#")) {
			int sharp = note.indexOf("#");
			note = note.substring(0, sharp);
			note+="is";
		}
		else if (note.contains("b")) {
			int flat = note.indexOf("b");
			if (flat>0) {
			note = note.substring(0, flat);
			note+="es";
			}
		}
		return note;
	}
	public static void main(String[] args) throws IOException {

		Parser parser = new Parser();
		NoteProducer np = parser.returnSongHeader(); // clef, key signature, and time signature of the song
		ArrayList<Verse> song = parser.returnSong();

		np.assignSharpsFlats();
		np.setAdjNotes();
		np.createDim();

		String chordOutput ="";
		String extraLine = "";

		// for every verse
		for (int v=0; v<song.size(); v++) {

			Verse verse = song.get(v);
			String verseName = song.get(v).getName();

			for (int i=0; i<verse.getChords().size(); i++) {

				if (i==0) {
					extraLine = "\n   " + "% "+ verseName + "\n   ";
				}
				else {
					extraLine ="";
				}

				String root = verse.getChords().get(i)[0];
				System.out.println(root);
				
				
				String quality = verse.getChords().get(i)[1];
				String optional_inv = verse.getChords().get(i)[2];
				String duration = verse.getChords().get(i)[3];
				String notes = verse.getChords().get(i)[4];
				chordOutput+=extraLine + "< "+ (np.singleChordString(root, quality, optional_inv, notes))+ "> "+ duration + "\n   ";
				
				
				
				np.outputArray.clear();
			}
		}

		String output = "\\score {" + "\n " + 
				"\\relative c" + octave + "{" +  "\n "  +  
				" \\clef " + clef + "\n " + 
				" \\time " + tS_top + "/" + tS_bot + 
				"{ " + "\n " +
				"  \\key " + songKey + 
				"  \\" + songQuality + 
				"{" + "  \n   "+ chordOutput +"}" + "\n" + 
				"  }" +"\n" + " }" + "\n" + "}" + "\n";

		System.out.println(output);
		//Logger.log(output);

	}
}
