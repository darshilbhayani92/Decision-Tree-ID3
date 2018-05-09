package com.ml.scu.project.id3;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ML_Tree {

	static ML_TreeNode node = null;
	static int DatasetVal = 1;
	static boolean childFlag = false;

	static String baseAccuracy = null;
	static String baseAccuracy1 = null;
	static String baseAccuracyTraingData = null;
	static String randomBaseAccuracy = null;
	static String randomTrainingBaseAccuracy = null;

	static ArrayList<Map<String, String>> randomAttributeValForNextSrch = new ArrayList<>();
	static ArrayList<Map<String, String>> randomFinalResWithAttrNm = new ArrayList<>();

	public static void main(String[] args) {
		ML_TreeData m1 = new ML_TreeData();

		//creating the ID3 and running the basic algorithm
		while ((ML_TreeData.totEntropyVal > 0 || (ML_TreeData.totCntOfDataread - ML_TreeData.totFileCntOfData) > 0)
				|| ML_Tree.node == null || !ML_Tree.childFlag) {
			m1.readData();
		}

		baseAccuracy = m1.testAccuracy(0);
		baseAccuracyTraingData = m1.testAccuracy(2);

		//checking for overfit or not
		if (Constant.checkOverFitOrnot)
			baseAccuracy1 = m1.testAccuracy(1);

		//Random forest implementation
		ML_TreeData mRandom = new ML_TreeData();
		if (Constant.randomForestFlag && Constant.randomForestExecute) {
			
			mRandom.getTheDataForRandomForest();
			// Random forest implementation and accuracy check
			for (int i = 0; i < Constant.randomForTrees; i++) {
				resetVar();

				while ((ML_TreeData.totEntropyVal > 0
						|| (ML_TreeData.totCntOfDataread - ML_TreeData.totFileCntOfData) > 0) || ML_Tree.node == null
						|| !ML_Tree.childFlag)
					mRandom.readData();
			}
			randomBaseAccuracy = mRandom.testAccuracy(0);
			randomTrainingBaseAccuracy = mRandom.testAccuracy(2);
		}

		System.out.println("m1.testAccuracy();..." + baseAccuracy1);
		System.out.println("m1.randomTrainingBaseAccuracy();..." + randomTrainingBaseAccuracy);
		System.out.println("Base Accuracy : " + baseAccuracy + " \nAccuracy after RandomForest implementation......"
				+ randomBaseAccuracy);

		//Show pop up msg
		showMsg();

		randomAttributeValForNextSrch.clear();
		randomFinalResWithAttrNm.clear();
	}

	//for jpanel show up final msg
	private static void showMsg() {
		StringBuilder sin = new StringBuilder();

		sin.append("\nDecision Tree Project by : Darshil Bhayani \n");
		sin.append("Machine Learning - Santa Clara University - Winter 2018 \n\n");

		if (Constant.dataset1Nm.equals("BalancScale"))
			sin.append("Classification Goal: To predit the balance scale tip in psychological Experiement \n\n");
		else
			sin.append("Classification Goal: To predit the number of people survived in Titanic Disaster \n\n");

		int patialDataPoint = ML_TreeData.totFileCntOfData;
		int totDataPoint = (int) (patialDataPoint / Constant.trainingDataSetfactor);

		sin.append("Total dataPoints :" + totDataPoint + "\n");
		sin.append("Number Of Attribute :" + ML_TreeData._attributetoInd.size() + "\n\n");

		sin.append("Training set :" + (Constant.trainingDataSetfactor) * 100 + "%\n");
		sin.append("Data Points in Training set :" + ML_TreeData.totFileCntOfData + "\n\n");

		sin.append("Test set :" + (Constant.testDataSetfactor) * 100 + "%\n");
		sin.append("Data Points in Test set :" + (totDataPoint - ML_TreeData.totFileCntOfData) + "\n\n");
		
		//sin.append("The decsion tree is combined with " + (Constant.testDataSetfactor) * 100 + "% Testing set \n");
		sin.append("Testing Accuracy rate :" + baseAccuracy + "\n");
		sin.append("Traingin Accuracy rate :" + baseAccuracyTraingData + "\n\n");
		if (Constant.randomForestFlag && Constant.randomForestExecute) {
			sin.append("Number of trees used for Random forest experienment :"+Constant.randomForTrees+ "\n");
			sin.append("Test Accuracy rate with Random Forest :" + randomBaseAccuracy + "\n");
			sin.append("Training Accuracy rate with Random Forest :" + randomTrainingBaseAccuracy + "\n");
		}

		Icon icon = new ImageIcon("C:\\Users\\LC Lab\\eclipse-workspace\\MLProj\\image\\java.png");
		javax.swing.JOptionPane.showMessageDialog(null, sin.toString(), "Java", 0, icon);
	}

	private static void resetVar() {
		ML_TreeData.totEntropyVal = 0;
		ML_TreeData.totCntOfDataread = 0;
		ML_TreeData.totFileCntOfData = 0;
		ML_Tree.node = null;
		ML_Tree.childFlag = false;

		ML_Tree.randomFinalResWithAttrNm.clear();
		ML_Tree.randomAttributeValForNextSrch.clear();

		ML_TreeData._featureSet.clear();
		ML_TreeData._attributetoInd.clear();
		ML_TreeData._IndToAttribute.clear();
		ML_TreeData.finalResWithAttrNm.clear();
		ML_TreeData.attributeValForNextSrch.clear();

	}

}