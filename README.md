## Cube Range Sum calculator API

The approach taken to solve the problem of summing the intensities of a specific range of 
'voxels' (voxels are basically pixels but in 3D space) was by using the [Summed Area Table](https://en.wikipedia.org/wiki/Summed_area_table) method
and extrapolating it for the 3 dimensional case. This way we take advantage of the sums being
composed of other sums of parts of the cube to code up a recursive solution which is efficient
since it makes use of meoization to eliminate the need for redudant computations. Whenever a voxel
is updated, the other sums in the meoization cache that are affected by that change need to be invalidated 
and the previous sum is simply changed based on how the intensity in the voxel being updated changes. This
means that the computation is spread between the updates and the queries and regarding the queries 
only 8 array references need to be made to figure out the sum of the range.

* References to meoized function calls to compute sum of range

```scala
private def queryVector(x: Int, y: Int, z: Int, x2: Int, y2: Int, z2: Int): Long = {
      (sumOfVolume((x2, y2, z2)) - sumOfVolume((x2, y-1, z2)) - sumOfVolume((x-1, y2, z2)) -
        sumOfVolume((x2, y2, z-1)) + sumOfVolume((x-1, y-1, z2)) + sumOfVolume((x2, y-1, z-1)) +
        sumOfVolume((x-1, y2, z-1)) - sumOfVolume((x-1, y-1, z-1)))
  }
```

![Summed Volume Cube](https://cloud.githubusercontent.com/assets/1668681/23099050/aa0a678e-f62b-11e6-8c5b-92c4e5117946.JPG)

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

####Example queries:

#####Creating a Grid with dimensionLength 20

```
curl -X POST -H "Content-Type: application/json" -d '{
  "dimensionLength" : 20
}' "http://localhost:8081/grids"
```

```json
{
  "id": "68059693-e17a-4192-bc89-503da2d61087",
  "dimensionLength": 20
}
```

#####Updating intensity of 'voxel' of grid with id '68059693-e17a-4192-bc89-503da2d61087' in position (2,2,2) to 4

```
curl -X PATCH -H "Content-Type: application/json" '{
  "dimensionLength" : 20
}' "http://localhost:8081/grids/68059693-e17a-4192-bc89-503da2d61087/values?x=2&y=2&z=2&value=4"
```

```json
{
  "msg": "grid 68059693-e17a-4192-bc89-503da2d61087updated succesfuly"
}
```

##### Querying grid with id '68059693-e17a-4192-bc89-503da2d61087' for sum of intensities of 'voxels' in between (1,1,1) and (3,3,3) 

```
curl -X GET -H "Content-Type: application/json" "http://localhost:8081/grids/68059693-e17a-4192-bc89-503da2d61087/sums?x=1&y=1&z=1&x2=3&y2=3&z2=3"

```

```json
{
  "result": 4
}
```