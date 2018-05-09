package com.ml.scu.project.id3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ML_TreeBuildingOpr {

	int yCnt = 0;
	int nCnt = 0;

	// Extracting the maximum Information gain and setting to the node value
	public void settingUpNode(Map<String, Double> rootInformationGain, ML_TreeNode node, ML_TreeNode parent,
			String nodeValues, String leafVal) {

		ML_TreeNode local;

		Iterator<String> itr = rootInformationGain.keySet().iterator();
		Double maxVal = Double.MIN_VALUE;

		String maxValRoot = new String();
		while (itr.hasNext()) {
			String tmp = itr.next();
			if (maxVal < rootInformationGain.get(tmp)) {
				maxValRoot = tmp;
				maxVal = rootInformationGain.get(tmp);
			}
		}

		// attribute unique values
		List<String> child = ML_TreeData._featureSet.get(maxValRoot);

		// Getting the instance of created Node
		local = new ML_TreeNode(maxValRoot, parent, child, nodeValues, leafVal);

		// setting to the global variable of root
		ML_Tree.node = local;
	}

	//Testing data accuracy
	public String testAccuracy(int checkForOverFitting) {

		System.out.println();
		System.out
				.println("----------------------------------TESTING DATA ACCURACY------------------------------------");

		// Local variables Initialization
		StringBuffer filename = new StringBuffer();
		BufferedReader br = null;
		String currLine;
		List<String[]> dataSetValuesforTestFull = new ArrayList<>();

		filename.append(Constant.path + Constant.Dataset);
		filename.append(ML_Tree.DatasetVal).append("/").append(Constant.dataset1Nm).append(Constant.Datasets);

		// System.out.println("filename..."+filename);

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename.toString())));

			// Fetch the test data and store in dataSetValuesforTest
			while ((currLine = br.readLine()) != null) {
				if (!currLine.equals("") || !currLine.equals(" ")) {
					dataSetValuesforTestFull.add(currLine.split(","));
				}
			}

			Random random = new Random();
			List<String[]> dataSetValuesforTest = new ArrayList<>();
			List<String[]> copyDataSetValuesforTest = new ArrayList<>();

			//check overfitting or not
			if (checkForOverFitting == 0) {
				double ratio = 0;
				if (!Constant.randomForestFlag)
					ratio = Constant.testDataSetfactor;
				else
					ratio = Constant.randomTestDataSetfactor;

				for (int i = 0; i < dataSetValuesforTestFull.size() * ratio; i++) {
					int cnt = random.nextInt(dataSetValuesforTestFull.size() - 0) + 0;
					//System.out.print(cnt + " ");
					dataSetValuesforTest.add(dataSetValuesforTestFull.get(cnt));
				}
			} else if (checkForOverFitting == 2) {
//				double ratio = 0;
//				if (!Constant.randomForestFlag)
//					ratio = Constant.trainingDataSetfactor;
//				else
//					ratio = Constant.randomTraingDataAccuracySetfactor;

				for (int i = 0; i < ML_TreeData.dataSetValues.size(); i++) {
//					int cnt = random.nextInt(ML_TreeData.dataSetValues.size() - 0) + 0;
					//System.out.print(cnt + " ");
					dataSetValuesforTest.add(ML_TreeData.dataSetValues.get(i));
				}
			}else {
				dataSetValuesforTest.addAll(ML_TreeData.dataSetValues);
			}

			System.out.println();
			// System.out.println("dataSetValuesforTest..." + dataSetValuesforTest.size());
			// System.out.println("dataSetValuesforTest..." + dataSetValuesforTest);

			System.out.println("ML_TreeData.finalResWithAttrNm...." + ML_TreeData.finalResWithAttrNm);
			System.out.println("ML_TreeData.attributeValForNextSrch...." + ML_TreeData.attributeValForNextSrch);

			if (ML_Tree.baseAccuracy == null) {
				Iterator<String> iterator = ML_TreeData.attributeValForNextSrch.keySet().iterator();

				while (iterator.hasNext()) {
					String nodeValues = iterator.next();
					if (nodeValues.contains("-")) {
						if (ML_TreeData.finalResWithAttrNm.keySet().contains(nodeValues)) {
							String[] rootAttrNms = ML_TreeData.finalResWithAttrNm.get(nodeValues).split("-");
							String[] rootAttrVals = nodeValues.split("-");

							int[] indArr = new int[rootAttrNms.length];
							String[] indVal = new String[rootAttrVals.length];

							// System.out.println(Arrays.toString(rootAttrNms));
							for (int i = 0; i < rootAttrNms.length; i++) {
								// System.out.println(ML_TreeData._attributetoInd.get(rootAttrNms[i]));
								// System.out.println(rootAttrVals[i]);

								if (Constant.resColumn != 0)
									indArr[i] = ML_TreeData._attributetoInd.get(rootAttrNms[i]) - 1;
								else
									indArr[i] = ML_TreeData._attributetoInd.get(rootAttrNms[i]);
								indVal[i] = rootAttrVals[i];
							}
							copyDataSetValuesforTest.addAll(dataSetValuesforTest);
							compareTheDataRes(indArr, indVal, dataSetValuesforTest, nodeValues);
						}

					} else {
						if (ML_TreeData.finalResWithAttrNm.containsKey(nodeValues)) {
							String rootAttrNms = ML_TreeData.finalResWithAttrNm.get(nodeValues);
							// System.out.println(ML_TreeData._attributetoInd.get(rootAttrNms));
							// System.out.println(nodeValues);

							int[] indArr = { ML_TreeData._attributetoInd.get(rootAttrNms) - 1 };

							if (Constant.resColumn != 0)
								indArr[0] = indArr[0] + 1;

							String[] indVal = { nodeValues };

							copyDataSetValuesforTest.addAll(dataSetValuesforTest);
							compareTheDataRes(indArr, indVal, dataSetValuesforTest, nodeValues);
						}
					}
				}

				// System.out.println("copyDataSetValuesforTest..."+copyDataSetValuesforTest.size());

				//count the  accuracy based on the count
				double accuracyPerc = 100;
				if (yCnt + nCnt > 0)
					accuracyPerc = (100 * yCnt) / (yCnt + nCnt);

				System.out.println("nCnt...." + nCnt + "....yCnt...." + yCnt);
				System.out.println(accuracyPerc + "%");

				if (Constant.randomForestExecute)
					Constant.randomForestFlag = true;

				return accuracyPerc + "%";
			} else {
				
				//count the  accuracy for  random forest

				System.out.println("ML_Tree.randomFinalResWithAttrNm..." + ML_Tree.randomFinalResWithAttrNm);
				System.out.println("ML_Tree.randomAttributeValForNextSrch..." + ML_Tree.randomAttributeValForNextSrch);

				List<int[]> indArrMain = new ArrayList<>();
				List<String[]> indValMain = new ArrayList<>();
				List<String> nodeValuesMain = new ArrayList<>();

				for (int j = 0; j < ML_Tree.randomFinalResWithAttrNm.size(); j++) {
					Iterator<String> iterator = ML_Tree.randomAttributeValForNextSrch.get(j).keySet().iterator();

					while (iterator.hasNext()) {
						String nodeValues = iterator.next();
						if (nodeValues.contains("-")) {
							if (ML_Tree.randomFinalResWithAttrNm.get(j).keySet().contains(nodeValues)) {
								String[] rootAttrNms = ML_TreeData.finalResWithAttrNm.get(nodeValues).split("-");
								String[] rootAttrVals = nodeValues.split("-");

								int[] indArr = new int[rootAttrNms.length];
								String[] indVal = new String[rootAttrVals.length];

								// System.out.println(Arrays.toString(rootAttrNms));
								for (int i = 0; i < rootAttrNms.length; i++) {
									// System.out.println(ML_TreeData._attributetoInd.get(rootAttrNms[i]));
									// System.out.println(rootAttrVals[i]);

									if (Constant.resColumn != 0)
										indArr[i] = ML_TreeData._attributetoInd.get(rootAttrNms[i]) - 1;
									else
										indArr[i] = ML_TreeData._attributetoInd.get(rootAttrNms[i]);
									indVal[i] = rootAttrVals[i];
								}
								copyDataSetValuesforTest.addAll(dataSetValuesforTest);
								indArrMain.add(indArr);
								indValMain.add(indVal);
								nodeValuesMain.add(nodeValues);
							}
						} else {
							if (ML_Tree.randomFinalResWithAttrNm.get(j).containsKey(nodeValues)) {
								String rootAttrNms = ML_Tree.randomFinalResWithAttrNm.get(j).get(nodeValues);
								// System.out.println(ML_TreeData._attributetoInd.get(rootAttrNms));
								// System.out.println(nodeValues);

								int[] indArr = { ML_TreeData._attributetoInd.get(rootAttrNms) - 1 };

								if (Constant.resColumn != 0)
									indArr[0] = indArr[0] + 1;

								String[] indVal = { nodeValues };

								copyDataSetValuesforTest.addAll(dataSetValuesforTest);
								indArrMain.add(indArr);
								indValMain.add(indVal);
								nodeValuesMain.add(nodeValues);
							}
						}
					}
				}

				compareTheDataResWithRandomForest(indArrMain, indValMain, dataSetValuesforTest, nodeValuesMain);
 
				//count the  accuracy based on the count
				double accuracyPerc = 100;
				if (yCnt + nCnt > 0)
					accuracyPerc = (100 * yCnt) / (yCnt + nCnt);

				System.out.println("nCnt...." + nCnt + "....yCnt...." + yCnt);
				System.out.println(accuracyPerc + "%");

				return accuracyPerc + "%";
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

	private void compareTheDataResWithRandomForest(List<int[]> indArrMain, List<String[]> indValMain,
			List<String[]> dataSetValuesforTest, List<String> nodeValuesMain) {

		System.out.println("indArrMain..." + indArrMain.size());
		System.out.println("indValMain..." + indValMain.size());
		System.out.println("nodeValuesMain..." + nodeValuesMain.size());

		// Fetch the tree resultset
		List<String[]> dataSetValuesforTestLocal = new ArrayList<>();
		dataSetValuesforTestLocal.addAll(dataSetValuesforTest);

		for (String[] currLine : dataSetValuesforTestLocal) {

			String keyVal = currLine[Constant.resColumn];
			int localYcnt = 0;
			int localNcnt = 0;

			for (int i = 0; i < indArrMain.size(); i++) {
				int[] indArr = indArrMain.get(i);
				String[] indVal = indValMain.get(i);
				boolean flag = true;

				// System.out.println("indArr..."+indArr.length);
				// System.out.println("indVal..."+indVal.length);
				// System.out.println("nodeValuesMain..."+nodeValuesMain.size());
				// System.out.println("currLine..."+currLine.length);

				for (int j = 0; j < indArr.length; j++) {
					if (!currLine[indArr[j]].equals(indVal[j])) {
						flag = false;
					}
				}

				if (flag) {
					dataSetValuesforTest.remove(currLine);
					if (!keyVal.equals(ML_TreeData.attributeValForNextSrch.get(nodeValuesMain.get(i)))) {
						// System.out.println("#################################################################");
						flag = false;
						localNcnt++;
					} else {
						localYcnt++;
					}
				}
			}

			if (localNcnt > localYcnt)
				nCnt++;
			else
				yCnt++;
		}

	}

	// Compare the data based on index
	private void compareTheDataRes(int[] indArr, String[] indVal, List<String[]> dataSetValuesforTest,
			String nodeValuesForVal) {

		// Fetch the tree resultset
		List<String[]> dataSetValuesforTestLocal = new ArrayList<>();
		dataSetValuesforTestLocal.addAll(dataSetValuesforTest);

		// System.out.println("Arrays.toString(indArr)........"+Arrays.toString(indArr));
		// System.out.println("Arrays.toString(indVal)........"+Arrays.toString(indVal));

		for (String[] currLine : dataSetValuesforTestLocal) {

			// System.out.println("--------------------------------"+Arrays.toString(currLine));

			String keyVal = currLine[Constant.resColumn];
			boolean flag = true;

			for (int i = 0; i < indArr.length; i++) {

				// System.out.println("indVal[i]...." + indVal[i] +
				// "....currLine[indArr[i]]...." + currLine[indArr[i]]);
				//
				// System.out.println(
				// "keyVal..." + keyVal +
				// "....ML_TreeData.attributeValForNextSrch.get(nodeValuesForVal)...."
				// + ML_TreeData.attributeValForNextSrch.get(nodeValuesForVal));

				if (!currLine[indArr[i]].equals(indVal[i])) {
					flag = false;
				}
			}

			if (flag) {
				dataSetValuesforTest.remove(currLine);
				if (!keyVal.equals(ML_TreeData.attributeValForNextSrch.get(nodeValuesForVal))) {
					// System.out.println("#################################################################");
					flag = false;
					nCnt++;
				} else {
					yCnt++;
				}
			}
		}
	}

}
