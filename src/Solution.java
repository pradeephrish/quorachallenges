

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


public class Solution {
	private static final int DIM = 2;
	private Integer topics;
	private Integer questions;
	private Integer queries;
	private List<String> lines;
	private List<Point> kmatchingpoints;
	private List<Double> kmatchingscore;
	private List<Point> points; // just hold list of points
	private SolutionRect solutionRect;
	private KDNode treeHead;
	public static Map<Integer, Point> reverseMapTopics = new HashMap<Integer, Point>();

	public Solution() throws IOException {
		// TODO Auto-generated constructor stub
		this.points = new ArrayList<Point>();
		this.lines = new ArrayList<String>();
		init();
	}

	class Question{
		public Integer question;
		public Double distance;
		Question(Integer question,Double distance){
			this.question=question;
			this.distance=distance;
		}
	}
	
	
	class KDNode {

		KDNode(Point point) {
			this.point = point;
		}

		Point point;
		KDNode left;
		KDNode right;
	}

	class Point {

		Point(Double x, Double y, Integer id) {
			this.coor[0] = x;
			this.coor[1] = y;
			this.id = id;
		}

		public Integer id;
		public double coor[] = { 0.0, 0.0 };
	}

	class SolutionRect implements Cloneable {
		public double mincoor[] = { 0, 0 };
		public double maxcoor[] = { 0, 0 };

		SolutionRect(double minx, double miny, double maxx, double maxy) {
			this.mincoor[0] = minx;
			this.mincoor[1] = miny;

			this.maxcoor[0] = maxx;
			this.maxcoor[1] = maxy;
		}

		// trim's right portion ,returns left remaining
		public SolutionRect trimLeft(Integer dimension, Point point)
				throws CloneNotSupportedException { // left should deal with
													// xmin or ymin depedending
													// upon dimesion
			SolutionRect solutionRect = (SolutionRect) this.clone();

			solutionRect.maxcoor[dimension] = point.coor[dimension];

			return solutionRect;
		}

		// trim's left portion, returns Right remaining
		public SolutionRect trimRight(Integer dimension, Point point)
				throws CloneNotSupportedException { // right should deal with
													// xmax or ymax depedending
													// upon dimesion
			SolutionRect solutionRect = (SolutionRect) this.clone();

			solutionRect.mincoor[dimension] = point.coor[dimension];

			return solutionRect;
		}

	}

	class DimensionSortX implements Comparator<Point> {
		// sort on x dimensions
		@Override
		public int compare(Point o1, Point o2) {
			// TODO Auto-generated method stub

			if (o1.coor[0] < o2.coor[0])
				return -1;
			else if (o1.coor[0] > o2.coor[0])
				return 1;
			else
				return 0;
		}

	}

	class DimesionSortY implements Comparator<Point> {
		// sort on y dimensions
		@Override
		public int compare(Point o1, Point o2) {
			// TODO Auto-generated method stub
			if (o1.coor[0] < o2.coor[0])
				return -1;
			else if (o1.coor[0] > o2.coor[0])
				return 1;
			else
				return 0;
		}

	}

