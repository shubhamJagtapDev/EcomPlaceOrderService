# Implement recommend frequently bought products functionality for an e-commerce platform

## Problem Statement

You are building an e-commerce platform. As a part of this system, you need to expose a functionality using which we can recommend users to buy products which are frequently bought together. For example, if a user is buying a mobile phone, then we can recommend the user to buy a mobile cover and a screen guard along with the mobile phone.

## Requirements

Our data science team has figured out frequently bought items using state of the art machine learning algorithms. They have provided us with this data in a table called as `product_group` represented by ProductGroup model. Each row in this table represents a group of products which are frequently bought together.
Now our task is to use this data and implement a functionality using which we can recommend users to buy products which are frequently bought together.
* The request will contain the product id which the user is going to buy.
* Using this query the product group table to find out list of distinct products that the user can buy along with the product which the user is going to buy.
* Return the list of products which the user can buy along with the given product.
* Eg. If the user is buying a mobile phone, and we have the following product groups: [mobile phone, cover], [mobile phone, tempered glass], [mobile phone, insurance], [mobile phone, tempered glass, cover], then we should return the following products: [cover, tempered glass, insurance].