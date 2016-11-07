import java.util.ArrayList;


//import java.util.HashMap;

public class IntermediateRepresentation {
	
	/**
	HashMap<String, ArrayList<String>> sectionAndChords = new HashMap<String, ArrayList<String>> ();
	
	 class declaration (String sectionName, ArrayList<String> chords) {
		
	}
	 
	 class block (ArrayList<String> chords) {
			
		}
*/
}
	




/**
// A list of of the "Open" directions in Surroundings
case class Free (freeList: List[MoveDirection]) extends ExternalDSL{ 
  def toFreeArray = {
    freeList.toArray
  } 
}

// A list of the "Blocked" directions in Surroundings
case class Occupied (occList: List[MoveDirection]) extends ExternalDSL{ 
  def toOccArray = {
    occList.toArray
  } 
}

// Representation of all of the elements needed to formulate a Rule
case class Condition (currState: Int, free: Free, occ: Occupied, dir: MoveDirection, nextState: Int) 
  extends ExternalDSL {
  
  // Create Surroundings object
  def setSurroundings (freeArray:Array[MoveDirection], occArray:Array[MoveDirection]) : Surroundings = {     
    // Define surroundings
     var surr_array = new Array[RelativeDescription](4)
     
    // Initialize all indices to Anything
     var x = 0
     for (x<- 0 to (surr_array.size - 1)) {
       surr_array.update(x, Anything)
     }
     
    // Set N, E, W, or S to Open
     if (freeArray.contains(North)) {
       surr_array.update(0, Open)
     }
     if (freeArray.contains(East)) {
       surr_array.update(1, Open)
     }
     if (freeArray.contains(West)) {
       surr_array.update(2, Open)
     }
     if (freeArray.contains(South)) {
       surr_array.update(3, Open)
     }
     
     // Set N, E, W, or S to Blocked
     if (occArray.contains(North)) {
       surr_array.update(0, Blocked)
     }
     if (occArray.contains(East)) {
       surr_array.update(1, Blocked)
     }
     if (occArray.contains(West)) {
       surr_array.update(2, Blocked)
     }
     if (occArray.contains(South)) {
       surr_array.update(3, Blocked)
     }
     
     // Return Surroundings object
     new Surroundings(surr_array(0), surr_array(1), surr_array(2), surr_array(3))   
  }
  
  // Create a Rule object
  def createRule = {
    Rule(
      State(currState.toString), 
      setSurroundings(free.toFreeArray, occ.toOccArray), 
      dir,
      State((currState+nextState).toString())
    )
  }
}



 */