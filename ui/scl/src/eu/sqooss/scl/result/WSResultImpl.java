package eu.sqooss.scl.result;

import java.util.ArrayList;
import java.util.Iterator;

public class WSResultImpl extends WSResult {

	String data = null;
	
	public WSResultImpl(String data){
		this.data=data;
	}
	
	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<ArrayList<WSResultEntry>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<WSResultEntry> next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
