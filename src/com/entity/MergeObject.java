package com.entity;

import java.util.List;

public class MergeObject {
	private String name;
	private List<ChipLabel> list;
	
	public MergeObject(String name,List<ChipLabel> list) {
		this.setName(name);
		this.setList(list);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ChipLabel> getList() {
		return list;
	}
	public void setList(List<ChipLabel> list) {
		this.list = list;
	}
}
