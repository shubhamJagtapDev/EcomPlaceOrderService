## Requirements
### The request to place an order will contain the following information:
* User id of the user who wants to place the order.
* List of product ids and their quantities (List>, the pair's key is the product id and value is the quantity) which the user wants to order.
* Address id of the user's address where the order needs to be delivered.
### Before creating an order we need to check the following things:
* Does the user exist in the system? If not then we need to throw an exception.
* Does the address exist in the system? If not then we need to throw an exception.
* Does the address belong to the user? If not then we need to throw an exception.
* Do all the products have enough quantity to fill the order? If not then we need to throw an exception.
* If all the above checks pass, then we need to update the inventory with the updated quantity, create an order (with status as placed) and return the order details.
1. We should handle for concurrent requests, i.e. we should not overbook the inventory. We should allow concurrent requests to place an order only if the inventory has enough quantity to fulfill the order.
2. Few products might be facing a lot of demand but their supply is limited eg. iPhones. For such products we will store max number of quantity per order that a user can order. Details for such products will be stored in the high_demand_products table.