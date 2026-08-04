# scripts/test_webcam.py
import cv2

CAM_INDEX = 0  # try 1 if 0 fails

cap = cv2.VideoCapture(CAM_INDEX, cv2.CAP_DSHOW)  # CAP_DSHOW is best on Windows
if not cap.isOpened():
    print("Cannot open webcam")
    raise SystemExit(1)

print("Webcam OK. Press Q to quit.")
while True:
    ok, frame = cap.read()
    if not ok:
        break
    cv2.imshow("Laptop Camera", frame)
    if cv2.waitKey(1) & 0xFF in (ord("q"), ord("Q")):
        break

cap.release()
cv2.destroyAllWindows()