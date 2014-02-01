package hr.foi.air.kriptocavrljanje.core;

public class Comment {

	private String comment;
	private boolean side;
	
	public Comment(String comment, boolean side) {
		this.comment = comment;
		this.side = side;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isSide() {
		return side;
	}

	public void setSide(boolean side) {
		this.side = side;
	}
}
