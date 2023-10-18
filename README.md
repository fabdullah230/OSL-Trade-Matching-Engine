## OSL Matching Engine

This is a Java Spring implementation of the Matching Engine question described in the Technical Take Home Test document.

**Running the code:**   
For simplicity, it is best to clone the repo, build the project and run the application using IntelliJ IDEA.
Run the `OslTradeMatchingEngineApplication`class to start the application. The application will start running on port 8080.

**Requirements:**    
Zookeeper running on localhost:2181    
Kafka running on localhost:9092    
MySQL running on localhost:3036, create and use database 'trades'

**Testing the application:**   
Under the `simulation` package in the project directory, I have included a `Test` class with a main function. Run the main function to populate the Kafka 'orders' topic with orders, which will be picked up and proccessed by the matching engine which will output the result in one of the two output queues, 'trades' and 'orderbook'.

**Interacting with the application:**   
The `OrderMatchingEngineApi` exposes the following REST endpoints.

1. Create new order `POST  /api/new_order`    
   Request parameters:    
   instrument: *String* ("BTC", "ETH", "USDT")    
   side: *String* ("BUY", "SELL")  
   price: *double*  
   quantity: *int*  

2. Retrieve current orderbook of an instrument `GET /api/retrieve_orderbook`  
   Request parameters:  
   instrument: *String* ("BTC", "ETH", "USDT")  
         
3. Retrieve executed trades of an instrument `GET /api/get_executed_trades`   
   Request parameters:  
   instrument: *String* ("BTC", "ETH", "USDT")

4. Cancel an existing order `DELETE /api/cancel_existing_order`  
   Request parameters:  
   instrument: *String* ("BTC", "ETH", "USDT")  
   id: *String* (UUID)  
   side: *String* ("BUY", "SELL")  


**Control Flow Diagram**  
Here is a high level diagram of the order matching process. The user can introduce orders to the system through the API or the Kafka input queue.
![](https://github.com/fabdullah230/OSL-Trade-Matching-Engine/blob/main/OSL%20trade%20matching%20engine%20flow.png?raw=true)


