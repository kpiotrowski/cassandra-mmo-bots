from random import randint

RESOURCE_PERCENTAGE = 2

print("USE MmoBots;")

for i in range(1, 99):
    for j in range(1, 99):
        if randint(1, 1000) > RESOURCE_PERCENTAGE*10:
            continue

        gold = randint(20, 100)
        print(f"INSERT INTO Resources (Id, PosX, PosY, GoldLeft) VALUES ('{str(i)}_{str(j)}', {i},  {j},  {gold});")