package cn.com.cennavi.visualizer.service.createfile;

public class RoadSection implements Comparable<Object>{
	
	private Integer length;
	
	private Integer los;
	
	private Integer speed;

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getLos() {
		return los;
	}

	public void setLos(Integer los) {
		this.los = los;
	}

	@Override
	public int compareTo(Object o){
		RoadSection section = (RoadSection)o;
		Integer otherLength = section.getLength();
		//return otherLength.compareTo(this.length);
		return this.length.compareTo(otherLength);
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}
}
