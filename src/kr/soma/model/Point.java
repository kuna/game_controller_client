/**
 * 필터에서 쓰는 자료구조를 담고 있는 패키지 
 */
package kr.soma.model;

import java.util.HashMap;
import java.util.Map;

import kr.soma.filter.Direction;

public class Point {
	private int index = -1;	//denotes which Cluster it belongs to
	public double x, y, z;
	private int line;
	private int seq;
	private Direction direction;
	public static Map<Integer, Direction> dirMap = new HashMap<Integer, Direction>();
	
	static {
		dirMap.put(0, Direction.EAST);
		//dirMap.put(1, Direction.FORWARD_EAST);
		//dirMap.put(2, Direction.FORWARD);
		//dirMap.put(3, Direction.FORWARD_WEST);
		dirMap.put(4, Direction.WEST);
		//dirMap.put(5, Direction.BACKWARD_WEST);
		//dirMap.put(6, Direction.BACKWARD);
		//dirMap.put(7, Direction.BACKWARD_EAST);
		dirMap.put(8, Direction.NORTH);
		//dirMap.put(9, Direction.FORWARD_NORTH);
		//dirMap.put(10, Direction.FORWARD_SOUTH);
		dirMap.put(11, Direction.SOUTH);
		//dirMap.put(12, Direction.BACKWARD_SOUTH);
		//dirMap.put(13, Direction.BACKWARD_NORTH);
	}
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point(int line, int seq, double x, double y, double z) {
		this.line = line;
		this.seq = seq;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Double getSquareOfDistance(Point anotherPoint){
		return  (x - anotherPoint.x) * (x - anotherPoint.x)
				+ (y - anotherPoint.y) *  (y - anotherPoint.y) 
				+ (z - anotherPoint.z) *  (z - anotherPoint.z);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public String toString(){
		return "(" + x + "," + y + "," + z + ")";
	}	
	
	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

}
