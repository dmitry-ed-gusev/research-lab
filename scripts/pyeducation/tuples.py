"""
 Working with tuples in python.

 Created: Gusev Dmitrii, 13.03.2017
"""

print("Tuples working starting...")

# simple tuple
x = 0
y = 1
point = (10, 20)
print("point ->", point)
for coordinate in point:
    print("coordinate ->", coordinate)

# adding some complexity to tuple
point3D = (10, 20, 30)
print("3D point -> ", point3D)
if point[x] > 100:
    print("X greater than 100")
else:
    print("X less or equals to 100")

if point[y] < 10:
    print("Y less than 10")
elif point[y] < 100:
    print("Y less than 100 and greater or equals to 10")
else:
    print("Y greater or equals to 100")

