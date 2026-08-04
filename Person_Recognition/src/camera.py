import cv2
from src.config import VIDEO_SOURCE, CAMERA_INDEX, RTSP_URL
def open_camera():
    if VIDEO_SOURCE == "webcam":
        # CAP_DSHOW avoids long open delays / black screen on many Windows laptops
        cap = cv2.VideoCapture(CAMERA_INDEX, cv2.CAP_DSHOW)
        if not cap.isOpened():
            raise RuntimeError(f"Cannot open webcam index {CAMERA_INDEX}")
        return cap

    if VIDEO_SOURCE == "rtsp":
        if not RTSP_URL:
            raise RuntimeError("RTSP_URL is empty")
        cap = cv2.VideoCapture(RTSP_URL, cv2.CAP_FFMPEG)
        if not cap.isOpened():
            raise RuntimeError("Cannot open RTSP URL")
        return cap

   # if VIDEO_SOURCE == "video":
   #    if not VIDEO_PATH:
   #         raise RuntimeError("VIDEO_PATH is empty")
   #     cap = cv2.VideoCapture(VIDEO_PATH)
   #     if not cap.isOpened():
   #        raise RuntimeError(f"Cannot open video file: {VIDEO_PATH}")
   #     return cap

    raise RuntimeError(f"Unknown VIDEO_SOURCE: {VIDEO_SOURCE}")