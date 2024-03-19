#!/bin/bash

source /home/bootloop/miniconda3/bin/activate ldm
python --version
python /home/bootloop/stable-diffusion/scripts/img2img.py "$@"
