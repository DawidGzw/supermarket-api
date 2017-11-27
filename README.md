# supermarket-api
1. Launch by comand: mvnw #(maven wrapper with default spring-boot:run task) 
2. Sawgger available at http://localhost:8080/swagger-ui.
3. Embedded H2 is used as DB. 
4. So far only one total price calculation strategy is available,
but the design is extensible and more complex strategies are relatively easy to add later.
Another strategies could calculate total price of all possible combinations of promotions for provided amount 
(for example: 3 products for 30.00, 4 products for 37.00, 5 products for 45.00. Possible combinations for 8 products are:
5,3 and 4,4. For first combination total price is 75.00 and for second it's 74.00)


