"""
 Second education python script. Working with lists.

 Created: Gusev Dmitrii, 08.03.2017
"""

print("Working with lists.")

# -- create a list and play with it
pets = ["dog", "cat", 'Mouse', 'Parrot', "rat", "coW"]
print("First list ->", pets)
pets.reverse()
print("First list reversed -> ", pets)
pets.reverse()

output = ""
for pet in pets:
    output = "[" + pet + "]" + "[" + pet.title() + "]" + \
             "[" + pet.upper() + "]" + "[" + pet.lower() + "]"
    print("pet in diff styles -> " + output)

# -- out parts of list
print(pets[0])
print(pets[-2])

# use list generator
print(["my pet: " + pet for pet in pets])

# -- modify the last element
pets[-1] = 'friend'
print("Modified -> " + str(pets))

# -- out in a cycle
for pet in pets:
    print("a pet -> " + pet)

# copy list in for cycle
nlist = []
for pet in pets:
    nlist.append(pet)
print("copied by FOR cycle -> " + str(nlist))

# insert into list
counter = 0
nlist2 = []
for pet in pets:
    nlist2.insert(counter, "->" + pet.upper() + "<-")
    counter += 1
print("!!!! -> " + str(nlist2))

# crete a copy of list
new_pets = pets[:]
print(new_pets)
new_pets[0] = "barlog"
print(pets)
print(new_pets)

# copy part of list
newest_pets = pets[-2:]
print(newest_pets)
newest_pets[-1] = "mickey mouse"
print(new_pets)
print(newest_pets)

# remove the last element
del newest_pets[-1]
print(newest_pets)
print(newest_pets.pop())
print(newest_pets)

# list of "powers of 2" generator
squares = [number ** 2 for number in range(1, 101)]
print(squares)
squares.sort(reverse=True)
print(squares)
print()

# print squares line by line on screen
counter = 1
out = ""
numbers_on_line = 20
for number in sorted(squares):
    out += str(number)

    # add semicolon
    if counter < len(squares):
        out += ", "

    # print middle result
    if counter % numbers_on_line == 0:
        print(out)
        out = ""

    counter += 1

print("Finished.")
