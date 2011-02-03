package eu.sqooss.plugins.updater.git;

import java.util.ArrayList;
import java.util.List;

public class BranchGraph {

	List<BranchGraph> children;
	BranchGraph parent;
	int name;
	
	BranchGraph() {
		children = new ArrayList<BranchGraph>();
		this.name = -1;
	}
	
	void addChild(BranchGraph child) {
		child.parent = this;
		children.add(child);
		child.name = children.lastIndexOf(child);
	}
	
	void setName(int n) {
		this.name = n;
	}
	
	BranchGraph find (String s) {
		int idx = Integer.parseInt(s.split("-")[0]);
		return children.get(idx).find (s.substring(s.indexOf('-') == 0?0:s.indexOf('-') + 1));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		BranchGraph test = (BranchGraph) obj;
		return  (name == test.name) &&
				(parent != null && parent.equals(test.parent)) &&
				(children != null && children.equals(test.children));
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + children.hashCode();
		hash = 31 * hash + name;
		hash = 31 * hash + (null == parent ? 0 : parent.hashCode());
		return hash;
	}
	
	@Override
	public String toString() {
		if (parent == null)
			return String.valueOf(name);
		
		return parent.toString() + "-" + String.valueOf(name);
	}
}
