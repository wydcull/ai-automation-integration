import os
import time
import cv2

from src.camera import open_camera
from src.config import CAMERA_ID, CROPS_DIR
from src.config import VIDEO_SOURCE, RTSP_URL, CAMERA_INDEX, CAMERA_ID, CROPS_DIR
from src.gallery import create_face_app, build_gallery, match
from src.db import init_db, add_sighting

os.makedirs(CROPS_DIR, exist_ok=True)
init_db()

app = create_face_app()
gallery = build_gallery(app)
if not gallery:
    print("Gallery empty. Add photos under data/gallery/<Name>/")

cap = open_camera()
last_saved = {}  # name -> timestamp (avoid spam)

print("Running. Press Q to quit.")

while True:
    ok, frame = cap.read()
    if not ok:
        print("Reconnecting...")
        time.sleep(1)
        cap.release()
        cap = open_camera()  # webcam or RTSP, based on .env
        continue

#while True:
#    ok, frame = cap.read()
#    if not ok:
#        if VIDEO_SOURCE == "video":
#            print("Video finished.")
#            break
#        print("Reconnecting...")
#        time.sleep(1)
#        cap.release()
#        cap = open_camera()
#        continue

    frame = cv2.resize(frame, (960, 540))
    faces = app.get(frame)

    for face in faces:
        x1, y1, x2, y2 = map(int, face.bbox)
        name, score = match(gallery, face.embedding)

        color = (0, 255, 0) if name != "unknown" else (0, 0, 255)
        cv2.rectangle(frame, (x1, y1), (x2, y2), color, 2)
        cv2.putText(
            frame,
            f"{name} ({score:.2f})",
            (x1, max(0, y1 - 8)),
            cv2.FONT_HERSHEY_SIMPLEX,
            0.6,
            color,
            2,
        )

        # save at most once every 10s per identity
        now = time.time()
        key = name
        if now - last_saved.get(key, 0) > 10:
            crop = frame[max(0, y1):max(0, y2), max(0, x1):max(0, x2)]
            if crop.size > 0:
                path = os.path.join(CROPS_DIR, f"{int(now)}_{name}.jpg")
                cv2.imwrite(path, crop)
                add_sighting(name, CAMERA_ID, score, path)
                last_saved[key] = now

    cv2.imshow("Face Recognition", frame)
    if cv2.waitKey(1) & 0xFF in (ord("q"), ord("Q")):
        break

cap.release()
cv2.destroyAllWindows()