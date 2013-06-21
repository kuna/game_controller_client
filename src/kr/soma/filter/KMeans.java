package kr.soma.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import kr.soma.events.AccelerationEvent;
import kr.soma.model.Point;

public class KMeans {

	private static final Random random = new Random();
	public final List<Point> allPoints;
	public final int k;
	private Clusters pointClusters;	//the k Clusters
	private List<List<AccelerationEvent>> sumOfAccelerations;
	private boolean isTrained = false;
	
	/**@param pointsFile : the csv file for input points
	 * @param k : number of clusters
	 */
	public KMeans(List<List<AccelerationEvent>> sumOfAccelerations, int k) {
		this.sumOfAccelerations = sumOfAccelerations;
		this.k = k;
		List<Point> points = new ArrayList<Point>();
		int i = 0, j = 0;
		for (List<AccelerationEvent> accList : sumOfAccelerations) {
			j = 0;
			for (AccelerationEvent ae : accList) {
				points.add(new Point(i, j, ae.getX(), ae.getY(), ae.getZ()));
				j++;
			}
			i++;
		}
		this.allPoints = Collections.unmodifiableList(points);
	}

	private Point getPointByLine(String line) {
		String[] xyz = line.split(",");
		return new Point(Double.parseDouble(xyz[0]),
				Double.parseDouble(xyz[1]), Double.parseDouble(xyz[2]));
	}

	/**step 1: get random seeds as initial centroids of the k clusters
	 */
	private void getInitialKCentralPoints(){
		pointClusters = new Clusters(allPoints);
		List<Point> kCentralPoints = getKCentralPoints();
		for (int i = 0; i < k; i++){
			kCentralPoints.get(i).setIndex(i);
			pointClusters.add(new Cluster(kCentralPoints.get(i)));
		}	
	}
	
