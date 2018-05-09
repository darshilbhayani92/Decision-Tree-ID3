package com.ml.scu.project.id3;

import java.util.ArrayList;
import java.util.List;

public class ML_TreeNode {

	public ML_TreeNode parent;

	public List<String> children = new ArrayList<>(); // Children of the node
	// public List<TreeNode> childrenEdgaValue = new ArrayList<>(); // edge value
	// respected to the aforementioned Children

	public boolean isLeaf;
	public String name;

	public ML_TreeNode(String name, ML_TreeNode parent, List<String> child, String parentNodeVal, String leafVal) {

		this.name = name;
		this.parent = parent;

		// If it is the leaf of not
		if (child != null) {

			// If it is first time tree loading or not
			if (parent != null) {

				// creating the list for next tree tarversal
				if (ML_TreeData.attributeValForNextSrch.containsKey(parentNodeVal)) {
					for (String childVal : child)
						ML_TreeData.attributeValForNextSrch.put(parentNodeVal + "-" + childVal,
								ML_TreeData.attributeValForNextSrch.get(parentNodeVal) + "-" + name);

					ML_TreeData.attributeValForNextSrch.remove(parentNodeVal);
				}

			} else {
				this.children = child;
				for (String childVal : child) {
					ML_TreeData.attributeValForNextSrch.put(childVal, name);
				}
			}
		} else {

			//Data for the ID3 algortihm
			ML_TreeData.finalResWithAttrNm.put(parentNodeVal, ML_TreeData.attributeValForNextSrch.get(parentNodeVal));
			ML_TreeData.attributeValForNextSrch.put(parentNodeVal, leafVal);

			//Data for random forest 
			if (Constant.randomForestFlag) {
				ML_Tree.randomFinalResWithAttrNm.add(ML_TreeData.finalResWithAttrNm);
				ML_Tree.randomAttributeValForNextSrch.add(ML_TreeData.attributeValForNextSrch);
			}
		}

		System.out.println("ML_TreeData.attributeValForNextSrch......" + ML_TreeData.attributeValForNextSrch);
		System.out.println("ML_TreeData.finalResWithAttrNm....." + ML_TreeData.finalResWithAttrNm);

	}

}
