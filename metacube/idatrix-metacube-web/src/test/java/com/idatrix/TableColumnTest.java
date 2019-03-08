package com.idatrix;

import com.ys.idatrix.metacube.metamanage.domain.TableColumn;

import java.util.ArrayList;

/**
 * @ClassName ${name}
 * @Description TODO
 * @Author ouyang
 * @Date
 */
public class TableColumnTest {

    public static void test1() {
        TableColumn column1 = new TableColumn();
        TableColumn column2 = new TableColumn();
        column1.setId(1l);
        column1.setColumnName("cs1");
        column2.setId(2l);
        column2.setColumnName("cs3");

        ArrayList list1 = new ArrayList();
        list1.add(column1);
        list1.add(column2);

        TableColumn column3 = new TableColumn();
        TableColumn column4 = new TableColumn();
        column3.setId(1l);
        column3.setColumnName("cs1");
        column4.setId(2l);
        column4.setColumnName("cs2");

        ArrayList list2 = new ArrayList();
        list2.add(column3);
        list2.add(column4);

        list1.retainAll(list2);
        System.out.println(list1);
    }

    public static void main(String[] args) {
        test1();
    }

}