package com.ml.scu.project.id3;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ML_TreeData_operations {

	private static DecimalFormat df2 = new DecimalFormat(".###");
	private static Map<String, Integer> _totEntropy = new HashMap<>();
	Map<String, Double> _featureListInfoGain = new TreeMap<>();

	Map<String, List<Double>> featureListTotEntropyWithMultFact = new TreeMap<>();

	// Find the total entropy count given ArrayList
	Map<String, Integer> findTotEntropyCnt(List<String[]> lst) {

		Map<String, Integer> hm = new HashMap<>();

		for (int i = 0; i < lst.size(); i++) {
			String[] tmp = lst.get(i);
			// System.out.println(tmp.length+"..."+i);

			System.out.println(Arrays.toString(tmp));

			// if (tmp.length > Constant.resColumn) { //Dataset2
			if (hm.get(tmp[Constant.resColumn]) == null)
				hm.put(tmp[Constant.resColumn], 0);
			hm.put(tmp[Constant.resColumn], hm.get(tmp[Constant.resColumn]) + 1);
			// }
		}

		System.out.println(hm);

		_totEntropy.putAll(hm);
		return hm;
	}

	// get log2 value using base
	public static double log2(double num) {
		if (num == 0)
			return 0;
		else
			return (Math.log(num) / Math.log(2));
	}

	// Calculate the entropy value for the Map
	double calulateEntropy(Map<String, Integer> entropyMap) {

		double totSum = 0;
		double totEntropy = 0;

		Iterator<String> itr = entropyMap.keySet().iterator();
		while (itr.hasNext())
			totSum += entropyMap.get(itr.next());

		// System.out.println("totSum...**..."+totSum);
		Iterator<String> itr1 = entropyMap.keySet().iterator();
		while (itr1.hasNext()) {
			double local = entropyMap.get(itr1.next()) / totSum;
			totEntropy += local * log2(local);
		}

		return Double.parseDouble(df2.format(-totEntropy));
	}

	// Calculate the Information gain
	public Map<String, Double> calulateInformationGain(Double totEntropyCnt, List<String[]> dataSetValues) {

		int totCnt = (int) getTotFromMap(ML_TreeData_operations._totEntropy);
		System.out.println("totCnt...." + totCnt);

		Iterator<String> itr = ML_TreeData._featureSet.keySet().iterator();
		while (itr.hasNext()) {
			String tmp = itr.next();

			List<Double> attributeWiseEntropy = new ArrayList<>();
			List<Double> attributeWiseEntropyWithMultFact = new ArrayList<>();

			// get each attribute names
			if (!tmp.equals(Constant.Class_Name)) {

				List<String> attributeUniqueList = ML_TreeData._featureSet.get(tmp);

				int attributeInd = 0;
				
				System.out.println("tmp.."+tmp);
				System.out.println("ML_TreeData._attributetoInd..."+ML_TreeData._attributetoInd);
				
				if (Constant.resColumn != 0)
					attributeInd = ML_TreeData._attributetoInd.get(tmp) - 1;
				else
					attributeInd = ML_TreeData._attributetoInd.get(tmp);
				
				System.out.println("attributeInd..."+attributeInd);

				// Iterate over each unique value of attribute to get Distinct count
				for (String attrVal : attributeUniqueList) {
//					System.out.println("----------" + attrVal + "...." + attributeInd);

					// Inside intialization beacsue
					Map<String, Integer> hmLocal = new HashMap<>();

					// iterate the whole Dataset
					for (int i = 0; i < dataSetValues.size(); i++) {
						String[] local = dataSetValues.get(i);

//						System.out.println(Arrays.toString(local));

						// Compare the current attribute's value with dataset's value , based on index
						if (attrVal.equals(local[attributeInd])) {

							if (hmLocal.get(local[Constant.resColumn]) == null)
								hmLocal.put(local[Constant.resColumn], 0);

							hmLocal.put(local[Constant.resColumn], hmLocal.get(local[Constant.resColumn]) + 1);
						}
					}

//					System.out.println("hmLocal..." + hmLocal);

					// Calculate the entropy for each distinct values after iteration // for only
					// single attributes's distinct value
					Double localEntropy = calulateEntropy(hmLocal);
					attributeWiseEntropy.add(localEntropy);
					// System.out.println("attributeWiseEntropy..."+attributeWiseEntropy);

					// current distinct attribute values total
					int localTot = (int) getTotFromMap(hmLocal);

					// Entropy value with multipplying factor
					Double localEntropyWithFact = ((double) localTot / (double) totCnt) * (localEntropy);
					if (!localEntropyWithFact.isNaN())
						attributeWiseEntropyWithMultFact.add(Double.parseDouble(df2.format(localEntropyWithFact)));
				}

				// featureListTotEntropy.put(tmp, attributeWiseEntropy);
				featureListTotEntropyWithMultFact.put(tmp, attributeWiseEntropyWithMultFact);
			}

		}

		System.out.println("featureListTotEntropyWithMultFact..." + featureListTotEntropyWithMultFact);

		/* final calculation information gain (Subtraction) */
		Iterator<String> itrForGain = featureListTotEntropyWithMultFact.keySet().iterator();
		while (itrForGain.hasNext()) {
			String s1Local = itrForGain.next();
			List<Double> lst = featureListTotEntropyWithMultFact.get(s1Local);

			Double d11 = 0.0;
			for (Double d1 : lst) {
				d11 += d1;
			}

			_featureListInfoGain.put(s1Local, Double.parseDouble(df2.format(totEntropyCnt - d11)));
		}
		System.out.println("featureListTotGain....." + _featureListInfoGain);

		return _featureListInfoGain;
	}

	// Get count of all total values for specific attribute like (yes+no of High)
	private double getTotFromMap(Map<String, Integer> local) {
		double totCnt = 0;
		Iterator<String> itrLocal = local.keySet().iterator();
		while (itrLocal.hasNext())
			totCnt += local.get(itrLocal.next());
		return totCnt;
	}

}
