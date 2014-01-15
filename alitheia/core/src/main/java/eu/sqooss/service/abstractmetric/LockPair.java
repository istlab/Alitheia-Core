package eu.sqooss.service.abstractmetric;

import java.util.Objects;

public final class LockPair {

	private Object left;
	private Long right;

	public LockPair(Object left, Long right) {
		this.left = left;
		this.right = right;
	}

	public Object getLeft() {
		return this.left;
	}
	
	public final void setLeft(Object left) {
		this.left = left;
	}

	public Long getRight() {
		return this.right;
	}
	
	public final void setRight(Long right) {
		this.right = right;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.left, this.right);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof LockPair) {
			LockPair that = (LockPair) other;
			return Objects.equals(this.getLeft(), that.getLeft())
					&& Objects.equals(this.getRight(), that.getRight());
		}
		return false;
	}

	@Override
	public String toString() {
		return "<LockPair[" + String.valueOf(this.getLeft()) + ", "
				+ String.valueOf(this.getRight()) + "]>";
	}
}
