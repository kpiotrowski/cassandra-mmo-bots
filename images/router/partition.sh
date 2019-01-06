#!/bin/bash
TABLE="Partitions"

until cqlsh 10.0.0.3 -f $DIR/start_partition.cql > /dev/null 2>&1 ; do echo "couldn't insert START time" | tee -a $LOG 1>&2; sleep 2; done
ip l s eth0 down
ip l s eth1 down

sleep $1

ip l s eth0 up
ip l s eth1 up

until cqlsh 10.0.0.3 -f $DIR/end_partition.cql > /dev/null 2>&1 ; do echo "couldn't insert END time" | tee -a $LOG 1>&2; sleep 2; done

