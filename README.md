Step 1:
Download the project from GitHub and open it in your development environment

Step 2:
Use Docker to run, use the docker compose up command to run the project

Step 3:
For a test request, use the curl command -X POST http://localhost:8080/api/v1/enrich -H "Content-Type: multipart/form-data; boundary=<calculated when request is sent>" -F "file=@src/test/resources/trade.csv"

Step 4:
Expected result:
data, product_name, currency, price
20160101,Treasury Bills Domestic,EUR,10.0
20160101,Corporate Bonds Domestic,EUR,20.1
20160101,REPO Domestic,EUR,30.34
20160101,Missing Product Name,EUR,35.34

With the following input data:

product.csv
product_id,product_name
1,Treasury Bills Domestic
2,Corporate Bonds Domestic
3,REPO Domestic
4,Interest rate swaps International
5,OTC Index Option
6,Currency Options
7,Reverse Repos International
8,REPO International
9,766A_CORP BD
10,766B_CORP BD

trade.csv
date,product_id,currency,price
20160101,1,EUR,10.0
20160101,2,EUR,20.1
20160101,3,EUR,30.34
20160101,11,EUR,35.34
