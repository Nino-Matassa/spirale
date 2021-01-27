package ie.matassa.nino.spirale;

public class RNoughtCalculation {
  private int current = 0;
  private int previous = 0;

  public RNoughtCalculation(int current, int previous) {
	this.current = current;
	this.previous = previous;
  }
  
  public Double calculate() {
	Double rNought = 0.0;
	
	if(current > 0 && previous > 0) {
	  rNought = current/(double)previous;
	} else if(current > 0 && previous == 0) {
	  current = 1;
	} else {
	  rNought = 0.0;
	} 
	
	return rNought;
  }
}
