#!/bin/sh
apt-get install xvfb libxrender1 libxtst6 libxi6 libgl1-mesa-dri
add-apt-repository ppa:webupd8team/java
apt-get update
apt-get install oracle-java8-installer
