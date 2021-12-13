package com.jySystem.dbms.dto;

import java.util.List;
import java.util.Map;

public class TreeDTO {

	private String id;
	private String text;
	private String iconCls;
	private String state;
	private Boolean checked;
	private Map<String, Object> attributes;
	private List<TreeDTO> children;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public List<TreeDTO> getChildren() {
		return children;
	}

	public void setChildren(List<TreeDTO> children) {
		this.children = children;
	}

}
