#!/bin/bash

sudo docker exec -it router bash -c "ip l s eth0 up && ip l s eth1 up"
sudo docker exec -it seed1 bash -c "cd \$CQL && bash restart.sh"
