package de.vexo.vexoengine.queue;
import java.util.LinkedHashSet;
import java.util.Set;


public class Warteschlange {
	
	private Set<String> schlange;
	
	public Warteschlange(){
		schlange = new LinkedHashSet<String>();
	}
	
	public void addToList(String... args){
		String[] newElements = new String[args.length];
		for(int i = 0; i < newElements.length; i++){
			newElements[i] = args[i];
			schlange.add(newElements[i]);
		}
	}
	
	public String getFirstElement(){
		if(schlange.size() > 0){
			String[] temp = new String[schlange.size()];
			schlange.toArray(temp);
			return temp[0];
		} else {
			return null;
		}
	}
	
	public int getLength(){
		return schlange.size();
	}
	
	public void deleteFirstElement(){
		schlange.remove(schlange.toArray(new String[schlange.size()])[0]);
	}
	
}
