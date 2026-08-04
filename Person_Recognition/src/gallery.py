import os
import cv2
import numpy as np
from insightface.app import FaceAnalysis
from src.config import GALLERY_DIR, MATCH_THRESHOLD

def create_face_app():
    app = FaceAnalysis(name="buffalo_s")  # safer on 4GB; try buffalo_l later
    app.prepare(ctx_id=0, det_size=(640, 640))  # ctx_id=0 => GPU; use -1 for CPU
    return app

def _cosine(a, b):
    a = a / (np.linalg.norm(a) + 1e-8)
    b = b / (np.linalg.norm(b) + 1e-8)
    return float(np.dot(a, b))

def build_gallery(app):
    gallery = []  # list of (name, embedding)
    if not os.path.isdir(GALLERY_DIR):
        return gallery

    for person in os.listdir(GALLERY_DIR):
        person_dir = os.path.join(GALLERY_DIR, person)
        if not os.path.isdir(person_dir):
            continue
        embs = []
        for fname in os.listdir(person_dir):
            path = os.path.join(person_dir, fname)
            img = cv2.imread(path)
            if img is None:
                continue
            faces = app.get(img)
            if not faces:
                print(f"No face: {path}")
                continue
            # largest face
            face = max(faces, key=lambda f: (f.bbox[2] - f.bbox[0]) * (f.bbox[3] - f.bbox[1]))
            embs.append(face.embedding)
        if embs:
            mean_emb = np.mean(np.stack(embs), axis=0)
            gallery.append((person, mean_emb))
            print(f"Enrolled: {person} ({len(embs)} images)")
    return gallery

def match(gallery, embedding):
    best_name, best_score = "unknown", -1.0
    for name, emb in gallery:
        score = _cosine(embedding, emb)
        if score > best_score:
            best_name, best_score = name, score
    if best_score < MATCH_THRESHOLD:
        return "unknown", best_score
    return best_name, best_score