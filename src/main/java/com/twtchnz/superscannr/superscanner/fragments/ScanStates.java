package com.twtchnz.superscannr.superscanner.fragments;

enum ScanStates {
    NEW_ORDER(0), ORDER_NUMBER(1), MATERIAL_CODE(2), SALES_ORDER(3);

    private int state;

    private ScanStates(int state) {
        this.state = state;
    }

    public int getState() { return state; }
}
