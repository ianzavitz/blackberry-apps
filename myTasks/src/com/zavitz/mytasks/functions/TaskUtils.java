package com.zavitz.mytasks.functions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import net.rim.device.api.system.Characters;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.xml.jaxp.XMLWriter;
import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;
import net.rim.device.api.xml.parsers.ParserConfigurationException;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;

import com.zavitz.mytasks.MTScreen;
import com.zavitz.mytasks.MTUiApplication;
import com.zavitz.mytasks.elements.*;

public class TaskUtils {

	public static void parseTags(Task task, String tags) {
		String[] split = split(tags, ",");
		if (split.length > 0)
			task.addTags(split);
	}

	public static void importXML(String s) throws ParserConfigurationException,
			SAXException, IOException {
		ByteArrayInputStream input = new ByteArrayInputStream(s.getBytes());

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		docBuilderFactory.setCoalescing(true);
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		docBuilder.isValidating();
		Document doc = docBuilder.parse(input);
		doc.getDocumentElement().normalize();
		NodeList list = doc.getElementsByTagName("*");

		String version = ((Element) list.item(1)).getAttribute("id");
		System.out.println(version);

		Vector projects = new Vector();

		if (version.startsWith("2")) {
			// Base projects
			NodeList nProjects = list.item(2).getChildNodes();
			for (int i = 0; i < nProjects.getLength(); i++) {
				Node node = nProjects.item(i);
				Project project = new Project();
				for (int j = 0; j < node.getChildNodes().getLength(); j++) {
					Node child = node.getChildNodes().item(j);
					if (child.getNodeName().equals("name")) {
						project.setName(Encode.decode(child.getChildNodes()
								.item(0).getNodeValue()));
					} else if (child.getNodeName().equals("type")) {
						project.setSortType(Integer.valueOf(
								Encode.decode(child.getChildNodes().item(0)
										.getNodeValue())).intValue());
					} else if (child.getNodeName().equals("projects")) {
						NodeList children = child.getChildNodes();
						for (int k = 0; k < children.getLength(); k++)
							addProject(children.item(k), project);
					} else if (child.getNodeName().equals("tasks")) {
						addTasks(child, project);
					}
				}
				projects.addElement(project);
			}
		} else {
			// import other version
		}

		MTScreen.mainProjects = new Project[projects.size()];
		projects.copyInto(MTScreen.mainProjects);
	}

	/**
	 * Feed a "project" node
	 * <project><name></name><type></type><projects></projects
	 * ><tasks></tasks></project>
	 */
	public static void addProject(Node node, Project parent) {
		Project project = new Project();
		for (int j = 0; j < node.getChildNodes().getLength()
				&& node.getNodeName().equals("project"); j++) {
			Node child = node.getChildNodes().item(j);
			if (child.getNodeName().equals("name")) {
				project.setName(child.getChildNodes().item(0).getNodeValue());
			} else if (child.getNodeName().equals("type")) {
				project.setSortType(Integer.valueOf(
						child.getChildNodes().item(0).getNodeValue())
						.intValue());
			} else if (child.getNodeName().equals("projects")) {
				NodeList children = child.getChildNodes(); // projects to add
				for (int i = 0; i < children.getLength(); i++)
					addProject(children.item(i), project);
			} else if (child.getNodeName().equals("tasks")) {
				addTasks(child, project);
			}
		}
		parent.addProject(project);
	}

