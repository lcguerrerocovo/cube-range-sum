## Cube Range Sum calculator API


### Running via console

To run the console to accept text input

```
sbt "runMain cubesum.Console"
```

wait until prompt and pass the input formated the following way

####Input Format 

The first line contains an integer T, the number of test-cases. T testcases follow. 

For each test case, the first line will contain two integers N and M separated by a single space. 

N defines the N * N * N matrix. 

M defines the number of operations. 

The next M lines will contain either

```
UPDATE x y z W
QUERY x1 y1 z1 x2 y2 z2 
```

####Sample Input

```
2
4 5
UPDATE 2 2 2 4
QUERY 1 1 1 3 3 3
UPDATE 1 1 1 23
QUERY 2 2 2 4 4 4
QUERY 1 1 1 3 3 3
2 4
UPDATE 2 2 2 1
QUERY 1 1 1 1 1 1
QUERY 1 1 1 2 2 2
QUERY 2 2 2 2 2 2

```

####Sample Output

```
Running time = 272588 ns
4
Running time = 211422 ns
4
Running time = 48705 ns
27
Running time = 55132 ns
0
Running time = 25198 ns
1
Running time = 165898 ns
1
```

The running time of each query performed will be shown prior to the result

### Running the Web Server

To run the web server using sbt

```
sbt run
```

Server will run on port **8081**
