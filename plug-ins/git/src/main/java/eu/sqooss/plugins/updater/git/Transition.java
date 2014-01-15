package eu.sqooss.plugins.updater.git;

import java.util.Objects;

import eu.sqooss.service.db.FileState;

public final class Transition {

	private final FileState left;
	private final FileState right;

	public Transition(FileState left, FileState right) {
		this.left = left;
		this.right = right;
	}

	public FileState getLeft() {
		return this.left;
	}

	public FileState getRight() {
		return this.right;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.left, this.right);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Transition) {
			Transition that = (Transition) other;
			return Objects.equals(this.getLeft(), that.getLeft())
					&& Objects.equals(this.getRight(), that.getRight());
		}
		return false;
	}

	@Override
	public String toString() {
		return "<Transition[" + String.valueOf(this.getLeft()) + ", "
				+ String.valueOf(this.getRight()) + "]>";
	}
}
