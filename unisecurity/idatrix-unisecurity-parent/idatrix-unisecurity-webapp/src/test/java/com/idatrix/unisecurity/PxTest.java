package com.idatrix.unisecurity;

import com.idatrix.unisecurity.domain.Node;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName PxTest
 * @Description
 * @Author ouyang
 * @Date
 */
public class PxTest {

    @Test
    public void mppx() {
        int arr[] = new int[]{50, 10, 5, 100, 1};
        for (int i = 0; i < arr.length - 1; i++) {
            for (int l = i + 1; l < arr.length; l++) {
                if (arr[i] > arr[l]) {
                    int k = arr[i];
                    arr[i] = arr[l];
                    arr[l] = k;
                }
            }
        }

        for (int i : arr) {
            System.out.print(i + "\t");
        }
    }

    @Test
    public void xzpx() {
        /*String str = "ddd";
        String s = str.substring(0, str.length() - 1) + "l";
        System.out.println(s);*/
        Map<String, Integer> map = new HashMap<>();
        String str = "aabbbcccc";
        for (int i = 0; i < str.length(); i++) {
            String s = str.charAt(i) + "";
            Integer count = map.get(s);
            if(count == null) {
                count = 1;
            } else {
                count++;
            }
            map.put(s, count);
        }

        Set<String> keys = map.keySet();
        for (String key : keys) {
            System.out.println(key + "出现次数：" + map.get(key));
        }

        /*
        int arr[] = new int[]{50, 10, 5, 100, 1};
        for (int i = 0; i < arr.length - 1; i++) {
            int max_index = i;
            for (int l = i + 1; l < arr.length; l++) {
                if (arr[i] < arr[l]) {
                    max_index = l;
                }
            }
            if (max_index != i) {
                int k = arr[i];
                arr[i] = arr[max_index];
                arr[max_index] = k;
            }
        }

        for (int i : arr) {
            System.out.print(i + "\t");
        }*/
    }

    @Test
    public void crpx() {
        int arr[] = new int[]{50, 10, 5, 100, 1};
        int k;
        for (int i = 0; i < arr.length; i++) {
            int index = i;
            int value = arr[i];
            while (index > 0 && value < arr[index - 1]) {
                arr[index] = arr[--index];
            }
            arr[index] = value;
        }

        for (int i : arr) {
            System.out.print(i + "\t");
        }
    }

    @Test
    public void dg() {
        // 使用循环
        /*int sum = 0;
        for (int i = 1; i <= 100; i++) {
            sum += i;
        }
        System.out.println(sum);*/

        // 递归
        int sum = sum(1, 0);
        System.out.println(sum);
    }

    public int sum(int start, int sum) {
        if(start > 100) {
            return sum;
        }
        sum += start;
        return sum(++start, sum);
    }

    @Test
    public void nodeDg() {
        Node node = new Node();
        Node node_1 = new Node();
        Node node_2 = new Node();
        Node node_1_1 = new Node();
        node_1.setLeftChild(node_1_1);
        node.setLeftChild(node_1);
        node.setRightChild(node_2);
        access(node, 1);
    }

    public void access(Node node, int sum) {
        if(node == null) {
            return;
        }
        if(sum != 1) {
            node.access();
        }
        access(node.getLeftChild(), ++sum);
        access(node.getRightChild(), ++sum);
    }

}