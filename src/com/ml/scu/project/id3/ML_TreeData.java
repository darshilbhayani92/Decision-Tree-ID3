package com.ml.scu.project.id3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ML_TreeData {

	static Map<String, List<String>> _featureSet = new TreeMap<>();
	static Map<String, Integer> _attributetoInd = new HashMap<>();
	static Map<Integer, String> _IndToAttribute = new HashMap<>();

	static Map<String, String> finalResWithAttrNm = new ConcurrentHashMap<>();
	static Map<String, String> attributeValForNextSrch = new ConcurrentHashMap<>();

	static int totCntOfDataread = 0, totFileCntOfData = 0;
	static double totEntropyVal = 0;
	
	boolean randomForestData = false;

	static List<String[]> dataSetValues = new ArrayList<>();
	static List<String[]> dataSetValuesTree = new ArrayList<>();

	public void readData() {

		if (ML_Tree.node == null) {
			// if the root is not defined
			this.loadFeatureList(); // One time load the features of the Data
			// System.out.println("featureSet......."+featureSet);
			//System.out.println("_attributetoInd..." + _attributetoInd);
			//System.out.println("_IndToAttribute..." + _IndToAttribute);

			this.createRootOfTree();

		} else {
			// if the root is already defined and need to add child nodes

			ML_Tree.childFlag = true;
			System.out.println("*********************************************************");

			totCntOfDataread = 0;
			totEntropyVal = 0;

			// Creating the local copy because ML_TreeData.attributeValForNextSrch needs to
			// be updated during operations
			Map<String, String> localAttributeValForNextSrch = new HashMap<>();
			localAttributeValForNextSrch.putAll(ML_TreeData.attributeValForNextSrch);

			Iterator<String> nodeValues = localAttributeValForNextSrch.keySet().iterator();

			// Load the tree and find the node for each unique value of an root attribute or
			// parent attribute

			while (nodeValues.hasNext()) {
				String nodeVal = nodeValues.next();
				this.loadTreeChildRootData(nodeVal);
			}

			//System.out.println("totEntropyVal......" + totEntropyVal);
			//System.out.println("cnt...." + (ML_TreeData.totCntOfDataread - ML_TreeData.totFileCntOfData));

		}
	}

	// Load the tree and find the node for each unique value of an root attribute or
	// parent attribute
	private void loadTreeChildRootData(String nodeValues) {

		// System.out.println("nodeValues for Tree...." + nodeValues);

		Map<Integer, String> hm = new HashMap<>();
		boolean furtherOpr = true;

		if (_featureSet.get(Constant.Class_Name).contains(ML_TreeData.attributeValForNextSrch.get(nodeValues)))
			furtherOpr = false;

		// extract each of the value with - and get the relevant dataset
		if (nodeValues.contains("-")) {
			String[] rootAttrValues = nodeValues.split("-");
			String[] rootVal = ML_TreeData.attributeValForNextSrch.get(nodeValues).split("-");

			for (int i = 0; i < rootVal.length; i++) {

				if (furtherOpr) {
					if (Constant.resColumn != 0)
						hm.put(_attributetoInd.get(rootVal[i]) - 1, rootAttrValues[i]);
					else
						hm.put(_attributetoInd.get(rootVal[i]), rootAttrValues[i]);
				}
			}
		} else {

			// System.out.println(ML_TreeData.attributeValForNextSrch.get(nodeValues));

			if (furtherOpr) {
				if (Constant.resColumn != 0)
					hm.put(_attributetoInd.get(ML_TreeData.attributeValForNextSrch.get(nodeValues)) - 1, nodeValues);
				else
					hm.put(_attributetoInd.get(ML_TreeData.attributeValForNextSrch.get(nodeValues)), nodeValues);
			}
		}

		if (furtherOpr)
			nodeCreationOpr(hm, nodeValues);

	}

	private void nodeCreationOpr(Map<Integer, String> hm, String nodeValues) {
		// Local variables Initialization
		List<String[]> dataSetValuesLocal = new ArrayList<>();
		ML_TreeData_operations mlopr = new ML_TreeData_operations();
		ML_TreeBuildingOpr mlTreeOpr = new ML_TreeBuildingOpr();

		// Read the data and store to the main datasetValues according the send parent
		// node values
		for (int j = 0; j < dataSetValues.size(); j++) {
			String rootDataCheck[] = dataSetValues.get(j);

			boolean flag = true;

			Iterator<Integer> itr = hm.keySet().iterator();
			while (itr.hasNext()) {
				int indLocal = itr.next();

				if (!rootDataCheck[indLocal].equals(hm.get(indLocal)))
					flag = false;
			}

			if (flag)
				dataSetValuesLocal.add(dataSetValues.get(j));
		}

		List<String[]> dataSetValues = new ArrayList<>();

		Random random = new Random();

		double ratio = 0;

		if (!Constant.randomForestFlag)
			ratio = Constant.trainingDataSetfactor;
		else
			ratio = Constant.randomTrainingDataSetfactor;

		for (int i = 0; i < dataSetValuesLocal.size() * ratio; i++) {
			int cnt = random.nextInt(dataSetValuesLocal.size() - 0) + 0;
			System.out.print(cnt + " ");
			dataSetValues.add(dataSetValuesLocal.get(cnt));
		}

		System.out.println("dataSetValues...." + dataSetValuesLocal.size());

		totCntOfDataread += dataSetValues.size();

		if (dataSetValues.size() > 0) {
			Double totEntropy = mlopr.calulateEntropy(mlopr.findTotEntropyCnt(dataSetValues));
			totEntropyVal += totEntropy;

			System.out.println("-------------totEntropy-------------" + totEntropy);

			Map<String, Double> rootInformationGain = mlopr.calulateInformationGain(totEntropy, dataSetValues);

			System.out.println("----------****---------------- Total Information Gain :" + rootInformationGain);

			mlTreeOpr.settingUpNode(rootInformationGain, ML_Tree.node, ML_Tree.node, nodeValues,
					dataSetValues.get(0)[Constant.resColumn]);
		}
	}
	
	public void getTheDataForRandomForest() {
		StringBuffer filename = new StringBuffer();
		BufferedReader br = null;
		filename.append(Constant.path + Constant.Dataset);
		filename.append(ML_Tree.DatasetVal).append("/").append(Constant.dataset1Nm).append(Constant.Datasets);
		
		String currLine;
		List<String[]> dataSetValuesLocal = new ArrayList<>();
		randomForestData = true;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename.toString())));

			// Read the data and store to the main datasetValues
			while ((currLine = br.readLine()) != null) {
				if (!currLine.equals("") || !currLine.equals(" "))
					// System.out.println("currLine....."+currLine);
					dataSetValuesLocal.add(currLine.trim().split(","));
			}
			
			double ratio = Constant.randomTrainingDataSetfactor;

			Random random = new Random();
			for (int i = 0; i < dataSetValuesLocal.size() * ratio; i++) {
				int cnt = random.nextInt(dataSetValuesLocal.size() - 0) + 0;
				System.out.print(cnt + " ");
				dataSetValuesTree.add(dataSetValuesLocal.get(cnt));
			}
			
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private void createRootOfTree() {
		// Local variables Initialization
		StringBuffer filename = new StringBuffer();
		BufferedReader br = null;
		String currLine;
		List<String[]> dataSetValuesLocal = new ArrayList<>();

		ML_TreeData_operations mlopr = new ML_TreeData_operations();
		ML_TreeBuildingOpr mlTreeOpr = new ML_TreeBuildingOpr();

		// getting the path value for Feature file
		filename.append(Constant.path + Constant.Dataset);
		filename.append(ML_Tree.DatasetVal).append("/").append(Constant.dataset1Nm).append(Constant.Datasets);
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename.toString())));

			// Read the data and store to the main datasetValues
			while ((currLine = br.readLine()) != null) {
				if (!currLine.equals("") || !currLine.equals(" "))
					// System.out.println("currLine....."+currLine);
					dataSetValuesLocal.add(currLine.trim().split(","));
			}
			
			if(randomForestData) {
				dataSetValuesLocal.clear();
				dataSetValuesLocal.addAll(dataSetValuesTree);
			}

			double ratio = 0;

			if (!Constant.randomForestFlag)
				ratio = Constant.trainingDataSetfactor;
			else
				ratio = Constant.randomTraingBuildfactor;

			Random random = new Random();
			for (int i = 0; i < dataSetValuesLocal.size() * ratio; i++) {
				int cnt = random.nextInt(dataSetValuesLocal.size() - 0) + 0;
				System.out.print(cnt + " ");
				dataSetValues.add(dataSetValuesLocal.get(cnt));
			}
			
			totFileCntOfData += dataSetValues.size();

			// System.out.println("dataSetValues...." + dataSetValues);
			// Printing the values of read data
			/*
			 * for (String[] s1 : dataSetValues) System.out.println(Arrays.toString(s1));
			 */

			Double totEntropy = mlopr.calulateEntropy(mlopr.findTotEntropyCnt(dataSetValues));
			System.out.println("-------------------------- totEntropy : " + totEntropy);

			Map<String, Double> rootInformationGain = mlopr.calulateInformationGain(totEntropy, dataSetValues);
			System.out.println("-------------------------- Total Information Gain :" + rootInformationGain);

			mlTreeOpr.settingUpNode(rootInformationGain, ML_Tree.node, null, null, null);

		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private void loadFeatureList() {

		// Local variables Initialization
		StringBuffer filename = new StringBuffer();
		BufferedReader br1 = null;
		String currLine;

		// getting the path value for Feature file
		filename.append(Constant.path + Constant.Dataset);
		filename.append(ML_Tree.DatasetVal).append("/").append(Constant.dataset1Nm).append(Constant.Feature);

		try {
			br1 = new BufferedReader(new InputStreamReader(new FileInputStream(filename.toString())));

			// Read the data and store to the main feature Map
			while ((currLine = br1.readLine()) != null) {
				if (!currLine.equals("") || !currLine.equals(" ")) {

					// Reading the data from the file and parsing it
					String tmp[] = currLine.split(":");
					// System.out.println("in here:"+tmp[0]);
					String local[] = tmp[1].trim().split(",");
					List<String> lstlocal = new ArrayList<>();
					for (String s1 : local) {
						lstlocal.add(s1.trim());
					}

					if (!tmp[0].equals(Constant.Class_Name)) {
						_attributetoInd.put(tmp[0], _attributetoInd.size() + 1); // In future to map the data with the
																					// column attirbute
						_IndToAttribute.put(_IndToAttribute.size() + 1, tmp[0]);
					}
					_featureSet.put(tmp[0], lstlocal);

				}
			}

		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		} finally {
			if (br1 != null)
				try {
					br1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public String testAccuracy(int i) {
		ML_TreeBuildingOpr mlTreeOpr = new ML_TreeBuildingOpr();
		return mlTreeOpr.testAccuracy(i);
	}
}
