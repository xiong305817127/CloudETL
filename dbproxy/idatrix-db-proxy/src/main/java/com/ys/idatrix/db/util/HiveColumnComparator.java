package com.ys.idatrix.db.util;


import com.ys.idatrix.db.api.hive.dto.HiveColumn;

import java.util.Comparator;

public class HiveColumnComparator implements Comparator<HiveColumn> {

	@Override
	public int compare(HiveColumn arg0, HiveColumn arg1) {
		if (arg0.getPartitionOrder() == arg1.getPartitionOrder()) {
			return arg0.getColumnName().compareToIgnoreCase(arg0.getColumnName());
		} else {
			return arg0.getPartitionOrder() - arg1.getPartitionOrder();
		}
	}

}
