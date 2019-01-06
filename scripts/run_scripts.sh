#!/bin/bash


LOG=/tmp/init.log
echo "Initiating cassandra database" >> $LOG
cqlsh -f $CQL/drop_schema.cql

echo "Creating keyspace and inputing resources";
until cqlsh -f $CQL/create_schema.cql > /dev/null 2>&1 ; do echo "Couldn't create schema" | tee -a $LOG 1>&2; sleep 2; done
bash $CQL/restart.sh
# until cqlsh -f $CQL/truncate_tables.cql > /dev/null 2>&1 ; do echo "Couldn't truncate tables" | tee -a $LOG 1>&2; sleep 2; done
# until cqlsh -f $CQL/resources.cql > /dev/null 2>&1 ; do echo "Couldn't insert resources" | tee -a $LOG 1>&2; sleep 2; done
