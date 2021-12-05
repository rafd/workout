#!/bin/bash


# find ./ -type f -name *.mp4 -exec towebm.sh {} \;

ffmpeg -i $1 -c vp9 -b:v 0 -crf 40 -vf scale=640:-1 $1.webm
