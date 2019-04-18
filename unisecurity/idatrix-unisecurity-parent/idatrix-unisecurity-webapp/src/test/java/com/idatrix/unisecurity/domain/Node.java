package com.idatrix.unisecurity.domain;

import lombok.Data;

/**
 * @ClassName Node
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
public class Node {

    private Node leftChild;

    private Node rightChild;

    public void access() {
        System.out.println("access ............");
    }

}