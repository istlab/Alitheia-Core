package eu.sqooss.plugins.devmatcher;

import java.util.Objects;

public final class Match {

	private final Long left;
	private final Long right;

	public Match(Long left, Long right) {
		this.left = left;
		this.right = right;
	}

	public Long getLeft() {
		return this.left;
	}

	public Long getRight() {
		return this.right;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.left, this.right);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Match) {
			Match that = (Match) other;
			return Objects.equals(this.getLeft(), that.getLeft())
					&& Objects.equals(this.getRight(), that.getRight());
		}
		return false;
	}

	@Override
	public String toString() {
		return "<Match[" + String.valueOf(this.getLeft()) + ", "
				+ String.valueOf(this.getRight()) + "]>";
	}
}
