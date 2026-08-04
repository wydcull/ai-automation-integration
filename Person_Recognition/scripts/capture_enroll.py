# scripts/capture_enroll.py
import os
import cv2

NAME = "Mayur"  # change this
out_dir = os.path.join("data", "gallery", NAME)
os.makedirs(out_dir, exist_ok=True)

cap = cv2.VideoCapture(0, cv2.CAP_DSHOW)
count = 0
print("Press SPACE to save photo, Q to quit")

while True:
    ok, frame = cap.read()
    if not ok:
        break
    cv2.imshow("Enroll", frame)
    key = cv2.waitKey(1) & 0xFF
    if key == ord(" "):
        path = os.path.join(out_dir, f"{count}.jpg")
        cv2.imwrite(path, frame)
        print("saved", path)
        count += 1
    elif key in (ord("q"), ord("Q")):
        break

cap.release()
cv2.destroyAllWindows()