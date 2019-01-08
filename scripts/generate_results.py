from pprint import pprint

from cassandra.cqlengine import columns
from cassandra.cqlengine import connection
from cassandra.cqlengine.models import Model


class Logs(Model):
    botid = columns.Text(primary_key=True)
    start = columns.DateTime(primary_key=True)
    end = columns.DateTime(primary_key=True)
    place = columns.Text()


class Requests(Model):
    botid = columns.Text(primary_key=True)
    requests = columns.BigInt()


connection.setup(['10.0.0.3'], "mmobots")

# sync_table(Logs)
# sync_table(RequestsCount)

print(f"Total log items: {Logs.objects.count()}")
print(f"Total request items:{Requests.objects.count()}")

# ************************************** REQUESTS ***************************************** #
requests = {"summary": 0}
for instance in Requests.objects():
    requests[instance.botid] = int(instance.requests)
    requests["summary"] += requests[instance.botid]

print(f"Requests count: {requests['summary']}")

# *************************************** LOGS ******************************************** #
'''
{
    'place_id': {
        'bot_id': [
            {start: x, end: y}
        ]
    }
}
'''


def detect_collision(logs_list, logs_list_2):
    """

    :param logs_list:       List of the log objects {'start': x, 'end': y}
    :param logs_list_2:     List of the log objects {'start': x, 'end': y}
    :return: int int: overlapping time, number of collisions
    """
    ot, c = 0, 0
    for log_1 in logs_list:
        for log_2 in logs_list_2:
            if log_1['start'] >= log_2['end'] or log_2['start'] >= log_1['end']:
                continue

            elif log_1['start'] <= log_2['start'] < log_2['end'] <= log_1['end']:
                ot += log_2['end']-log_2['start']
            elif log_2['start'] <= log_1['start'] < log_1['end'] <= log_2['end']:
                ot += log_1['end']-log_1['start']
            elif log_2['start'] <= log_1['start'] < log_2['end']:
                ot += log_2['end'] - log_1['start']
            elif log_1['start'] <= log_2['start'] < log_1['end']:
                ot += log_1['end'] - log_2['start']

            # pprint({"msg": "Detected collision", "time_ranges": [log_1, log_2]})
            c += 1

    return ot, c


def count_total_time(logs_list):
    return sum([x['end'] - x['start'] for x in logs_list])


def calculate_place_logs(place_name, bot_logs):
    place_tt, place_t, place_c = 0, 0, 0
    checked = {}

    for bot_name, log_list in bot_logs.items():
        tt = count_total_time(log_list)
        place_tt += tt
        place_t += tt

        for bot_name_2, log_list_2 in bot_logs.items():
            if bot_name == bot_name_2 or bot_name_2 in checked:
                continue

            # print(f"\nChecking place {place_name}, bots: {bot_name} ; {bot_name_2}")
            ot, c = detect_collision(log_list, log_list_2)
            place_t -= ot
            place_c += c

        checked[bot_name] = True

    return place_tt, place_t, place_c


logs = {}
for instance in Logs.objects():
    if instance.place not in logs:
        logs[instance.place] = {}
    if instance.botid not in logs[instance.place]:
        logs[instance.place][instance.botid] = []
    logs[instance.place][instance.botid].append({'start': instance.start.timestamp(), 'end': instance.end.timestamp()})

total_time, time, collisions = 0, 0, 0
for key, value in logs.items():
    total_time_place, time_place, collisions_place = calculate_place_logs(key, value)
    total_time += total_time_place
    time += time_place
    collisions += collisions_place

print(f"Total collecting time: {total_time}")
print(f"Real collecting time: {time}")
print(f"Total number of collisions: {collisions}")
