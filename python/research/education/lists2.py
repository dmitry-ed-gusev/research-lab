"""
 Third educational program in python. Working with lists again.

 Created: Gusev Dmitrii, 09.03.2017
"""

# list (range) of numbers from 0 to 9
numbers = range(0, 10)
print("raw range ->", numbers)
numbers = list(numbers)
print("range converted to list -> " + str(numbers))

# list of cars and it's modifications
cars = ['bmw', 'audi', "ваз", "mercedes", "opel", "chevrolet"]
print("cars ->", cars)
cars.append("saab")
print("appended one car ->", cars)
cars.insert(0, "chrysler")
print("inserted at head ->", cars)
cars.insert(int(len(cars) / 2), "volvo")
print("inserted in the middle of list ->", cars)

# make a copy of list after adding some data
cars_copy = cars[:]

# removing data from list
del cars[-1]
print("deleted the last car ->", cars)
print("popped element ->", cars.pop(), "\nlist after pop() ->", cars)
print("pop() from the middle of the list ->", cars.pop(int(len(cars) / 2)), "\nlist after ->", cars)
cars.remove('volvo')
print("removed by value 'volvo' ->", cars)

car = 'opel'
if car in cars:
    cars.remove(car)
    print("removed car [" + str(car) + "], result list ->", cars)

# restore cars list from copy
cars = cars_copy[:]
print("list restored from copy ->", cars)
# iterate list in for cycle
for car in cars:
    print("current car:", car)

cars.reverse()
print("reversed list ->", cars)

# work with numbers (range) list
print("numbers ->", numbers)
new_range = list(range(0, 100, 3))
print("new numbers (step = 3) ->", new_range)

# create and out cubes list
cubes = []
for number in numbers:
    cubes.append(number ** 3)
print("cubes list ->", cubes)

# some statistics with numbers
print("max of cubes ->", max(cubes), "| min of cubes ->", min(cubes), "| sum of cubes ->", sum(cubes))

# list generator
print(["this is -> " + str(x) for x in range(2, 5)])
# generate list of squares
squares = [x ** 2 for x in range(0, 100, 3)]
print("squares list ->", squares)
