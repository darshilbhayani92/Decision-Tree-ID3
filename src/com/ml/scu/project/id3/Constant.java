package com.ml.scu.project.id3;

public class Constant {

	//file path according to the system
	public static final String path = "/Users/darshilbhayani/Documents/macWorkspace/Assignment1/Data/";
	public static final String Dataset = "Dataset";
//	 public static final String dataset1Nm = "Titanic";
	public static final String dataset1Nm = "BalancScale";

	public static final String Datasets = "_Dataset.txt";
	public static final String Feature = "_Feature.txt";

	// Dataset1 Constants
	public static final int resColumn = 0;
	//public static final int resColumn = 3;
	public static final String Class_Name = "Class_Name";

	public static final double trainingDataSetfactor = 0.80;
	public static final double testDataSetfactor = 0.20;

	// Factor for each time random value pick up
	public static final double randomTrainingDataSetfactor = 0.70;
	public static final double randomTestDataSetfactor = 0.30;
	public static final double randomTraingBuildfactor = 0.15;

	static boolean checkOverFitOrnot = true;
	
	static boolean randomForestExecute = true;
	static boolean randomForestFlag = false;
	static int randomForTrees = 10;
}