	private List<Point> getKCentralPoints() {
		double maxAcceleration=0;
		double minAcceleration=0;
		
		for (List<AccelerationEvent> accList: sumOfAccelerations) {
			double maxacc = Double.MIN_VALUE;
			double minacc = Double.MAX_VALUE;
			for(int i=0; i<accList.size(); i++) {
				if(Math.abs(accList.get(i).getX()) > maxacc) {
					maxacc=Math.abs(accList.get(i).getX());
				}
				if(Math.abs(accList.get(i).getY()) > maxacc) {
					maxacc=Math.abs(accList.get(i).getY());
				}
				if(Math.abs(accList.get(i).getZ()) > maxacc) {
					maxacc=Math.abs(accList.get(i).getZ());
				}
				if(Math.abs(accList.get(i).getX()) < minacc) {
					minacc=Math.abs(accList.get(i).getX());
				}
				if(Math.abs(accList.get(i).getX()) < minacc) {
					minacc=Math.abs(accList.get(i).getX());
				}
				if(Math.abs(accList.get(i).getX()) < minacc) {
					minacc=Math.abs(accList.get(i).getX());
				}
			}
			maxAcceleration += maxacc;
			minAcceleration += minacc;
		}
		maxAcceleration = maxAcceleration / sumOfAccelerations.size();
		minAcceleration = minAcceleration / sumOfAccelerations.size();
		
		double pi = Math.PI;
		double radius = (maxAcceleration + minAcceleration) / 2;
		List<Point> kCentralPoints = new ArrayList<Point>();
		/*
		kCentralPoints.add(new Point(Direction.EAST, radius, 0.0, 0.0));
		kCentralPoints.add(new Point(Direction.FORWARD_EAST, Math.cos(pi / 4) * radius, 0.0, Math.sin(pi / 4) * radius));
		kCentralPoints.add(new Point(Direction.FORWARD, 0.0, 0.0, radius));
		kCentralPoints.add(new Point(Direction.FORWARD_WEST, Math.cos(pi * 3 / 4) * radius, 0.0, Math.sin(pi * 3 / 4) * radius));
		kCentralPoints.add(new Point(Direction.WEST, -radius, 0.0, 0.0));
		kCentralPoints.add(new Point(Direction.BACKWARD_WEST, Math.cos(pi * 5 / 4) * radius, 0.0, Math.sin(pi * 5 / 4) * radius));
		kCentralPoints.add(new Point(Direction.BACKWARD, 0.0, 0.0, -radius));
		kCentralPoints.add(new Point(Direction.BACKWARD_EAST, Math.cos(pi * 7 / 4) * radius, 0.0, Math.sin(pi * 7 / 4) * radius));
		kCentralPoints.add(new Point(Direction.NORTH, 0.0, radius, 0.0));
		kCentralPoints.add(new Point(Direction.FORWARD_NORTH, 0.0, Math.cos(pi / 4) * radius, Math.sin(pi / 4) * radius));
		kCentralPoints.add(new Point(Direction.FORWARD_SOUTH, 0.0, Math.cos(pi * 3 / 4) * radius, Math.sin(pi * 3 / 4) * radius));
		kCentralPoints.add(new Point(Direction.SOUTH, 0.0, -radius, 0.0));
		kCentralPoints.add(new Point(Direction.BACKWARD_SOUTH, 0.0, Math.cos(pi * 5 / 4) * radius, Math.sin(pi * 5 / 4) * radius));
		kCentralPoints.add(new Point(Direction.BACKWARD_NORTH, 0.0, Math.cos(pi * 7 / 4) * radius, Math.sin(pi * 7 / 4) * radius));
		*/
		kCentralPoints.add(new Point(radius, 0.0, 0.0));
		kCentralPoints.add(new Point(Math.cos(pi / 4) * radius, 0.0, Math.sin(pi / 4) * radius));
		kCentralPoints.add(new Point(0.0, 0.0, radius));
		kCentralPoints.add(new Point(Math.cos(pi * 3 / 4) * radius, 0.0, Math.sin(pi * 3 / 4) * radius));
		kCentralPoints.add(new Point(-radius, 0.0, 0.0));
		kCentralPoints.add(new Point(Math.cos(pi * 5 / 4) * radius, 0.0, Math.sin(pi * 5 / 4) * radius));
		kCentralPoints.add(new Point(0.0, 0.0, -radius));
		kCentralPoints.add(new Point(Math.cos(pi * 7 / 4) * radius, 0.0, Math.sin(pi * 7 / 4) * radius));
		kCentralPoints.add(new Point(0.0, radius, 0.0));
		kCentralPoints.add(new Point(0.0, Math.cos(pi / 4) * radius, Math.sin(pi / 4) * radius));
		kCentralPoints.add(new Point(0.0, Math.cos(pi * 3 / 4) * radius, Math.sin(pi * 3 / 4) * radius));
		kCentralPoints.add(new Point(0.0, -radius, 0.0));
		kCentralPoints.add(new Point(0.0, Math.cos(pi * 5 / 4) * radius, Math.sin(pi * 5 / 4) * radius));
		kCentralPoints.add(new Point(0.0, Math.cos(pi * 7 / 4) * radius, Math.sin(pi * 7 / 4) * radius));
		return kCentralPoints;
	}
	
	/**step 2: assign points to initial Clusters
	 */
	private void getInitialClusters(){
		pointClusters.assignPointsToClusters();
	}
	
	/** step 3: update the k Clusters until no changes in their members occur
	 */
	private void updateClustersUntilNoChange(){
		boolean isChanged = pointClusters.updateClusters();
		while (isChanged)
			isChanged = pointClusters.updateClusters();
	}
	
	/**do K-means clustering with this method
	 */
	public List<Cluster> getPointsClusters() {
		if (pointClusters == null) {
			getInitialKCentralPoints();
			getInitialClusters();
			updateClustersUntilNoChange();
		}
		return pointClusters;
	}
}
