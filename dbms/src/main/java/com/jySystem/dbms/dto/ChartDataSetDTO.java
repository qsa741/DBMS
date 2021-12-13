package com.jySystem.dbms.dto;

import java.util.List;

public class ChartDataSetDTO {

	private String label;
	private String backgroundColor;
	private String borderColor;
	private int borderWidth;
	private List<String> data;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	ChartDataSetDTO() {
	}

	public ChartDataSetDTO(String label, String backgroundColor, String borderColor, int borderWidth) {
		super();
		this.label = label;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
	}

}
