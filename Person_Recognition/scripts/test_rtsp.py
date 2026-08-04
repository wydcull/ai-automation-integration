import cv2
import sys
sys.path.append(".")
from src.config import RTSP_URL

cap = cv2.VideoCapture(RTSP_URL, cv2.CAP_FFMPEG)
if not cap.isOpened():
    print("Failed to open RTSP")
    raise SystemExit(1)

print("RTSP OK. Press Q to quit.")
while True:
    ok, frame = cap.read()
    if not ok:
        print("Frame grab failed, reconnecting...")
        cap.release()
        cap = cv2.VideoCapture(RTSP_URL, cv2.CAP_FFMPEG)
        continue

    # optional downscale for laptop
    frame = cv2.resize(frame, (960, 540))
    cv2.imshow("RTSP Test", frame)
    if cv2.waitKey(1) & 0xFF in (ord("q"), ord("Q")):
        break

cap.release()
cv2.destroyAllWindows()