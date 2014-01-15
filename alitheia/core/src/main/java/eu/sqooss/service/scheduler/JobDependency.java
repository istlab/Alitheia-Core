package eu.sqooss.service.scheduler;

import java.util.Objects;

public final class JobDependency {

	private final Job from;
	private final Job to;
	
	public JobDependency(Job from, Job to) {
		this.from = from;
		this.to = to;
	}

	public Job getFrom() {
		return this.from;
	}

	public Job getTo() {
		return this.to;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.from, this.to);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof JobDependency) {
			JobDependency that = (JobDependency) other;
			return Objects.equals(this.getFrom(), that.getFrom())
					&& Objects.equals(this.getTo(), that.getTo());
		}
		return false;
	}

	@Override
	public String toString() {
		return "<JobDependency[" + String.valueOf(this.getFrom()) + ", "
				+ String.valueOf(this.getTo()) + "]>";
	}
}
