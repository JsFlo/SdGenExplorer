#!/bin/bash

source /home/bootloop/miniconda3/bin/activate ldm
python --version
python /home/bootloop/stable-diffusion/scripts/txt2img.py "$@"
