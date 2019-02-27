#!/bin/sh

sysbench --test=cpu --cpu-max-prime=20000000 --num-threads=4 run