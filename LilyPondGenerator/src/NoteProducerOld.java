import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class NoteProducerOld {


	private static String clef;
	private static String songKey;
	private static String songQuality;
	private static int tS_top;
	private static int tS_bot;

	public ArrayList<Integer> intervalArray = new ArrayList<Integer> ();

	public ArrayList<String> outputArray= new ArrayList<String> ();

	// maybe instead of declaring it like this you have to set it??? 
	public NoteProducerOld(String clef, String songKey, String songQuality, int tS_top, int tS_bot) {
		this.clef = clef;
		this.songKey = songKey;
		this.songQuality = songQuality;
		this.tS_top = tS_top;
		this.tS_bot = tS_bot;
	}

	public void setClef1(String clef) {
		this.clef = clef;
	}

	public void setSongKey(String songKey) {
		this.songKey = songKey;
	}

	public void setSongQuality(String songQuality) {
		this.songQuality = songQuality;
	}

	public void setTSTop (int tS_top) {
		this.tS_top = tS_top;
	}

	public void setTSBot(int tS_bot) {
		this.tS_bot = tS_bot;
	}

	public ArrayList<String> sharps = new ArrayList<String> ();
	public ArrayList<String> flats = new ArrayList<String> ();
	public ArrayList<Integer> majorTriadIntervals = new ArrayList<Integer>();
	public ArrayList<Integer> minorTriadIntervals = new ArrayList<Integer>();
	public ArrayList<Integer> dom7Intervals = new ArrayList<Integer>();
	public ArrayList<Integer>maj7Intervals = new ArrayList<Integer>();
	public ArrayList<Integer> min7Intervals = new ArrayList<Integer>();
	public ArrayList<Integer>dim7Intervals = new ArrayList<Integer>();
	public char [] adjNotes = new char [7];

	HashMap<String, String[]> dimMap = new HashMap<String, String[]>();

	/**
	 * Generate notes for every possible diminished triad 
	 * @return
	 */
	public HashMap<String, String[]> createDim() {

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

		dimMap.put("c", c_dim);
		dimMap.put("c sharp", cs_dim);
		dimMap.put("d flat", df_dim);
		dimMap.put("d", d_dim);
		dimMap.put("d sharp", ds_dim);
		dimMap.put("e flat", ef_dim);
		dimMap.put("e", e_dim);
		dimMap.put("f", f_dim);
		dimMap.put("f sharp", fs_dim);
		dimMap.put("g flat", gf_dim);
		dimMap.put("g", g_dim);
		dimMap.put("g sharp", gs_dim);
		dimMap.put("a flat", af_dim);
		dimMap.put("a", a_dim);
		dimMap.put("a sharp", as_dim);
		dimMap.put("b flat", bf_dim);
		dimMap.put("b", b_dim);

		return dimMap;
	}

	/**
	 * Arrays of notes, in increasing half steps
	 * @return
	 */
	public void assignSharpsFlats() {

		majorTriadIntervals.add(0);
		majorTriadIntervals.add(4);
		majorTriadIntervals.add(7);

		minorTriadIntervals.add(0);
		minorTriadIntervals.add(3);
		minorTriadIntervals.add(7);

		dom7Intervals.add(0);
		dom7Intervals.add(4);
		dom7Intervals.add(7);
		dom7Intervals.add(10);

		maj7Intervals.add(0);
		maj7Intervals.add(4);
		maj7Intervals.add(7);
		maj7Intervals.add(11);

		min7Intervals.add(0);
		min7Intervals.add(3);
		min7Intervals.add(7);
		min7Intervals.add(10);

		dim7Intervals.add(0);
		dim7Intervals.add(3);
		dim7Intervals.add(6);
		dim7Intervals.add(9);


		adjNotes[0]='a';
		adjNotes[1]='b';
		adjNotes[2]='c';
		adjNotes[3]='d';
		adjNotes[4]='e';
		adjNotes[5]='f';
		adjNotes[6]='g';

		sharps.add("a");
		sharps.add("a sharp");
		sharps.add("b");
		sharps.add("c");
		sharps.add("c sharp");
		sharps.add("d");
		sharps.add("d sharp");
		sharps.add("e");
		sharps.add("e sharp");
		sharps.add("f sharp");
		sharps.add("g");
		sharps.add("g sharp");

		flats.add("a");
		flats.add("b flat");
		flats.add("c flat");
		flats.add("c");
		flats.add("d flat");
		flats.add("d");
		flats.add("e flat");
		flats.add("f flat");
		flats.add("f");
		flats.add("g flat");
		flats.add("g");
		flats.add("a flat");
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
			if(array.get(i)==s)  {
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

		// get the name of each of the 3 notes 
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
		if (quality == "major"){
			intervalArray = majorTriadIntervals;
		}
		else if (quality == "minor"){
			intervalArray = minorTriadIntervals;
		}
		else if (quality.trim() == "dom7"){
			intervalArray = dom7Intervals;
		}
		else if (quality == "maj7"){
			intervalArray = maj7Intervals;
		}
		else if (quality == "min7"){
			intervalArray = min7Intervals;
		}
		else if (quality == "dim7"){
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
	public String singleChordString (String root, String quality, String inv) {

		String noteString="";
		ArrayList<String> qualityArrayUsed= new ArrayList<String> ();

		// default assign to sharps (all roots except for flat roots)
		int rootIndex = returnIndex(sharps, root.trim());
		qualityArrayUsed=sharps;

		// if root not in sharps, go to flats
		if (rootIndex==-1) {
			rootIndex = returnIndex(flats, root.trim());
			qualityArrayUsed=flats;
		}

		intArray(quality.trim());

		// diminished chords 
		if (quality=="diminished") {
			for (int i=0; i<dimMap.get(root).length; i++ ){
				outputArray.add(dimMap.get(root)[i]);
			}
		}

		// default use sharps array; if adjacent, use flats array
		else if (intervalArray!=null) {
			for (int i=0;i<intervalArray.size();i++) {
				outputArray.add(qualityArrayUsed.get((rootIndex+ intervalArray.get(i)) % qualityArrayUsed.size()));
			}
			if (adjacentNoteNames()) {
				if (qualityArrayUsed==sharps) {
					qualityArrayUsed = flats;
				}
				else {
					qualityArrayUsed = sharps;
				}
				for (int i=0;i<intervalArray.size();i++) {
					outputArray.set(i+1,qualityArrayUsed.get((rootIndex+ intervalArray.get(i)) % qualityArrayUsed.size()));
				}
			}
		}

		// if inverse not specified, don't shuffle order 
		int rootNoteIndex = 0;
		if (inv!=null) {

			// if inversion specified by a root note name 
			if (inv.matches("[A-Za-z]")) {
				if (intervalArray!=null) {
					rootNoteIndex = returnIndex(outputArray, inv);
				}
				if (rootNoteIndex==-1){
					throw new ArrayIndexOutOfBoundsException();
				}
			}
			else {
				// if inversion is represented by a number
				// only let diminished do a 3rd inversion
				if (inv=="1"||inv=="2"||inv=="3") {
					rootNoteIndex = Integer.parseInt(inv);
				}
			}
		}

		// shuffle the array and add each element to noteString 
		String [] result = shuffleArrayIndex(outputArray, rootNoteIndex);

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
		//outputString= "<" + noteString.trim() +">" + dur;
		return noteString;
	}

	/**
	 * Log the message in a txt file
	 * @author sophiasysun
	 *
	 */
	public static class Logger {
		public static void log(String message) throws IOException { 
			PrintWriter out = new PrintWriter(new FileWriter("sampleOutput.txt", true), true);
			out.write(message);
			out.close();
		}
	}

	public static void main(String[] args) throws IOException {


		ArrayList<ArrayList<String[]>> song = new ArrayList<ArrayList<String[]>>();

		HashMap<ArrayList<String[]>, String> sectionNames = new HashMap<ArrayList<String[]>, String> ();

		NoteProducerOld np = new NoteProducerOld ("treble", "c", "major" , 4 , 4);

		np.assignSharpsFlats();
		np.createDim();

		// each chord has 5 notes, verse and songs should be araylist bc dynamically changing size
		String [] verse1_1 = new String [4];
		String [] verse1_2 = new String [4];
		String [] verse1_3 = new String [4];

		String [] verse2_1 = new String [4];
		String [] verse2_2 = new String [4];
		String [] verse2_3 = new String [4];
		String [] verse2_4 = new String [4];

		String [] chorus_1 = new String [4];
		String [] chorus_2 = new String [4];

		// I want the name of this arrayList to reflect the different states, for ex, verse1, chorus, bridge, etc
		ArrayList<String[]> verse1 = new ArrayList<String[]> ();
		ArrayList<String[]> chorus = new ArrayList<String[]> ();
		ArrayList<String[]> verse2 = new ArrayList<String[]> ();

		// verse 1 

		verse1_1[0]= "c";
		verse1_1[1]= "maj7";
		verse1_1[2]= "e"; 
		verse1_1[3]= "2";

		verse1_2[0]= "c";
		verse1_2[1]= "diminished ";
		verse1_2[2]= "2";
		verse1_2[3]= "8";

		verse1_3[0]= "e";
		verse1_3[1]= "maj7";
		verse1_3[2]= null;
		verse1_3[3]= "8";

		verse1.add(verse1_1);
		verse1.add(verse1_2);
		verse1.add(verse1_3);

		// verse 2
		verse2_1[0]= "d";
		verse2_1[1]= "maj7";
		verse2_1[2]= null;
		verse2_1[3]= "4";

		verse2_2[0]= "d";
		verse2_2[1]= "diminished ";
		verse2_2[2]= null; 
		verse2_2[3]= "8";

		verse2_3[0]= "c";
		verse2_3[1]= "maj7";
		verse2_3[2]= null;
		verse2_3[3]= "2";

		verse2_4[0]= "f";
		verse2_4[1]= "major";
		verse2_4[2]= null;
		verse2_4[3]= "8";

		verse2.add(verse2_1);
		verse2.add(verse2_2);
		verse2.add(verse2_3);
		verse2.add(verse2_4);

		// chorus

		chorus_1[0]= "d";
		chorus_1[1]= "major";
		chorus_1[2]= "f sharp";
		chorus_1[3]= "4";

		chorus_2[0]= "d";
		chorus_2[1]= "diminished ";
		chorus_2[2]= "2";
		chorus_2[3]= "8";

		chorus.add(chorus_1);
		verse2.add(chorus_2);

		// general

		String chordOutput =""; 

		// add verses to song 

		song.add(verse1);
		song.add(verse2);
		song.add(verse1);
		song.add(chorus);

		// add verse arrays and verse names to hashmap

		sectionNames.put(verse1, "Verse 1");
		sectionNames.put(verse2, "Verse 2");
		sectionNames.put(chorus, "Chorus");

		String extraLine = "";

		// for every section of the song
		for (int sect = 0; sect<song.size(); sect++) {

			// print out each individual chord in the section 
			for (int i=0; i<song.get(sect).size(); i++) {
				if (i==0) {
					extraLine = "\n   " + "% "+ sectionNames.get(song.get(sect)) + "\n   ";
				}
				else {
					extraLine="";
				} 
				chordOutput+=extraLine + "< "+(np.singleChordString(song.get(sect).get(i)[0], song.get(sect).get(i)[1], song.get(sect).get(i)[2]))+ "> "+  song.get(sect).get(i)[3] + "\n   ";
				np.outputArray.clear();
			}
		}

		String output = "\\score {" + "\n " + 
				"\\relative c{" +  "\n "  +  
				" \\clef " + clef + "\n " + 
				" \\time " + tS_top + "/" + tS_bot + 
				"{ " + "\n " +
				"  \\key " + songKey + 
				"  \\" + songQuality + 
				"{" + "  \n   "+ chordOutput +"}" + "\n" + 
				"  }" +"\n" + " }" + "\n" + "}" + "\n";

		Logger.log(output);

	}
}