	public static void addTasks(Node node, Project parent) {
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			Node tNode = node.getChildNodes().item(i);
			Task task = new Task();
			for (int j = 0; j < tNode.getChildNodes().getLength()
					&& tNode.getNodeName().equals("task"); j++) {
				Node temp = tNode.getChildNodes().item(j);
				if (temp.getNodeName().equals("name") && temp.hasChildNodes())
					task.setName(Encode.decode(temp.getChildNodes().item(0)
							.getNodeValue()));
				else if (temp.getNodeName().equals("description")
						&& temp.hasChildNodes())
					task.setDescription(Encode.decode(temp.getChildNodes()
							.item(0).getNodeValue()));
				else if (temp.getNodeName().equals("priority")
						&& temp.hasChildNodes())
					task.setPriority(Integer.valueOf(
							Encode.decode(temp.getChildNodes().item(0)
									.getNodeValue())).intValue());
				else if (temp.getNodeName().equals("color")
						&& temp.hasChildNodes())
					task.setColor(Integer.valueOf(
							Encode.decode(temp.getChildNodes().item(0)
									.getNodeValue())).intValue());
				else if (temp.getNodeName().equals("status")
						&& temp.hasChildNodes())
					task.setStatus(Integer.valueOf(Encode.decode(
							temp.getChildNodes().item(0).getNodeValue()))
							.intValue());
				else if (temp.getNodeName().equals("percent_complete")
						&& temp.hasChildNodes())
					task.setPercentComplete(Integer.valueOf(
							Encode.decode(temp.getChildNodes().item(0)
									.getNodeValue())).intValue());
				else if (temp.getNodeName().equals("date_due")
						&& temp.hasChildNodes())
					task.setDateDue(Long.parseLong(Encode.decode(temp
							.getChildNodes().item(0).getNodeValue())));
				else if (temp.getNodeName().equals("reminder")
						&& temp.hasChildNodes())
					task.setReminder(Long.parseLong(Encode.decode(temp
							.getChildNodes().item(0).getNodeValue())));
				else if (temp.getNodeName().equals("uid")
						&& temp.hasChildNodes())
					task.setUID(Encode.decode(temp.getChildNodes().item(0)
							.getNodeValue()));
				else if (temp.getNodeName().equals("tags")
						&& temp.hasChildNodes()) {
					NodeList tags = temp.getChildNodes();
					for (int k = 0; k < tags.getLength()
							&& tags.item(k).hasChildNodes(); k++)
						task.addTag(Encode.decode(tags.item(k).getChildNodes()
								.item(0).getNodeValue()));
				} else if (temp.getNodeName().equals("notes")
						&& temp.hasChildNodes()) {
					NodeList notes = temp.getChildNodes();
					for (int k = 0; k < notes.getLength()
							&& notes.item(k).hasChildNodes(); k++)
						task.addNote(Encode.decode(notes.item(k)
								.getChildNodes().item(0).getNodeValue()));
				}
			}
			parent.addTask(task);
		}
	}

	public static String getXML() {
		String temp = "<root>";
		temp += "<version id=\"" + MTUiApplication.VERSION + "\"></version>";
		temp += "<projects>";
		for (int i = 0; i < MTScreen.mainProjects.length; i++)
			temp += get(MTScreen.mainProjects[i]);
		temp += "</projects>";
		temp += "</root>";
		return temp;
	}

	private static String get(Project project) {
		String temp = "<project>";

		temp += "<name>" + Encode.encode(project.getName()) + "</name>";
		temp += "<type>" + Encode.encode(project.getSortType()) + "</type>";

		temp += "<projects>";
		for (int i = 0; i < project.getProjects().length; i++)
			temp += get(project.getProject(i));
		temp += "</projects>";

		temp += "<tasks>";
		for (int i = 0; i < project.getTasks().length; i++) {
			Task task = project.getTask(i);
			temp += "<task>";
			temp += "<name>" + Encode.encode(task.getName()) + "</name>";
			temp += "<description>" + Encode.encode(task.getDescription())
					+ "</description>";
			temp += "<priority>" + Encode.encode(task.getPriority())
					+ "</priority>";
			temp += "<color>" + Encode.encode(task.getColor()) + "</color>";
			temp += "<status>" + Encode.encode(task.getStatus()) + "</status>";
			temp += "<percent_complete>"
					+ Encode.encode(task.getPercentComplete())
					+ "</percent_complete>";
			temp += "<date_due>" + Encode.encode(task.getDateDue())
					+ "</date_due>";
			temp += "<reminder>" + Encode.encode(task.getReminder())
					+ "</reminder>";
			temp += "<tags>";
			for (int j = 0; j < task.getTags().length; j++)
				temp += "<tag>" + Encode.encode(task.getTag(j)) + "</tag>";
			temp += "</tags>";
			temp += "<notes>";
			for (int j = 0; j < task.getNotes().length; j++)
				temp += "<note>" + Encode.encode(task.getNote(j)) + "</note>";
			temp += "</notes>";
			temp += "<uid>" + Encode.encode(task.getUID()) + "</uid>";
			temp += "</task>";
		}
		temp += "</tasks>";
		temp += "</project>";
		return temp;
	}

	public static String getPath(Project project) {
		String path = project.getName();

		Vector projects = new Vector();
		add(projects, MTScreen.mainProjects);

		Vector temp = new Vector();
		add(temp, MTScreen.mainProjects);
		while (!temp.isEmpty()) {
			add(projects, ((Project) temp.elementAt(0)).getProjects());
			add(temp, ((Project) temp.elementAt(0)).getProjects());
			temp.removeElementAt(0);
		}

		Project parent = null;
		while ((parent = findParent(projects, project)) != null) {
			path = parent.getName() + " "
					+ Characters.RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
					+ " " + path;
			project = parent;
		}

		return path;
	}

	public static String getPath(Task task) {
		Vector projects = new Vector();
		add(projects, MTScreen.mainProjects);

		Vector temp = new Vector();
		add(temp, MTScreen.mainProjects);
		while (!temp.isEmpty()) {
			add(projects, ((Project) temp.elementAt(0)).getProjects());
			add(temp, ((Project) temp.elementAt(0)).getProjects());
			temp.removeElementAt(0);
		}

		return getPath(findParent(projects, task)) + " "
				+ Characters.RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK + " "
				+ task.getName();
	}

	private static Project findParent(Vector projects, Task child) {
		for (int i = 0; i < projects.size(); i++) {
			Project parent = (Project) projects.elementAt(i);
			if (Arrays.contains(parent.getTasks(), child))
				return parent;
		}

		return null;
	}

	private static Project findParent(Vector projects, Project child) {
		for (int i = 0; i < projects.size(); i++) {
			Project parent = (Project) projects.elementAt(i);
			if (Arrays.contains(parent.getProjects(), child))
				return parent;
		}

		return null;
	}

	public static void add(Vector vector, Object[] object) {
		for (int i = 0; i < object.length; i++)
			vector.addElement(object[i]);
	}

	public static Task[] findTasks(String query) {
		Task[] tasks = new Task[0];
		Project[] projects = new Project[MTScreen.mainProjects.length];
		for (int i = 0; i < projects.length; i++)
			projects[i] = MTScreen.mainProjects[i];
		while (projects.length > 0) {
			for (int i = 0; i < projects[0].getTasks().length; i++) {
				if (match(projects[0].getTask(i), query))
					Arrays.add(tasks, projects[0].getTask(i));
			}
			for (int i = 0; i < projects[0].getProjects().length; i++)
				Arrays.add(projects, projects[0].getProject(i));
			Arrays.removeAt(projects, 0);
		}
		return tasks;
	}

	private static boolean match(Task task, String query) {
		query = query.toLowerCase();
		if (task.getName().toLowerCase().indexOf(query) >= 0)
			return true;
		if (task.getDescription().toLowerCase().indexOf(query) >= 0)
			return true;
		for (int i = 0; i < task.getTags().length; i++)
			if (task.getTag(i).toLowerCase().indexOf(query) >= 0)
				return true;
		return false;
	}

	public static String getTags(Task task) {
		String response = implode(task.getTags(), ", ");
		if (response.equals(""))
			return "No tags";
		return response;
	}

	public static String implode(String[] ary, String delim) {
		String s = "";
		while (ary.length > 0) {
			s += ary[0];
			Arrays.removeAt(ary, 0);
			if (ary.length > 0)
				s += delim;
		}
		return s;
	}

	public static Task[] getTasks(long from, long to) {
		Task[] tasks = new Task[0];
		try {
			Project[] projects = new Project[MTScreen.mainProjects.length];
			for (int i = 0; i < projects.length; i++)
				projects[i] = MTScreen.mainProjects[i];
			while (projects.length > 0) {
				for (int i = 0; i < projects[0].getTasks().length; i++)
					if (CalUtils.inTime(from, projects[0].getTask(i)
							.getDateDue(), to))
						Arrays.add(tasks, projects[0].getTask(i));
				for (int i = 0; i < projects[0].getProjects().length; i++)
					Arrays.add(projects, projects[0].getProject(i));
				Arrays.removeAt(projects, 0);
			}
		} catch (Exception e) {
		}
		return tasks;
	}

	public static int getTaskCount(long from, long to) {
		int count = 0;
		Project[] projects = new Project[MTScreen.mainProjects.length];
		for (int i = 0; i < projects.length; i++)
			projects[i] = MTScreen.mainProjects[i];
		while (projects.length > 0) {
			for (int i = 0; i < projects[0].getTasks().length; i++)
				if (CalUtils.inTime(from, projects[0].getTask(i).getDateDue(),
						to))
					count++;
			for (int i = 0; i < projects[0].getProjects().length; i++)
				Arrays.add(projects, projects[0].getProject(i));
			Arrays.removeAt(projects, 0);
		}
		return count;
	}

	public static Task[] getCompletedTasks() {
		Task[] tasks = new Task[0];
		Project[] projects = new Project[MTScreen.mainProjects.length];
		for (int i = 0; i < projects.length; i++)
			projects[i] = MTScreen.mainProjects[i];
		while (projects.length > 0) {
			for (int i = 0; i < projects[0].getTasks().length; i++)
				if (projects[0].getTask(i).getStatus() == Task.COMPLETED)
					Arrays.add(tasks, projects[0].getTask(i));
			for (int i = 0; i < projects[0].getProjects().length; i++)
				Arrays.add(projects, projects[0].getProject(i));
			Arrays.removeAt(projects, 0);
		}
		return tasks;
	}

	public static int getCompletedTaskCount() {
		int count = 0;
		Project[] projects = new Project[MTScreen.mainProjects.length];
		for (int i = 0; i < projects.length; i++)
			projects[i] = MTScreen.mainProjects[i];
		while (projects.length > 0) {
			for (int i = 0; i < projects[0].getTasks().length; i++)
				if (projects[0].getTask(i).getStatus() == Task.COMPLETED)
					count++;
			for (int i = 0; i < projects[0].getProjects().length; i++)
				Arrays.add(projects, projects[0].getProject(i));
			Arrays.removeAt(projects, 0);
		}
		return count;
	}

	public static String[] split(String text, String separator) {
		Vector nodes = new Vector();
		int index = text.indexOf(separator);
		while (index >= 0) {
			nodes.addElement(text.substring(0, index).trim());
			text = text.substring(index + separator.length());
			index = text.indexOf(separator);
		}
		nodes.addElement(text);

		String[] result = new String[nodes.size()];
		nodes.copyInto(result);

		return result;
	}

}
