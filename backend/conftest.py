import os

os.environ.setdefault("DJANGO_SECRET_KEY", "test-secret-key")
os.environ.setdefault("POSTGRES_PASSWORD", "test-password")
os.environ.setdefault("DJANGO_DEBUG", "true")
