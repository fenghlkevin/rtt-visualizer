package cn.com.cennavi.visualizer.common.dataloader.corresponding;

import java.io.Serializable;
import java.util.List;

public class CorrespondingInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4962484897284909712L;
	
	private Long meshNo;
	
	private Integer rticLinkKind;
	
	private Long rticLinkNo;
	
	private Integer trafficClass;
	
	private Integer productClass;
	
	private Integer length;
	
	private List<NILink> links;
	
	public static class NILink implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8886842853340452312L;

		private Integer direction;
		
		private Long linkid;
		
		private Integer length;

		public Integer getDirection() {
			return direction;
		}

		public void setDirection(Integer direction) {
			this.direction = direction;
		}

		public Long getLinkid() {
			return linkid;
		}

		public void setLinkid(Long linkid) {
			this.linkid = linkid;
		}

		public Integer getLength() {
			return length;
		}

		public void setLength(Integer length) {
			this.length = length;
		}
	}

	public Long getMeshNo() {
		return meshNo;
	}

	public void setMeshNo(Long meshNo) {
		this.meshNo = meshNo;
	}

	public Integer getRticLinkKind() {
		return rticLinkKind;
	}

	public void setRticLinkKind(Integer rticLinkKind) {
		this.rticLinkKind = rticLinkKind;
	}

	public Long getRticLinkNo() {
		return rticLinkNo;
	}

	public void setRticLinkNo(Long rticLinkNo) {
		this.rticLinkNo = rticLinkNo;
	}

	public Integer getTrafficClass() {
		return trafficClass;
	}

	public void setTrafficClass(Integer trafficClass) {
		this.trafficClass = trafficClass;
	}

	public Integer getProductClass() {
		return productClass;
	}

	public void setProductClass(Integer productClass) {
		this.productClass = productClass;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public List<NILink> getLinks() {
		return links;
	}

	public void setLinks(List<NILink> links) {
		this.links = links;
	}
}
