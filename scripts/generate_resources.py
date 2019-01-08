from random import randint

RESOURCE_PERCENTAGE = 15
MAP_SIZE = 20

print("USE MmoBots;")

for i in range(1, MAP_SIZE-1):
    for j in range(1, MAP_SIZE-1):
        if randint(1, 1000) > RESOURCE_PERCENTAGE*10:
            continue

        gold = randint(50, 200)
        print(f"INSERT INTO Places (Id, PosX, PosY, Type, Gold) VALUES ('{str(i)}_{str(j)}', {i},  {j},  'RESOURCE', "
              f"{gold});")

print(f"INSERT INTO Places (Id, PosX, PosY, Type, Gold) VALUES ('1', 0,  0,  'CITY', 0);")
print(f"INSERT INTO Places (Id, PosX, PosY, Type, Gold) VALUES ('2', {MAP_SIZE-1}, {MAP_SIZE-1}, 'CITY', 0);")
print(f"INSERT INTO Places (Id, PosX, PosY, Type, Gold) VALUES ('3', 0,  {MAP_SIZE-1}, 'CITY', 0);")
print(f"INSERT INTO Places (Id, PosX, PosY, Type, Gold) VALUES ('4', {MAP_SIZE-1}, 0,  'CITY', 0);")
