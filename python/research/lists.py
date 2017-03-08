"""
 Second education python script. Working with lists.

 Created: Gusev Dmitrii, 08.03.2017
"""

print("Working with lists.")

# -- create a list and play with it
pets = ["dog", "cat", 'mouse', 'parrot', "rat"]
print("First list ->", pets)

# -- out parts of list
print(pets[0])
print(pets[-2])

# -- modify the last element
pets[-1] = 'friend'
print("Modified -> " + str(pets))

# -- out in a cycle
for pet in pets:
    print("a pet -> " + pet)

print("Finished.")
