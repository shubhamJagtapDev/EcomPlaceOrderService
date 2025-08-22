package com.example.ecom.adapters;

import com.example.ecom.models.Location;

public interface ShippingTimeEstimationAdaptor {
    int calculateEstimation(Location source, Location destination);
}
