package com.fun.quora.nearby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

public class Solution {
	public Integer topics;
	public Integer questions;
	public Integer queries;
	public List<String> lines;
	
	Solution() throws IOException{
		lines = new ArrayList<String>();
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
	
	
	class Point{
		
		Point(Double x, Double y,Integer id){
			this.x=x;
			this.y=y;
			this.id=id;
		}
		
		public Integer id;
		public double x;
		public double y;
		public double distance=Integer.MAX_VALUE;
	}
	
	//each map will store point and their distances for particular topic/question == index,  w.r.t input query
	/*
	 * Note, Topic Map stores,  Index of a Topic against it's Point
	 *       Question Map Stores, Index of a Questions against it's best matching Topic's point
	 */
	public static Map<Point,Integer> mapTopics = new HashMap<Point, Integer>();
	public static Map<Integer,Point> reverseMapTopics = new HashMap<Integer,Point>();
	public static Map<Point,Integer> mapQuestion = new HashMap<Point, Integer>();
	
	
	public static double getDistance(Point p1,Point p2){
		 return Math.sqrt(
		            (p1.x - p2.x) *  (p1.x - p2.x) + 
		            (p1.y - p2.y) *  (p1.y - p2.y)
		        );
	}
	
	
	public static void main(String[] args) throws IOException {
		Solution solution = new Solution();
		solution.detectNearestPoint();
		}


	private void detectNearestPoint() {
		// TODO Auto-generated method stub
		
//		System.out.println(lines.size()+" "+topics+" "+questions);
//		System.out.println(lines);
		//detect queries
		for (int i = topics+questions+1; i < lines.size(); i++) {
			String[] query = lines.get(i).split("\\s+"); 
			Point queryPoint = new Point(Double.parseDouble(query[2]),Double.parseDouble(query[3]),Integer.MAX_VALUE);
			queryPoint.distance=0;
			Integer topsearchCount=Integer.parseInt(query[1]);
			if(query[0].equalsIgnoreCase("t")){
				//find nearest topic
				findNearestTopics(topsearchCount,queryPoint);
			}else{
				//find nearest questions
				findNearestQuestions(topsearchCount,queryPoint);
			}
		}
	}


	private void findNearestQuestions(Integer topsearchCount, Point queryPoint) {
		// TODO Auto-generated method stub
//		System.out.println("Finding nearest questions");
		PriorityQueue<Question> queueQuestions = new PriorityQueue<Question>(topics, new Comparator<Solution.Question>(){
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
				
			}});
		
		for (int i = 0; i < questions; i++) {
			String questionLine = lines.get(topics+i+1);
			String question[] = questionLine.split("\\s+");
			Map<Integer,Double> distances = new HashMap<Integer,Double>();
			for (int j = 1; j < question.length; j++) {  //get all point distances
				 Point p = reverseMapTopics.get(Integer.parseInt(question[j]));
				 if(p!=null)
					 distances.put(j,getDistance(queryPoint, reverseMapTopics.get(Integer.parseInt(question[j]))));
				 else
					 distances.put(j, Double.MAX_VALUE);
			}
			
			Set<Entry<Integer, Double>> entries = distances.entrySet();
			SortedSet<Entry<Integer,Double>> sentries = new TreeSet<Map.Entry<Integer,Double>>(new Comparator<Entry<Integer,Double>>() {

				@Override
				public int compare(Entry<Integer, Double> o1,
						Entry<Integer, Double> o2) {
					// TODO Auto-generated method stub
					if(o1.getValue() < o2.getValue())
						return -1;
					else if(o1.getValue() > o2.getValue())
						return 1;
					else
						return 0;
				}
			});
			sentries.addAll(entries);
			//get lowest distance
			Entry<Integer, Double> entry = sentries.first();
			//update lowest distance topic == distance of the question in prio queue
			//Point bestPoint = reverseMapTopics.get(Integer.parseInt(question[entry.getKey()]));
			//bestPoint.distance=entry.getValue();
			queueQuestions.add(new Question(i, entry.getValue()));
			//mapQuestion.put(bestPoint, i);
		}
		//print best match k questions
//			System.out.println("Poping Nearest questions");
		System.out.println();
		for (int i = 0; i < topsearchCount; i++) { 
			Question question = queueQuestions.poll();
			System.out.print(question.question+" ");
		}
	}


	private void findNearestTopics(Integer topsearchCount, Point queryPoint) {
		// TODO Auto-generated method stub
		PriorityQueue<Point> queueTopics = new PriorityQueue<Solution.Point>(topics, new Comparator<Point>(){
			@Override
			public int compare(Point o1, Point o2) {
				// TODO Auto-generated method stub
				if(o1.distance < o2.distance)
					return -1;
				else if(o1.distance > o2.distance)
						return 1;
				else
					if(o1.id > o2.id)
						return -1;
					else if(o1.id < o2.id)
						return 1;
					else
						return 0;
					
			}});
		for (int i = 0; i < topics; i++) {
			String topicLine = lines.get(i+1);
			String topic[] = topicLine.split("\\s+");
			Point point = new Point(Double.parseDouble(topic[1]),Double.parseDouble(topic[2]),i);
			point.distance=getDistance(queryPoint, point);
			mapTopics.put(point,i);
			reverseMapTopics.put(i, point);
			queueTopics.add(point);
		}
		
		//done with computing distance, pop from queue
//		System.out.println("Poping Nearest Topics");
//		System.out.println();
		for (int i = 0; i < topsearchCount; i++) {
			Point point = queueTopics.poll();
			System.out.print(mapTopics.get(point)+" ");
		}
	}


	private void init() throws IOException {
		// TODO Auto-generated method stub
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		String firstLine = stdin.readLine();
		lines.add(firstLine);
		
		String line[]=lines.get(0).split("\\s+");
//		System.out.println(line.length);
		this.topics = Integer.parseInt(line[0]);
		this.questions = Integer.parseInt(line[1]);
		this.queries = Integer.parseInt(line[2]);
		
		
		for (int i = 0; i < topics+questions+queries; i++) {
			String s = stdin.readLine();
			lines.add(s);
//			System.out.println("i is "+i+"  "+lines.get(i+1));
		}
		stdin.close();
		
	}
	
}