	private void init() throws IOException {
		// TODO Auto-generated method stub

		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));
		String firstLine = stdin.readLine();
		lines.add(firstLine);

		String line[] = lines.get(0).split("\\s+");
		// System.out.println(line.length);
		this.topics = Integer.parseInt(line[0]);
		this.questions = Integer.parseInt(line[1]);
		this.queries = Integer.parseInt(line[2]);

		for (int i = 0; i < topics + questions + queries; i++) {
			String s = stdin.readLine();
			lines.add(s);
			// System.out.println("i is "+i+"  "+lines.get(i+1));
		}
		stdin.close();
		// for all point call build tree

		double xmin = Double.POSITIVE_INFINITY, ymin = Double.POSITIVE_INFINITY;
		double xmax = Double.NEGATIVE_INFINITY, ymax = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < topics; i++) {
			String topicLine = lines.get(i + 1);
			String topic[] = topicLine.split("\\s+");
			Point point = new Point(Double.parseDouble(topic[1]),
					Double.parseDouble(topic[2]), Integer.parseInt(topic[0]));
			// doesn't consider order
			// treeHead=insert(point, treeHead, 0); //first cutting dimension X

			if (point.coor[0] < xmin)
				xmin = point.coor[0];
			if (point.coor[1] < ymin)
				ymin = point.coor[1];
			if (point.coor[0] > xmax)
				xmax = point.coor[0];
			if (point.coor[1] > ymax)
				ymax = point.coor[1];

			points.add(point);
			reverseMapTopics.put(Integer.parseInt(topic[0]), point);
		}
		// now sorting on dimesion
		treeHead = insertM(points, 0);

		//System.out.println("Done building tree " + treeHead);
		//System.out.println("Built Solutio Rect");
		solutionRect = new SolutionRect(xmin, ymin, xmax, ymax);
	}

	private void detectNearestPoint() {
		// TODO Auto-generated method stub
		for (int i = topics + questions + 1; i < lines.size(); i++) {
			String[] query = lines.get(i).split("\\s+");
			Point queryPoint = new Point(Double.parseDouble(query[2]),
					Double.parseDouble(query[3]), Integer.MAX_VALUE);
			Integer topsearchCount = Integer.parseInt(query[1]);
			if (query[0].equalsIgnoreCase("t")) {
				// find nearest topic
				try {
					kmatchingpoints = new ArrayList<Solution.Point>();
					kmatchingscore = new ArrayList<Double>();
					closeSoFar = Double.POSITIVE_INFINITY;
					kNearestNeighbours(queryPoint, treeHead, solutionRect, 0);
					//System.out.println("Printing matching points");
					// not printing in reverse order since best elements are at
					// end, don't wast time reversing just print from end
					int count = 0;
					for (int j = kmatchingpoints.size() - 1; j > -1; j--) {
//						System.out.print(kmatchingpoints.get(j).id + "  "+ kmatchingscore.get(j));
						System.out.print(kmatchingpoints.get(j).id+" ");
						++count;
						if (count == topsearchCount)
							break;
					}

				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// find nearest questions
				try {
					findNearestQuestions(topsearchCount, queryPoint);
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void findNearestQuestions(Integer topsearchCount, Point queryPoint)
			throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		Double queryOriginDistance = getDistance(new Point(0d, 0d,
				Integer.MAX_VALUE), queryPoint);

		// TODO Auto-generated method stub
		// System.out.println("Finding nearest questions");
		PriorityQueue<Solution.Question> queueQuestions = new PriorityQueue<Solution.Question>(
				topics, new Comparator<Solution.Question>() {
					@Override
					public int compare(Question q1, Question q2) {
						// TODO Auto-generated method stub
						if(q1.distance < q2.distance)
							return -1;
						else if(q1.distance > q2.distance)
								return 1;
						else 
							if(q1.question > q2.question)
								return -1;
							else if(q1.question < q2.question)
								return 1;
							else
								return 0;
					}
				});

		for (int i = 0; i < questions; i++) {
			String questionLine = lines.get(topics + i + 1);
			String question[] = questionLine.split("\\s+");
			Map<Integer, Double> distances = new HashMap<Integer, Double>();
			for (int j = 1; j < question.length; j++) { // get all point
														// distances
				Point p = reverseMapTopics.get(Integer.parseInt(question[j]));
				if (p != null)
					distances.put(
							j,
							getDistance(queryPoint, reverseMapTopics
									.get(Integer.parseInt(question[j]))));
				else
					distances.put(j, Double.MAX_VALUE);
			}

			Set<Entry<Integer, Double>> entries = distances.entrySet();
			SortedSet<Entry<Integer, Double>> sentries = new TreeSet<Map.Entry<Integer, Double>>(
					new Comparator<Entry<Integer, Double>>() {

						@Override
						public int compare(Entry<Integer, Double> o1,
								Entry<Integer, Double> o2) {
							// TODO Auto-generated method stub
							if (o1.getValue() < o2.getValue())
								return -1;
							else if (o1.getValue() > o2.getValue())
								return 1;
							else
								return 0;
						}
					});
			sentries.addAll(entries);
			// get lowest distance
			Entry<Integer, Double> entry = sentries.first();
			// update lowest distance topic == distance of the question in prio
			// queue
			// Point bestPoint =
			// reverseMapTopics.get(Integer.parseInt(question[entry.getKey()]));
			// bestPoint.distance=entry.getValue();
			queueQuestions.add(new Question(i, entry.getValue()));
		}
		System.out.println();
		for (int i = 0; i < topsearchCount; i++) {
			Question question = queueQuestions.poll();
			// System.out.println(question.distance+" "+queryOriginDistance);
			if (!(question.distance.equals(queryOriginDistance)))
				System.out.print(question.question + " ");
		}
	}

	double distance(Point point, SolutionRect boundingBox) {
		double sumSq = 0.0;
		for (int i = 0; i < DIM; i++) {
			if (point.coor[i] < boundingBox.mincoor[i])
				sumSq += (boundingBox.mincoor[i] - point.coor[i])
						* (boundingBox.mincoor[i] - point.coor[i]);
			else if (point.coor[i] > boundingBox.maxcoor[i])
				sumSq += (point.coor[i] - boundingBox.maxcoor[i])
						* (point.coor[i] - boundingBox.maxcoor[i]);
		}
		return Math.sqrt(sumSq); // don't waste time with square roots, consider
									// this later
	}

	double closeSoFar = Double.POSITIVE_INFINITY;

	public double kNearestNeighbours(Point query, KDNode node,
			SolutionRect rectangle, Integer dimesion)
			throws CloneNotSupportedException {
		if (node == null)
			return 0;
		if (distance(query, rectangle) > closeSoFar)
			return closeSoFar; // return all done

		// computer distance of query and node
		double distance = getDistance(query, node.point);
		if (distance < closeSoFar) {
			closeSoFar = distance;
			kmatchingpoints.add(node.point);
			kmatchingscore.add(closeSoFar);
		}

		// visit subtrees
		if (query.coor[dimesion] < node.point.coor[dimesion]) {
			kNearestNeighbours(query, node.left,
					rectangle.trimLeft(dimesion, node.point), dimesion + 1
							% DIM);
			kNearestNeighbours(query, node.right,
					rectangle.trimRight(dimesion, node.point), dimesion + 1
							% DIM);
		} else {
			kNearestNeighbours(query, node.right,
					rectangle.trimRight(dimesion, node.point), dimesion + 1
							% DIM);
			kNearestNeighbours(query, node.left,
					rectangle.trimLeft(dimesion, node.point), dimesion + 1
							% DIM);
		}
		return closeSoFar;

	}

	public static double getDistance(Point p1, Point p2) {
		return Math.sqrt((p1.coor[0] - p2.coor[0]) * (p1.coor[0] - p2.coor[0])
				+ (p1.coor[1] - p2.coor[1]) * (p1.coor[1] - p2.coor[1]));
	}

	// direct approach not finding median - because each time sorting will take
	// O(n(logn)) time
	private KDNode insert(Point point, KDNode t, Integer dimension) {
		// TODO Auto-generated method stub
		if (t == null)
			t = new KDNode(point);
		else if (point.coor[dimension] < t.point.coor[dimension])
			t.left = insert(point, t.left, dimension + 1 % DIM);
		else
			t.right = insert(point, t.right, dimension + 1 % DIM);
		return t;
	}

	// direct approach not finding median - because each time sorting O(n(logn))
	// but n=n/2
	private KDNode insertM(List<Point> points, Integer dimension) {
		// TODO Auto-generated method stub

		if (points.size() == 0)
			return null;

		if (dimension == 0)
			Collections.sort(points, new DimensionSortX());
		else
			Collections.sort(points, new DimesionSortY());

		// select median
		int median = points.size() / 2;

		KDNode node = new KDNode(points.get(median));

		node.left = insertM(points.subList(0, median), dimension + 1 % DIM);
		node.right = insertM(points.subList(median + 1, points.size()),
				dimension + 1 % DIM);

		return node;
	}

	public static void main(String[] args) throws IOException {
		Solution solution = new Solution();
		solution.detectNearestPoint();
	}

}
