package com.kms.demo.entity;


/**
 * @author matrixelement
 */
class NullNode extends Node {

    private final static NullNode NULL_NODE = new NullNode();

    @Override
    public boolean isNull() {
        return true;
    }

    private NullNode() {

    }

    public static NullNode getInstance() {
        return NULL_NODE;
    }

}
