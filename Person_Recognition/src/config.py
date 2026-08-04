import os
from dotenv import load_dotenv

load_dotenv()

VIDEO_SOURCE = os.getenv("VIDEO_SOURCE", "webcam")  # webcam | rtsp
CAMERA_INDEX = int(os.getenv("CAMERA_INDEX", "0"))
RTSP_URL = os.getenv("RTSP_URL")
CAMERA_ID = os.getenv("CAMERA_ID", "laptop-cam")
MATCH_THRESHOLD = float(os.getenv("MATCH_THRESHOLD", "0.40"))
GALLERY_DIR = os.getenv("GALLERY_DIR", "data/gallery")
CROPS_DIR = os.getenv("CROPS_DIR", "data/crops")
DATABASE_URL = os.getenv("DATABASE_URL")