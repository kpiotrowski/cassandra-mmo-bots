import os
import json
import matplotlib.pyplot as plt

BASE_PATH_BOT = "./results_done/bot"
test_files = os.listdir(BASE_PATH_BOT)

bots_x = []
plot1 = {"bots_y_n": [], "bots_y_p": []}
plot2 = {"bots_y_n_t": [], "bots_y_n_r": [], "bots_y_p_t": [], "bots_y_p_r": []}

test_files = [int(x) for x in test_files]
test_files.sort()

for file_name in test_files:
    f_normal = f"{BASE_PATH_BOT}/{file_name}/results"
    f_partit = f"{BASE_PATH_BOT}/{file_name}/results_partition"
    bots_x.append(int(file_name))

    with open(f_normal, 'r') as f:
        data = json.load(f)
        plot1['bots_y_n'].append(data['collisions_count'])
        plot2['bots_y_n_t'].append(data['total_time'])
        plot2['bots_y_n_r'].append(data['real_time'])
    with open(f_partit, 'r') as f:
        data = json.load(f)
        plot1['bots_y_p'].append(data['collisions_count'])
        plot2['bots_y_p_t'].append(data['total_time'])
        plot2['bots_y_p_r'].append(data['real_time'])

plt.subplot(221)
plt.title("Number of collisions")
plt.plot(bots_x, plot1['bots_y_n'], 'rs--', bots_x, plot1['bots_y_p'], 'bs--')
plt.xlabel('App bots number')
plt.ylabel('Collisions')
plt.legend(['No partition', 'partition'])

plt.subplot(222)
plt.title("Collecting time")
plt.plot(bots_x, plot2['bots_y_n_t'], 'rs--', bots_x, plot2['bots_y_n_r'], 'bs--', bots_x, plot2['bots_y_p_t'], 'gs--', bots_x, plot2['bots_y_p_r'], 'ys--')
plt.xlabel('App bots number')
plt.ylabel('Collecting time')
plt.legend(['No partition - time', 'No partition - effective time', 'partition - time', 'partition - effective time'])





BASE_PATH_BOT = "./results_done/waitTime"
test_files = os.listdir(BASE_PATH_BOT)

bots_x = []
plot1 = {"bots_y_n": [], "bots_y_p": []}
plot2 = {"bots_y_n_t": [], "bots_y_n_r": [], "bots_y_p_t": [], "bots_y_p_r": []}

test_files = [float(x) for x in test_files]
test_files.sort()

for file_name in test_files:
    file_name = int(file_name) if file_name>=1 else file_name
    f_normal = f"{BASE_PATH_BOT}/{file_name}/results"
    f_partit = f"{BASE_PATH_BOT}/{file_name}/results_partition"
    bots_x.append(int(file_name))

    with open(f_normal, 'r') as f:
        data = json.load(f)
        plot1['bots_y_n'].append(data['collisions_count'])
        plot2['bots_y_n_t'].append(data['total_time'])
        plot2['bots_y_n_r'].append(data['real_time'])
    with open(f_partit, 'r') as f:
        data = json.load(f)
        plot1['bots_y_p'].append(data['collisions_count'])
        plot2['bots_y_p_t'].append(data['total_time'])
        plot2['bots_y_p_r'].append(data['real_time'])


plt.subplot(223)
plt.title("Number of collisions")
plt.plot(bots_x, plot1['bots_y_n'], 'rs--', bots_x, plot1['bots_y_p'], 'bs--')
plt.xlabel('Sleep time')
plt.ylabel('Collisions')
plt.legend(['No partition', 'partition'])


plt.subplot(224)
plt.title("Collecting time")
plt.plot(bots_x, plot2['bots_y_n_t'], 'rs--', bots_x, plot2['bots_y_n_r'], 'bs--', bots_x, plot2['bots_y_p_t'], 'gs--', bots_x, plot2['bots_y_p_r'], 'ys--')
plt.xlabel('Sleep time')
plt.ylabel('Collecting time')
plt.legend(['No partition - time', 'No partition - effective time', 'partition - time', 'partition - effective time'])


plt.show()