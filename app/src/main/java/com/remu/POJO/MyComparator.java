package com.remu.POJO;

import java.util.Comparator;

public class MyComparator implements Comparator<PlaceModel> {
    @Override
    public int compare(PlaceModel o1, PlaceModel o2) {
        if (o1.getPlaceWeight() > o2.getPlaceWeight()) {
            return -1;
        } else if (o1.getPlaceWeight() < o2.getPlaceWeight()) {
            return 1;
        }
        return 0;
    }
}
