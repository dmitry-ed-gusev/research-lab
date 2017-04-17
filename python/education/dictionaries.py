"""
 Working with dictionaries in Python.

 Created: Gusev Dmitrii, 13.03.2017
"""

print("Working with dictionaries is starting...")

# simple point and some operations
point = {'x': 10, 'y': 20}
print("point (dict) ->", point)
print("X coordinate ->", point['x'])
print("Y coordinate ->", point['y'])

# add color to the point
color = 'color'
point[color] = 'green'
print("point with added color ->", point)
print("point color ->", point[color])
# remove color
del point[color]
print("point again without color ->", point)

# add some new attributes to point and work with them
point[color] = 'red'
point['speed'] = "fast"
point['type'] = "fast and furious :)"

# iterate over point content
print("\nPrinting point in a cycle: ")
for key, value in point.items():
    print(key, "->", value)

print("\nOut only keys: ")
for key in point.keys():
    print("key ->", key, "(value for current key: " + str(point[key]) + ")")

# change point speed and out it (and do something more)
point['speed'] = "the fastest"
point['z_order'] = 10
print("Out only values: ")
for value in point.values():
    print("value ->", value)

print("Out point values without repeats: ")
for value in set(point.values()):
    print("value ->", value)

# generating points and putting them into list
points_range = list(range(0, 10))
points = []
for point_n in points_range:
    if point_n % 2 == 0:
        point_color = 'green'
    else:
        point_color = 'red'
    # creating one point
    point = {'x': point_n, 'y': point_n ** 2, 'color': point_color, 'id': 'point' + str(point_n)}
    # add point to list
    points.append(point)
print("List of points created! Size:", str(len(points)) + ".")

# iterate/print of points list
print("\nPoints output:")
counter = 1
for point in points:
    print("Point #{}: {}".format(counter, point))
    counter += 1

