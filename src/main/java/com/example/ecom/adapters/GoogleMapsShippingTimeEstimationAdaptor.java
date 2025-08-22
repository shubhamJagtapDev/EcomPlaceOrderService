package com.example.ecom.adapters;

import com.example.ecom.libraries.GoogleMapsApi;
import com.example.ecom.libraries.models.GLocation;
import com.example.ecom.models.Location;
import org.springframework.stereotype.Component;

@Component
public class GoogleMapsShippingTimeEstimationAdaptor implements ShippingTimeEstimationAdaptor{
    private GoogleMapsApi googleMapsApi;

    public GoogleMapsShippingTimeEstimationAdaptor() {
        this.googleMapsApi = new GoogleMapsApi();
    }

    @Override
    public int calculateEstimation(Location source, Location destination) {
        GLocation srcLocation = new GLocation();
        srcLocation.setLatitude(source.getLatitude());
        srcLocation.setLongitude(source.getLongitude());

        GLocation desLocation = new GLocation();
        desLocation.setLatitude(destination.getLatitude());
        desLocation.setLongitude(destination.getLongitude());
        return googleMapsApi.estimate(srcLocation, desLocation);
    }
}
