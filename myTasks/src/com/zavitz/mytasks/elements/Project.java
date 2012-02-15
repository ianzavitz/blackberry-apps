package com.zavitz.mytasks.elements;

import net.rim.device.api.util.*;

public class Project implements Persistable {

	public static final int SORT_NAME = 0;
	public static final int SORT_DATE = 1;
	public static final int SORT_PRIORITY = 2;
	public static final int SORT_STATUS = 3;
	public static final int SORT_MANUAL = 4;
	
	private String name;
	private Project[] projects;
	private Task[] tasks;
	
	private int sortType = SORT_DATE;

	public Project() {
		name = "";
		projects = new Project[0];
		tasks = new Task[0];
	}
	
	public Project getProject(int index) {
		return projects[index];
	}
	
	public Project[] getProjects() {
		return projects;
	}
	
	public Task getTask(int index) {
		return tasks[index];
	}
	
	public Task[] getTasks() {
		return tasks;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setSortType(int type) {
		sortType = type;
	}
	
	public int getSortType() {
		return sortType;
	}
	
	public void addProject(Project project) {
		Arrays.add(projects, project);
	}

	public boolean deleteProject(Project project) {
		if(projectIndex(project) == -1)
			return false;
		Arrays.remove(projects, project);
		return true;
	}
	
	public boolean deleteTask(Task task) {
		if(taskIndex(task) == -1)
			return false;
		Arrays.remove(tasks, task);
		return true;
	}
	
	public void addTask(Task task) {
		Arrays.add(tasks, task);
	}

	public int projectIndex(Project project) {
		for (int i = 0; i < projects.length; i++)
			if (projects[i] == project)
				return i;
		return -1;
	}

	public int taskIndex(Task task) {
		for (int i = 0; i < tasks.length; i++)
			if (tasks[i] == task)
				return i;
		return -1;
	}

}