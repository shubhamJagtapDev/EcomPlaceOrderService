package com.example.ecom.services;

import com.example.ecom.adapters.ShippingTimeEstimationAdaptor;
import com.example.ecom.exceptions.AddressNotFoundException;
import com.example.ecom.exceptions.ProductNotFoundException;
import com.example.ecom.models.Address;
import com.example.ecom.models.DeliveryHub;
import com.example.ecom.models.Location;
import com.example.ecom.models.Product;
import com.example.ecom.repositories.AddressRepository;
import com.example.ecom.repositories.DeliveryHubRepository;
import com.example.ecom.repositories.ProductRepository;
import com.example.ecom.services.service_interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private ShippingTimeEstimationAdaptor shippingTimeEstimationAdaptor;
    private ProductRepository productRepository;
    private AddressRepository addressRepository;
    private DeliveryHubRepository deliveryHubRepository;

    @Autowired
    public ProductServiceImpl(ShippingTimeEstimationAdaptor shippingTimeEstimationAdaptor, ProductRepository productRepository, AddressRepository addressRepository, DeliveryHubRepository deliveryHubRepository) {
        this.shippingTimeEstimationAdaptor = shippingTimeEstimationAdaptor;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.deliveryHubRepository = deliveryHubRepository;
    }

    @Override
    public Date estimateDeliveryDate(int productId, int addressId) throws ProductNotFoundException,
            AddressNotFoundException
    {
        Optional<Product> productOptional = productRepository.findById(productId);
        if(productOptional.isEmpty()) {
            throw new ProductNotFoundException("Product does not exists : product_id " + productId);
        }
        Product productData = productOptional.get();

        Optional<Address> addressOptional = addressRepository.findById(addressId);
        if (addressOptional.isEmpty()) {
            throw new AddressNotFoundException("Invalid address");
        }
        Address userAddress = addressOptional.get();

        Optional<DeliveryHub> deliveryHubOptional = deliveryHubRepository
                                            .findDeliveryHubByAddress_ZipCode(userAddress.getZipCode());
        if (deliveryHubOptional.isEmpty()) {
            throw new AddressNotFoundException("Invalid deliveryHub address");
        }

        Location sellerLocation = new Location();
        sellerLocation.setLatitude(productData.getSeller().getAddress().getLatitude());
        sellerLocation.setLongitude(productData.getSeller().getAddress().getLongitude());

        Location userLocation = new Location();
        userLocation.setLongitude(userAddress.getLongitude());
        userLocation.setLatitude(userAddress.getLatitude());

        Location deliveryHubLocation = new Location();
        deliveryHubLocation.setLatitude(deliveryHubOptional.get().getAddress().getLatitude());
        deliveryHubLocation.setLongitude(deliveryHubOptional.get().getAddress().getLongitude());

        int sellerToDeliveryHubTime = shippingTimeEstimationAdaptor
                .calculateEstimation(sellerLocation, deliveryHubLocation);
        int deliveryHubToUserTime = shippingTimeEstimationAdaptor.calculateEstimation(deliveryHubLocation, userLocation);

        Date currentTime = new Date();
        Date estimatedShippingTime = new Date(currentTime.getTime() +
                (sellerToDeliveryHubTime+deliveryHubToUserTime)*1000L);

        return estimatedShippingTime;
    }
}
