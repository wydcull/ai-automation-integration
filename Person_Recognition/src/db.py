from datetime import datetime, timezone
from sqlalchemy import create_engine, text
from src.config import DATABASE_URL

engine = create_engine(DATABASE_URL, pool_pre_ping=True)

def init_db():
    with engine.begin() as conn:
        conn.execute(text("""
            CREATE TABLE IF NOT EXISTS persons (
                id SERIAL PRIMARY KEY,
                name TEXT UNIQUE NOT NULL,
                created_at TIMESTAMPTZ DEFAULT NOW()
            )
        """))
        conn.execute(text("""
            CREATE TABLE IF NOT EXISTS sightings (
                id SERIAL PRIMARY KEY,
                person_name TEXT,
                camera_id TEXT NOT NULL,
                score REAL,
                crop_path TEXT,
                created_at TIMESTAMPTZ DEFAULT NOW()
            )
        """))

def add_person(name: str):
    with engine.begin() as conn:
        conn.execute(
            text("INSERT INTO persons(name) VALUES (:name) ON CONFLICT (name) DO NOTHING"),
            {"name": name},
        )

def add_sighting(person_name, camera_id, score, crop_path):
    with engine.begin() as conn:
        conn.execute(
            text("""
                INSERT INTO sightings(person_name, camera_id, score, crop_path, created_at)
                VALUES (:person_name, :camera_id, :score, :crop_path, :created_at)
            """),
            {
                "person_name": person_name,
                "camera_id": camera_id,
                "score": score,
                "crop_path": crop_path,
                "created_at": datetime.now(timezone.utc),
            },
        )

def recent_sightings(limit=20):
    with engine.connect() as conn:
        rows = conn.execute(
            text("""
                SELECT id, person_name, camera_id, score, crop_path, created_at
                FROM sightings
                ORDER BY created_at DESC
                LIMIT :limit
            """),
            {"limit": limit},
        ).mappings().all()
    return [dict(r) for r in rows]