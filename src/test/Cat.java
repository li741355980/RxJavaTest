package test;

import java.util.Comparator;

public class Cat implements Comparator<Cat>{
    int cuteness;
    public Cat(int cuteness){
    	this.cuteness=cuteness;
    }
	public int compareTo(Cat another) {
        return Integer.compare(cuteness, another.cuteness);
    }
	public int compare(Cat arg0, Cat arg1) {
		// TODO Auto-generated method stub
		return Integer.compare(arg0.cuteness, arg1.cuteness);
	}
    
}