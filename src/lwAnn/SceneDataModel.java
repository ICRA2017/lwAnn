package lwAnn;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import javax.imageio.ImageIO;
public class SceneDataModel implements java.io.Serializable{
	

	ArrayList<LabelRegister> labels;
	public String filename;
	public SceneDataModel(String filename) {
		labels = new ArrayList<LabelRegister>();
		this.filename = filename;
		
	}
	
	public void addLabel(String l) {
		labels.add(new LabelRegister(l));
	}
	
	 private void writeObject(ObjectOutputStream out) throws IOException {
	        out.defaultWriteObject();
	        for(LabelRegister s : labels) {
	        	if(s.strokes.isEmpty()) {
	        		continue;
	        	}
	        	s.dump(this.filename);
	        }
	        
	    }

	
	public void deleteLabel(int idx) {
		LabelRegister rem = labels.get(idx);
		System.out.println("DELETING: " + rem);
		if(rem != null) {
			labels.remove(rem);
		}
	}
	
	public void deleteLabel(String l, String uuid) {
		LabelRegister rem = null;
		for(LabelRegister lt : labels) {
			if(lt.uuid == uuid) {
				rem = lt;
				break;
			}
		}
		if(rem != null) {
			labels.remove(rem);
		}
	}
	

}
