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
cars.reverse()
print("reversed list ->", cars)

