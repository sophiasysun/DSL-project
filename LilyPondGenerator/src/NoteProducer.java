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

	// tried to make progress on solving offset issue 
	private static String clef;
	private static String octave;
	private static String songKey;
	private static String songQuality;
	private static int tS_top;
	private static int tS_bot;
	private static final int STEPS=12;
	private int offset;
	private String title;
	private String composer;

	public boolean usedSharps;
	public boolean usedFlats;
	public boolean nonaccidentalRoot;
	boolean sharpsTranspose = false;
	boolean flatsTranspose = false;

	StringManipulation str = new StringManipulation();

	public QualityIntervals intervals  = new QualityIntervals ();
	public ArrayList<Integer> intervalArray = new ArrayList<Integer> ();
	public ArrayList<String> outputArray= new ArrayList<String> ();

	// from Notes class
	public Notes n = new Notes();
	public ArrayList<String> sharps = n.getSharps();
	public ArrayList<String> flats = n.getFlats();
	public ArrayList<String> sharps_eb = n.getSharps_eb();
	public ArrayList<String> flats_cf = n.getFlats_cf();
	public HashMap<String, ArrayList<String>> enharmonicChords = n.getEnharmonicChords();
	public HashMap<String, String[]> dimMap = n.getDimChords();
	public ArrayList<String> sharpExceptions = n.getSharpExceptions();
	public ArrayList<String> flatExceptions = n.getFlatExceptions();

	// Arrays of notes, in increasing half steps
	public ArrayList<Integer> majorTriadIntervals = intervals.getMaj();;
	public ArrayList<Integer> minorTriadIntervals = intervals.getMaj();
	public ArrayList<Integer> dom7Intervals = intervals.getDom7();
	public ArrayList<Integer> maj7Intervals = intervals.getMaj7();
	public ArrayList<Integer> min7Intervals = intervals.getMin7();
	public ArrayList<Integer> dim7Intervals = intervals.getDim7();

	public char [] adjNotes = new char [7];

	ArrayList<String> qualityArrayUsed= new ArrayList<String> ();
	int chordRootIndex = 0;
	int invIndex = 0;
	

	String updatedChordRoot;
	String updatedChordInv;

	// maybe instead of declaring it like this you have to set it??? 
	public NoteProducer(String clef, String octave, String songKey, String songQuality, int tS_top, int tS_bot, int offset, String title, String composer) {
		this.clef = clef;
		this.octave = octave;
		this.songKey = songKey;
		this.songQuality = songQuality;
		this.tS_top = tS_top;
		this.tS_bot = tS_bot;
		this.offset = offset;
		this.title = title;
		this.composer = composer;

		if (this.songKey.contains("is")) {
			sharpsTranspose = true;
			flatsTranspose = false;
		}
		else if (this.songKey.contains("es")) {
			flatsTranspose = true;
			sharpsTranspose = false;
		}

		// no sharp no flat in root 
		else {
			// transpose up 
			if (offset>0){
				sharpsTranspose = true;
				flatsTranspose = false;
			}
			// transpose down 
			else {
				flatsTranspose = true;
				sharpsTranspose = false;
			}
		}			

		int indexOfSongKey;

		if (sharpsTranspose) {
			indexOfSongKey = sharps.indexOf(songKey);
			songKey =  sharps.get((indexOfSongKey+ offset + STEPS)%STEPS);
			songKey = songKey.replace("is", "#");
		}

		else if (flatsTranspose) {
			indexOfSongKey =  flats.indexOf(songKey);
			songKey = flats.get((indexOfSongKey+ offset + STEPS)%STEPS);
			songKey = songKey.replace("es", "b");
		}
		this.songKey = songKey;
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
	 * Translate letter inversion into integer inversion
	 * by finding the inversion relationship between the 
	 * original chordRoot and original inversionNote
	 * @param root
	 * @param inv
	 */
	public void translateToNumericalInversion (String root, String inv) {
		
		ArrayList<String> tempArray = new ArrayList<String>();
		int oldRootIndex = qualityArrayUsed.indexOf(root);
		for (int i=0;i<intervalArray.size();i++) {
			tempArray.add(qualityArrayUsed.get((oldRootIndex+ intervalArray.get(i) + STEPS) % STEPS));
		}
		
		for (int i = 0;i<tempArray.size(); i++) {
			if (inv.equals(tempArray.get(i))){
				updatedChordInv = String.valueOf(i);
			}
		}
	}

	/**
	 * Shuffles the notes in an array to the right for inversion 
	 * @param array
	 * @param noteNumber
	 * @return
	 */
	public String [] shuffleArrayIndex(ArrayList<String> array, int noteNumber) {
		String[] shuffled = new String[array.size()];
		for (int i = 0; i <array.size(); i++) {
			shuffled[i] = array.get((i+noteNumber) % array.size());
		}
		String [] result = shuffled;
		return result;
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
	 * Checks if adjacent notes in a chord have adjacent note names 
	 * @param array
	 * @param noteNumber
	 * @return
	 */
	public boolean adjacentNoteNames() {
		setAdjNotes();
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
	 * Creates header with title and composer, if specified by user
	 * @param title
	 * @param composer
	 * @return
	 */
	public String createHeader (String title, String composer) {

		if (title==null && composer==null) {
			return "";
		}
		else {
			return "\\header { " + "\n " + 
					"title = " + title + "\n " + "composer = " + "\"" + composer + "\"" + "\n" + "}" + "\n";
		}
	}

	public void convertRootAndInversionOfChord (String root, String quality, String inv) {

		// if inversion is specified using a letter, make sure both chordRoot and invNote
		// can be found in the qualityArray
		// then find the new root given the offset
		// and use "translateToNumericalInversion" to find the numerical inversion 
		if (inv.matches("[A-Za-z]+")) {

			if (sharpsTranspose) {
				if (sharps.contains(root) && sharps.contains(inv)) {
					qualityArrayUsed = sharps;
				}

				else if (sharps_eb.contains(root) && sharps_eb.contains(inv) ) {
					qualityArrayUsed = sharps_eb;
				}

				else if (flats.contains(root) && flats.contains(inv)) {
					qualityArrayUsed = flats;
				}

				else if (flats_cf.contains(root) && flats_cf.contains(inv)) { 
					qualityArrayUsed = flats_cf;
				}
			}

			else if (flatsTranspose) {
				if (flats.contains(root) && flats.contains(inv)) {
					qualityArrayUsed = flats; 
				}
				else if (flats_cf.contains(root) && flats_cf.contains(inv)) { 
					qualityArrayUsed = flats_cf; 
				}
				else if (sharps.contains(root) && sharps.contains(inv)) {
					qualityArrayUsed = sharps;
				}
				else if (sharps_eb.contains(root) && sharps_eb.contains(inv)) {
					qualityArrayUsed = sharps_eb;
				}
			}

			chordRootIndex = qualityArrayUsed.indexOf(root);
			updatedChordRoot = qualityArrayUsed.get((chordRootIndex + offset + STEPS)%STEPS);
			chordRootIndex = qualityArrayUsed.indexOf(updatedChordRoot);
			
			translateToNumericalInversion(root, inv);
			
			if (updatedChordRoot.equals("b") || updatedChordRoot.equals("e")) {
				qualityArrayUsed = sharps;
			}
			else if (updatedChordRoot.equals("c") || updatedChordRoot.equals("f")) {
				qualityArrayUsed = flats;
			}
		}


		// if inversion is specified using a number 
		else {
			if (sharpsTranspose) {
				if (sharps.contains(root)) {
					qualityArrayUsed = sharps;
				}

				else if (sharps_eb.contains(root)) {
					qualityArrayUsed = sharps_eb;
				}

				else if (flats.contains(root)) {
					qualityArrayUsed = flats;
				}

				else if (flats_cf.contains(root)) { 
					qualityArrayUsed = flats_cf;
				}
			}

			else if (flatsTranspose) {
				if (flats.contains(root)) {
					qualityArrayUsed = flats; 
				}
				else if (flats_cf.contains(root)) { 
					qualityArrayUsed = flats_cf; 
				}
				else if (sharps.contains(root)) {
					qualityArrayUsed = sharps;
				}
				else if (sharps_eb.contains(root)) {
					qualityArrayUsed = sharps_eb;
				}
			}
			chordRootIndex = qualityArrayUsed.indexOf(root);
			updatedChordRoot = qualityArrayUsed.get((chordRootIndex + offset + STEPS)%STEPS);
			chordRootIndex = qualityArrayUsed.indexOf(updatedChordRoot);

			if (updatedChordRoot.equals("b") || updatedChordRoot.equals("e")) {
				qualityArrayUsed = sharps;
			}
			else if (updatedChordRoot.equals("c") || updatedChordRoot.equals("f")) {
				qualityArrayUsed = flats;
			}
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
		
		// reset
		updatedChordRoot = null;
		updatedChordInv = null;

		String noteString="";
		// if inverse not specified, don't shuffle order 
		int rootNoteIndex = 0;

		if (inv==null) {
			inv = "0";
			updatedChordInv = "0";
		}
		
		// if parsing custom chord 
		if (notes!=null && root == null && quality == null) {

			// populate outputArray with customNotes
			String[] notesInCustomChord = notes.split(" ");
			for (int i =0; i<notesInCustomChord.length; i++) {
				outputArray.add(str.convertSharpFlat(notesInCustomChord[i]));
			}
			
			// get numerical inversion given bottom note of chord 
			for (int i =0; i<notesInCustomChord.length; i++) {
				if (outputArray.get(i).equals(inv)) {
					updatedChordInv = Integer.toBinaryString(i);
				}
			}
		}

		// if parsing non-custom chord 
		else {
			root = root.trim();
			quality = quality.trim();
			intArray(quality); 


			// RESET ROOT 
			convertRootAndInversionOfChord(root, quality, inv);

			String root_and_quality = updatedChordRoot+" "+quality;
			
			// 2) handle diminished chords 
			if (quality.equals("diminished")) {
				for (int i=0; i<dimMap.get(updatedChordRoot).length; i++ ){
					outputArray.add(dimMap.get(updatedChordRoot)[i]);
				}
			}

			// 3) handle ## or bb
			else if (enharmonicChords.containsKey(root_and_quality)){
				specialChords(root_and_quality);
				intervalArray = null; 
			}

			// 4) all other chords
			else {

				for (int i=0;i<intervalArray.size();i++) {
					outputArray.add(qualityArrayUsed.get((chordRootIndex+ intervalArray.get(i) + STEPS) % STEPS)); //handle offset 
				}

				if (adjacentNoteNames()) {

					if (qualityArrayUsed.equals(sharps)) {
						qualityArrayUsed = flats;
					}
					else if (qualityArrayUsed.equals(flats)) {
						qualityArrayUsed = sharps;
					}
					for (int i=0;i<intervalArray.size();i++) {
						outputArray.set(i, qualityArrayUsed.get((chordRootIndex+ intervalArray.get(i) + STEPS) % STEPS)); //handle offset 

					}
				}
			}
		}


		// HANDLE INVERSION (note name inversions already converted to integers) 
		int inversion = Integer.valueOf(updatedChordInv);
		if (inversion<outputArray.size()) {
			rootNoteIndex = Integer.parseInt(updatedChordInv);
		}
		else {
			System.out.println("This inversion int is not possible");
		}

		// shuffle the array and add each element to noteString 
		String [] result=shuffleArrayIndex(outputArray, rootNoteIndex);

		for (int i=0;i<result.length;i++) {
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
	 * create outputArray with notes given root and quality 
	 * 	eis_maj
		eis_min
		fes_maj
		fes_min
		bis_maj
		bis_min
		ces_maj
		ces_min
		dis_maj
		ais_maj
	 * @param root_and_quality
	 */
	public void specialChords (String root_and_quality) {
		ArrayList<String> enharmChord = new ArrayList<String>();
		enharmChord = enharmonicChords.get(root_and_quality);
		for (int i=0; i<enharmChord.size(); i++) {
			outputArray.add(enharmChord.get(i));
		}
	}

	public static void main(String[] args) throws IOException {

		Parser parser = new Parser();
		NoteProducer np = parser.returnSongHeader(); 
		ArrayList<Verse> song = parser.returnSong();

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
				String quality = verse.getChords().get(i)[1];
				String optional_inv = verse.getChords().get(i)[2];
				String duration = verse.getChords().get(i)[3];
				String notes = verse.getChords().get(i)[4];
				chordOutput+=extraLine + "< "+ (np.singleChordString(root, quality, optional_inv, notes))+ "> "+ duration + "\n   ";

				np.outputArray.clear();
			}
		}

		String header = np.createHeader(np.title, np.composer);
		String output = header + "\n" + "\\score {" + "\n " + 
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
